package service;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitQ {

    private static String proposalExchange = "TRAVEL_PROPOSALS";
    private static String intentExchange = "TRAVEL_INTENTS";
    public static String key = "Orchestrator";
    static Channel P_channel, I_channel;
    private static GsonBuilder builder;
    static Gson gson;

    //addresses of bindings, start as empty
    static String I_address = "";
    static String P_address = "";

    // To state if the bindings have been already created
    private static boolean connectionCreated = false;

    RabbitQ() {
        builder = new GsonBuilder();
        gson = builder.create();
        try {
            connect();
        } catch (IOException | TimeoutException e) {
            System.out.println(e);
        }
    }

    public void connect() throws IOException, TimeoutException {
        if (!connectionCreated) {
            ConnectionFactory factory = new ConnectionFactory();
            Connection connection = factory.newConnection();
            factory.setHost("localhost");
            //proposal channel
            P_channel = connection.createChannel();
            //intent channel
            I_channel = connection.createChannel();
            //both fanout exchanges
            P_channel.exchangeDeclare(proposalExchange, "fanout");
            I_channel.exchangeDeclare(intentExchange, "fanout");
            P_channel.queueDeclare("proposalsQ", true, false, false, null);
            String queueProposal = P_channel.queueDeclare().getQueue();
            P_address = queueProposal;
            P_channel.queueBind(queueProposal, proposalExchange, "");
            I_channel.queueDeclare("intentsQ", true, false, false, null);
            String queueIntent = I_channel.queueDeclare().getQueue();
            I_address = queueIntent;
            I_channel.queueBind(queueIntent, intentExchange, "");

            connectionCreated = true;

            // consuming content of both queues
            consumeProposal();
            consumeIntent();
        }
    }

    public boolean sendToQueue(String exchange, String message) throws IOException {
        Channel channel;
        // Check if the exchange name is valid
        if (!exchange.equals(proposalExchange) && !exchange.equals(intentExchange)) {
            return false;
        } else {
            // Check which exchange the message is sent to and store it in selected channel
            if (exchange.equals(proposalExchange)) {
                channel = P_channel;
            } else {
                channel = I_channel;
            }
            // publish message
            channel.basicPublish(exchange, message, null, message.getBytes("UTF-8"));
            return true;
        }
    }

    public boolean consumeIntent() {
        builder = new GsonBuilder();
        gson = builder.create();
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            TripIntent i = gson.fromJson(new String(delivery.getBody(), "UTF-8"), TripIntent.class);
            i.updateJSON(i.tripID, i.userID);
        };
        try {
            I_channel.basicConsume(I_address, true, deliverCallback, consumerTag -> {
            });
        } catch (IOException e) {
            System.out.println(e);
        }
        return true;
    }

    //consume proposal messages
    public boolean consumeProposal() throws IOException {
        builder = new GsonBuilder();
        gson = builder.create();
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            // Convert the message to a TripProposal object in order to access setWeather and storeIn
            TripProposal _proposal = gson.fromJson(new String(delivery.getBody(), "UTF-8"), TripProposal.class);
            //get lon and lat from the proposal
            int coords[] = _proposal.splitCoords(_proposal.getCoords());
            _proposal.setWeather(new WeatherHandler().getWeather(coords[0], coords[1]));
            _proposal.storeIn();
        };
        P_channel.basicConsume(P_address, true, deliverCallback, consumerTag -> {
        });
        return true;
    }
}
