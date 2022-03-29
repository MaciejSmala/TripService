package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class IDHandler {
    private static HttpURLConnection con;
    
    public String generateRandomNumber() throws IOException {
        BufferedReader reader;
        String temp;
        String randNum = "";
        try {
            URL url = new URL("https://www.random.org/integers/?num=1&min=1&max=10000&col=1&base=10&format=plain&rnd=new");
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            //unlike 7timer, random.org simply gives one number
            if (con.getResponseCode() < 299) {
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                while ((temp = reader.readLine()) != null) {
                    randNum = temp;
                }
            reader.close();
            }
        }finally {
            con.disconnect();
        }
        return randNum;
    }

    public String generateTripID(){
        String tripID = "tripID";
        try {
            tripID += generateRandomNumber();
            if (FileReader.checkID(tripID)) {
                tripID = generateTripID();
            } else {
                WriteToTxtFiles.writeDatabase(tripID);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        return tripID;
    }

    public String generateUserID(){
        String userID = "userID";
        try {
            userID += generateRandomNumber();
            if (FileReader.checkID(userID)) {
                userID = generateUserID();
            } else {
                WriteToTxtFiles.writeDatabase(userID);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        return userID;
    }
}
