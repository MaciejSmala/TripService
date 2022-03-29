package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import java.util.ArrayList;
import org.json.JSONObject;

public class WeatherHandler {

    private HttpURLConnection con;
    private StringBuffer responseContent = new StringBuffer();

    public String getTemperature() {
        return ("average temperature: " + readWeather("temp2m"));
    }

    public String getHumidity() {
        int avg = readWeather("rh2m");
        String result = "";
        switch (avg) {
            case -4:
            case -3:
            case -2:
            case -1:
            case 0:
                result = "very small";
                break;
            case 1:
            case 2:
            case 3:
            case 4:
                result = "small";
                break;
            case 5:
            case 6:
            case 7:
            case 8:
                result = "average";
                break;
            case 9:
            case 10:
            case 11:
            case 12:
                result = "big";
                break;
            case 13:
            case 14:
            case 15:
            case 16:
                result = "very big";
                break;
        }
        return ("average Humidity: " + result);
    }

    public String getCloudcover() {
        int avg = readWeather("cloudcover");
        String result = "";
        switch (avg) {
            case 1:
            case 2:
            case 3:
                result = "small";
                break;
            case 4:
            case 5:
            case 6:
                result = "medium";
                break;
            case 7:
            case 8:
            case 9:
                result = "big";
                break;
        }
        return ("average Cloudcover: " + result);
    }

    public int readWeather(String JSONname) {
        //accessing json file that has an object that has an array
        JSONObject jsonObj = new JSONObject(responseContent.toString());
        JSONArray ja_data = jsonObj.getJSONArray("dataseries");
        ArrayList<Integer> arrayL = new ArrayList<>();
        for (int j = 0; j < ja_data.length(); j++) {
            JSONObject json = ja_data.getJSONObject(j);
            arrayL.add(json.getInt(JSONname));
        }
        //7timer gives 24 values for the whole day. For simplification we just take the average of that
        int total = 0;
        int avg = 0;
        for (int i = 0; i < arrayL.size(); i++) {
            total = total + arrayL.get(i);
            avg = total / arrayL.size();
        }
        return avg;

    }

    public String getWeather(long lon, long lat) throws IOException {
        BufferedReader reader;
        String line;
        try {
            URL url = new URL("http://www.7timer.info/bin/astro.php?lon=" + lon + "&lat=" + lat + "&product=astro&output=json&tzshift=0");
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            if (con.getResponseCode() < 299) {
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
                reader.close();
            }
        } finally {
            con.disconnect();
        }
        return "The current weather in this location is: " + getTemperature() + ", " + getHumidity() + ", " + getCloudcover();
    }
}
