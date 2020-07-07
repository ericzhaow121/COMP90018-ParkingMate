package com.comp900019.MelbParking.DataManagement;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DataManagement {
    private URL url;
    private List<ParkingSpot> ps;
    private static DataManagement rd;
    private DatabaseHelper dataHelper;

    private DataManagement(DatabaseHelper helper){
        this.dataHelper = helper;
    }
    public static DataManagement getInstance(DatabaseHelper helper){
        // getInstance method could prevent multiple creation
        if(rd == null){
           rd = new DataManagement(helper);
        }
        return rd;
    }
    public void updatingData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    connect();
                    try {
                        TimeUnit.MINUTES.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    private void connect(){
        Log.d("DATA", "Getting New Data");
        // using this method to connect online data and download to database
        try {
            Type type = TypeToken.getParameterized(List.class, ParkingSpot.class).getType();
            url = new URL("https://data.melbourne.vic.gov.au/resource/vh2v-4nfs.json?$limit=5000");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if (connection.getResponseCode() == 200) {
                InputStream input = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(input));
                //only looking for variables with label
                Gson g = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                ps = new ArrayList();
                ps = g.fromJson(br, type);
                updateBackgroundData(ps);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void updateBackgroundData(List<ParkingSpot> ps) {
        //Store data in SQLite
        long startTime = System.currentTimeMillis();
        long endTime = 0;
//        dataHelper.refreshDatabase();
        if(dataHelper.isDatabaseExist()){
            for(ParkingSpot temp: ps){
                dataHelper.update(Integer.parseInt(temp.getBay_id()),temp.getSt_marker_id(),
                        temp.getState(),temp.getLat(),temp.getLon());
                ParkingSpot test = dataHelper.search(temp.getBay_id());

            }
        }else{
            for(ParkingSpot temp: ps){
                dataHelper.insert(Integer.parseInt(temp.getBay_id()),temp.getSt_marker_id(),
                        temp.getState(),temp.getLat(),temp.getLon());
                ParkingSpot test = dataHelper.search(temp.getBay_id());
            }
        }
        endTime = System.currentTimeMillis();
        long timeneeded =  ((startTime - endTime) /1000);
        Log.i("runtime", "done: "+timeneeded+"s");
    }
    public List<ParkingSpot> retriveAllData(){
        return dataHelper.queryAll();
    }
    public ParkingSpot retriveData(String bay_id){
        return dataHelper.search(bay_id);
    }
}
