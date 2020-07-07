package com.comp900019.MelbParking.DataManagement;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RetrieveAddress {
    private String key = "AIzaSyAx-ypAZ2zU_07ABVZpbgZtXUzyU5zNnjY";

    public Address getAddress(LatLng target) throws IOException {
        StringBuilder myUrl = new StringBuilder("https://maps.googleapis.com/maps/api/geocode/json?latlng=");
        myUrl.append(target.latitude+","+target.longitude);
        myUrl.append("&key="+key);
        String result = downloadURL(myUrl.toString());
        Address address = parseData(result);
        return address;
    }
    private Address parseData(String s){
        String pureAddress;
        JSONObject jsonObject ;
        Address address = null;
        try {
            jsonObject = new JSONObject(s);
            pureAddress = jsonObject.getJSONArray("results").getJSONObject(0).getString("formatted_address");
            String temp[] = pureAddress.split(", ");
            address = new Address(temp[0], temp[1]);
        }catch (Exception e){
            e.printStackTrace();
        }

        return address;
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
