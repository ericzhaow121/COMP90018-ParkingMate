package com.example.carpark;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class DistanceCalculation{

    private HashMap<String, String> directionsList;
    private final String LABEL = "duration";
    enum Mode {WALKING, DRIVING, BICYCLING};
    //call this function first to calculate result
    public void Calculate(String url){
        Object object[] = new Object[1];
        object[0] = url;
        directionsList = parseData(this.doInBackground(object));
    }
    protected String doInBackground(Object... objects) {
        String url = (String)objects[0];
        String downloadedData = "";
        try {
            downloadedData = downloadURL(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return downloadedData;
    }
    //this method returns the calculation result; "duration": number, "distance": number
    public HashMap<String, String> getCalculation(){
        return directionsList;
    }
    private HashMap<String, String> parseData(String s){
        JSONArray jsonArray = null;
        JSONObject jsonObject ;
        try {
            jsonObject = new JSONObject(s);
            jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");
        }catch (Exception e){
            e.printStackTrace();
        }

        return getDuration(jsonArray);
    }
    private HashMap<String, String> getDuration(JSONArray jsonArray) {
        HashMap<String, String> googleDirectionsMap = new HashMap<>();
        String duration;
        String distance;

        try{
            duration = jsonArray.getJSONObject(0).getJSONObject("duration").getString("text");
            distance = jsonArray.getJSONObject(0).getJSONObject("distance").getString("text");

            googleDirectionsMap.put("duration",duration);
            googleDirectionsMap.put("distance",distance);

        }catch (Exception e){
            e.printStackTrace();
        }
        return  googleDirectionsMap;
    }
    private String downloadURL(String Myurl) throws IOException {
        String data = "";
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(Myurl);
            urlConnection=(HttpURLConnection) url.openConnection();
            urlConnection.connect();

            inputStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer sb = new StringBuffer();

            String line = "";
            while((line = br.readLine()) != null)
            {
                sb.append(line);
            }

            data = sb.toString();
            br.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            inputStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}
