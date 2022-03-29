package service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalTime;
import java.util.concurrent.TimeoutException;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("TripService")
public class Orchestrator {

    RabbitQ RabbitQ;
    FileReader FileReader;
    TripHandler TripHandler;
    IDHandler IDHandler;

    public Orchestrator() {
        FileReader = new FileReader();
        RabbitQ = new RabbitQ();
        try {
            RabbitQ.connect();
        } catch (IOException | TimeoutException e) {
            System.out.println(e);
        }
        TripHandler = new TripHandler();
        IDHandler = new IDHandler();
    }

    @Path("SendTripProposal")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String sendProposal(@QueryParam("title") String title,
            @QueryParam("creatorID") String creatorID,
            @QueryParam("coordinates (x,y)") String coords,
            @QueryParam("Date (dd-mm-yyyy") String date,
            @QueryParam("Time (hh:mm)") String time) throws IOException {

        // check if ID exists
        if (FileReader.checkID(creatorID)) {
            try {
                LocalTime.parse(time);
            } catch (Exception e) {
                return "<html>Invalid time</html>";
            }
            // send the trip proposal filled with user's input to the rabbitq
            RabbitQ.sendToQueue("TRAVEL_PROPOSALS", new TripProposal(title, coords, creatorID, time, date).sendProposal());
            return "<html>Proposal Sent</html>";
        } else {
            return "<html>Invalid data inserted</html>";
        }

    }

    @Path("ShowEveryTrip")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getTrips() {
        String everyTrip = "";
        try {
            everyTrip = TripHandler.getAllTrips();
        } catch (FileNotFoundException e) {
            System.out.println(e);
        }
        return everyTrip;
    }

    @Path("SendTripIntent")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String sendIntent(@QueryParam("tripID") String tripID, @QueryParam("userID") String userID) {
        if (FileReader.checkID(tripID)) {
            if (FileReader.checkID(userID)) {
                try {
                    //send a trip intent with the user's input data
                    RabbitQ.sendToQueue("TRAVEL_INTENTS", new TripIntent(tripID, userID).sendIntent());
                } catch (IOException e) {
                    System.out.println(e);
                }

                return "<html>intent sent</html>";
            } else {
                return "<html>Invalid user ID</html>";
            }
        } else {
            return "<html>Invalid trip ID</html>";
        }
    }

    @Path("MyTrips")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getMyTrips(@QueryParam("UserID") String userID) {
        String myTrips = TripHandler.userTrips(userID);
        return myTrips;
    }

    @Path("GetTripIntents")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getTripInterests(@QueryParam("TripID") String tripID) {
        return TripHandler.getTripIntents(tripID);
    }

    @Path("GetUserID")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getUserID() {
        String iD = IDHandler.generateUserID();
        return "<html>User ID: " + iD + "</html>";
    }

    @PUT
    @Consumes(MediaType.TEXT_HTML)
    public void putHtml(String content) {
    }
}
