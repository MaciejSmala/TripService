package service;

import java.util.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TripProposal {

    private String title, tripID, coords, creatorID, time, date;
    String weather = "weather...";
    private IDHandler IDHandler;
    private GsonBuilder builder;
    Gson gson;

    public TripProposal(String title, String coords, String creatorID,
            String time, String date) {
        this.title = title;
        this.coords = coords;
        this.date = date;
        this.time = time;
        this.creatorID = creatorID;
        IDHandler = new IDHandler();
        builder = new GsonBuilder();
        gson = builder.create();
        this.tripID = IDHandler.generateTripID();

    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getTripID() {
        return tripID;
    }

    public String getCoords() {
        return coords;
    }

    public String sendProposal() {
        //send the proposal to json
        String jsonMessage = gson.toJson(new Trip(this.tripID, this.creatorID, this.title, this.coords, this.date, this.time, this.weather));
        return jsonMessage;
    }

    public int[] splitCoords(String rawCoordinates) {
        rawCoordinates = rawCoordinates.trim();
        String[] chars;
        int[] coordinates = {0, 0};
        if (rawCoordinates.contains(",")) {
            try {
                chars = rawCoordinates.split(",");
                coordinates[0] = Integer.parseInt(chars[0]);
                coordinates[1] = Integer.parseInt(chars[1]);
            } catch (NumberFormatException e) {
                System.out.println(e);
            }
        }
        return coordinates;
    }

    public boolean storeIn() {
        try {
            // Check for existing JSON files and read contents 
            // and store in a linked list
            builder = new GsonBuilder();
            gson = builder.create();
            LinkedList proposals = new LinkedList();
            File jsonFile = new File("proposal.json");
            if (jsonFile.isFile()) {
                try (Scanner reader = new Scanner(jsonFile)) {
                    if (reader.hasNext()) {
                        String content = reader.nextLine();
                        //stuff from proposal.json is mapped as a class into the _proposals[]
                        Trip _proposals[] = gson.fromJson(content, Trip[].class);
                        proposals.addAll(Arrays.asList(_proposals));
                    }
                }
            }
            // if the file has not been found then create a new one
            proposals.add(new Trip(this.tripID, this.creatorID, this.title, this.coords, this.date, this.time, this.weather));
            // Store the array of proposals as JSON
            try (FileWriter fw = new FileWriter("proposal.json")) {
                // Store the array of proposals as JSON
                fw.write(gson.toJson(proposals));
                fw.flush();
                fw.close();
            }
        } catch (JsonSyntaxException | IOException e) {
            System.out.println(e);
            return false;
        }
        return true;
    }
}
//TripProposal class could have been used if not for the fact that the conversion of class would also convert things like gson
//therefore just creating another class with only selected data is the approach taken

class Trip {

    private String title, tripID, coords, creatorID, time, date, weather;
    LinkedList interested;

    public Trip(String tripID, String creatorID, String title, String coords,
            String date, String time, String weather) {
        this.tripID = tripID;
        this.creatorID = creatorID;
        this.title = title;
        this.coords = coords;
        this.date = date;
        this.time = time;
        this.weather = weather;
        interested = new LinkedList();
    }

    public String getID() {
        return tripID;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getCoords() {
        return coords;
    }

    public String getWeather() {
        return weather;
    }

    public String getCreatorID() {
        return creatorID;
    }
}
