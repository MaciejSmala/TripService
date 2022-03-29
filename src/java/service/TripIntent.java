package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class TripIntent {

    public String userID, tripID;
    private GsonBuilder builder;
    Gson gson;

    TripIntent(String tripID, String userID) {
        this.userID = userID;
        this.tripID = tripID;
        builder = new GsonBuilder();
        gson = builder.create();

    }

    public boolean updateJSON(String _tripID, String _interested) {
        Trip proposals[] = {};
        builder = new GsonBuilder();
        gson = builder.create();
        try {
            File file = new File("proposal.json");
            if (file.isFile()) {
                try (Scanner reader = new Scanner(file)) {
                    if (reader.hasNext()) {
                        //if proposal.json exists and has entries then read them and map them to trip class into _proposals[]
                        Trip _proposals[] = gson.fromJson(reader.nextLine(), Trip[].class);
                        proposals = Arrays.copyOf(_proposals, _proposals.length);
                    }
                } catch (FileNotFoundException e) {
                    System.out.println(e);
                }
            }
        } catch (JsonSyntaxException e) {
            System.out.println(e);
        }
        //if proposals has entries
        if (proposals.length > 0) {
            for (Trip proposal : proposals) {
                if (proposal.getID().trim().equals(_tripID.trim())) {
                    proposal.interested.add(_interested);
                }
            }
            try {
                try (FileWriter fw = new FileWriter("proposal.json")) {
                    fw.write(gson.toJson(proposals));
                    fw.flush();
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
        return true;
    }

    public Trip[] addIntentToTrip(Trip[] trips, String tripID, String userID) {
        for (Trip trip : trips) {
            if (trip.getID().equals(tripID)) {
                trip.interested.add(userID);
            }
        }
        return trips;
    }

    public String sendIntent() {
        String jsonMessage = gson.toJson(new Intent(tripID, userID));
        return jsonMessage;
    }
}
//same thing as with the Trip class. this is here simply for the gson and builder
//to not cause trouble when mapping data from classes

class Intent {

    String tripID, userID;

    Intent(String tripID, String userID) {
        this.tripID = tripID;
        this.userID = userID;
    }
}
