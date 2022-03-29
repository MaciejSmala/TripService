package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class TripHandler {

    String getAllTrips() throws FileNotFoundException {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String output = "<html>";
        File json = new File("proposal.json");
        if (json.isFile()) {
            Scanner scanner = new Scanner(json);
            if (scanner.hasNext()) {
                //if proposal.json has entries then map them as a class into trips[]
                Trip[] trips = gson.fromJson(scanner.nextLine(), Trip[].class);
                //iterate through those trips and display them as an html object
                for (Trip trip : trips) {
                    output += "<h1>Title: " + trip.getTitle() + "</h1>";
                    output += "<ul><li> Coordinates: " + trip.getCoords() + "</li>"
                            + "<li> Date: " + trip.getDate() + "</li>"
                            + "<li> Time: " + trip.getTime() + "</li>"
                            + "<li> TripID: " + trip.getID() + "</li>"
                            + "<li> Weather: " + trip.getWeather() + "</li></ul>";
                }
            } else {
                //if the file exists but has no entries
                output += "No trips have been made yet";
            }
        } else {
            //if the file does not exist at all
            output += "No trips have been made yet";
        }
        output += "</html>";
        return output;
    }

    public String userTrips(String userID) {
        boolean foundTrip = false;
        Trip proposals[] = {};
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        try {
            File jsonFile = new File("proposal.json");
            if (jsonFile.isFile()) {
                try (Scanner scanner = new Scanner(jsonFile)) {
                    if (scanner.hasNext()) {
                        //if proposal.json exists and has entries then get them, map them as Trip class and store in _proposals[]
                        Trip _proposals[] = gson.fromJson(scanner.nextLine(), Trip[].class);
                        proposals = Arrays.copyOf(_proposals, _proposals.length);
                    }
                } catch (FileNotFoundException e) {
                    System.out.println(e);
                }
            }
        } catch (JsonSyntaxException e) {
            System.out.println(e);
        }

        String output = "<html>";
        //if entries in proposal.json were found
        if (proposals.length > 0) {
            for (Trip proposal : proposals) {
                for (int i = 0; i < proposal.interested.size(); i++) {
                    //looks throguh ALL the proposals but only selects the trips that match the usersID
                    if (proposal.interested.get(i).equals(userID.trim())) {
                        foundTrip = true;
                        output += "<h1>Title: " + proposal.getTitle() + "</h1>";
                        output += "<ul><li> Trip ID: " + proposal.getID() + "</li>"
                                + "<li> Date: " + proposal.getDate() + "</li>"
                                + "<li> Time: " + proposal.getTime() + "</li>"
                                + "<li> TripID: " + proposal.getID() + "</li>"
                                + "<li> Weather: " + proposal.getWeather() + "</li></ul>";
                    }
                }
            }
        }
        if (!foundTrip) {
            output += "You have no trips";
        }
        output += "</html>";
        return output;
    }

    //pretty much same thing as userTrips
    public String getTripIntents(String tripID) {
        String output = "<html>";
        boolean TripExists = false;
        Trip proposals[] = {};
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        try {
            File jsonFile = new File("proposal.json");
            if (jsonFile.isFile()) {
                try (Scanner scanner = new Scanner(jsonFile)) {
                    if (scanner.hasNext()) {
                        //if proposal.json exists and has entries then get them, map them as Trip class and store in _proposals[]
                        Trip _proposals[] = gson.fromJson(scanner.nextLine(), Trip[].class);
                        proposals = Arrays.copyOf(_proposals, _proposals.length);
                    }
                } catch (FileNotFoundException e) {
                    System.out.println(e);
                }
            }
        } catch (JsonSyntaxException e) {
            System.out.println(e);
        }

        for (Trip proposal : proposals) {
            if (!TripExists) {
                //looks throguh ALL the proposals but only selects the trips that match the usersID
                if (proposal.getID().equals(tripID)) {
                    output += "<h1>Interested users: </h1>";
                    TripExists = true;
                    for (int i = 0; i < proposal.interested.size(); i++) {
                        output += "<li>" + proposal.interested.get(i) + "</li>";
                    }
                }
            }
        }
        if (!TripExists) {
            output += "<p>No trip with ID: " + tripID + ".</p>";
        }
        output += "</html>";
        return output;
    }
}
