package com.comp900019.MelbParking.DataManagement;

import android.util.Log;

import com.comp900019.MelbParking.ClusterableParkingSpot;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GetClosestSpot{
    private HashMap<ClusterableParkingSpot, HashMap<String, String>> distanceMap;
    private HashMap<ClusterableParkingSpot, HashMap<String, String>> durationMap;
    private final String LABEL = "closetbugs";
    private final String key = "AIzaSyAx-ypAZ2zU_07ABVZpbgZtXUzyU5zNnjY";
    private final DistanceCalculation.Mode mode= DistanceCalculation.Mode.WALKING; // calculating the duration by walking or driving or bicycling
    private final double mapSensitive = 0.003; // sensitive gives the around area
    private DistanceCalculation calculation;

//    public static HashMap<ClusterableParkingSpot, HashMap<String, String>> getRecommandFactory(final ArrayList<ClusterableParkingSpot> spots, final LatLng currentLocation, final int numberOfRecom, final int duration){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                GetClosestSpot closed = new GetClosestSpot();
//                closed.setRecommendSpot(spots, currentLocation, numberOfRecom, duration);
//
//            }
//        }).start();
//        return null;
//    }
    public GetClosestSpot(){
        calculation = new DistanceCalculation();
    }

    public void setRecommendSpot(ArrayList<ClusterableParkingSpot> spots, LatLng currentLocation, int numberOfRecom, int duration){
        HashMap<ClusterableParkingSpot, Double> distanceTemp = new HashMap<>();
        HashMap<ClusterableParkingSpot, Double> durationTemp = new HashMap<>();
        for(ClusterableParkingSpot spot: spots){
            Double latitude = spot.getPosition().latitude;
            Double longtitude = spot.getPosition().latitude;
            if((latitude>currentLocation.latitude+mapSensitive || latitude<currentLocation.latitude-mapSensitive) ||
                    (longtitude < currentLocation.longitude-mapSensitive || longtitude<currentLocation.longitude-mapSensitive)
                    || spot.getDuration()<duration){
                continue;
            }
            String url = setUrl(currentLocation, new LatLng(latitude, longtitude), mode);
            calculation.Calculate(url);
            HashMap<String, String> cal = calculation.getCalculation();
            distanceTemp.put(spot, Double.parseDouble(cal.get("distance").split(" ")[0]));
            durationTemp.put(spot, Double.parseDouble(cal.get("duration").split(" ")[0]));
        }
        distanceTemp = sortByValue(distanceTemp);
        durationTemp = sortByValue(durationTemp);

        distanceMap = getSmallesetKvalue("distance","duration",distanceTemp, durationTemp, numberOfRecom);
        durationMap = getSmallesetKvalue("duration","distance",durationTemp, distanceTemp, numberOfRecom);
    }
    public HashMap<String, String> getDistanceSpot(LatLng startPoint, LatLng endPoint, DistanceCalculation.Mode mode){
        String url = setUrl(startPoint, new LatLng(endPoint.latitude, endPoint.longitude), mode);
        calculation.Calculate(url);
        return calculation.getCalculation();
    }
    public HashMap<ClusterableParkingSpot, HashMap<String, String>> getDistanceRecom(){
        return this.distanceMap;
    }
    public HashMap<ClusterableParkingSpot, HashMap<String, String>> getDurationRecom(){
        return this.durationMap;
    }
    private HashMap<ClusterableParkingSpot, HashMap<String, String>> getSmallesetKvalue(String focusedKey, String notFocusedKey, HashMap<ClusterableParkingSpot, Double> focusedValue, HashMap<ClusterableParkingSpot, Double> notFocused, int num){
        HashMap<ClusterableParkingSpot, HashMap<String, String>> store = new HashMap<>();
        int count = 0;
        for(ClusterableParkingSpot spot: focusedValue.keySet()){
            HashMap<String, String> temp = new HashMap<>();
            temp.put(notFocusedKey, Double.toString(notFocused.get(spot)));
            temp.put(focusedKey,Double.toString(focusedValue.get(spot)));
            store.put(spot, temp);
            count++;
            if(count==num){
                break;
            }
        }
        return store;

    }
    private static HashMap<ClusterableParkingSpot, Double> sortByValue(HashMap<ClusterableParkingSpot, Double> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<ClusterableParkingSpot, Double> > list =
                new LinkedList<>(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<ClusterableParkingSpot, Double> >() {
            public int compare(Map.Entry<ClusterableParkingSpot, Double> o1,
                               Map.Entry<ClusterableParkingSpot, Double> o2)
            {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<ClusterableParkingSpot, Double> temp = new LinkedHashMap<>();
        for (Map.Entry<ClusterableParkingSpot, Double> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }
    private String setUrl(LatLng startLocation, LatLng endLocation, DistanceCalculation.Mode mode){
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        url.append("origin="+startLocation.latitude+","+startLocation.longitude);
        url.append("&destination="+endLocation.latitude+","+endLocation.longitude);
        url.append("&key="+key);
        switch (mode){
            case WALKING:
                url.append("&mode=walking");
                break;
            case BICYCLING:
                url.append("&mode=bicycling");
            case DRIVING:
                url.append("&mode=driving");
                break;
        }
        return url.toString();
    }
}