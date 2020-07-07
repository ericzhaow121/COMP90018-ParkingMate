package com.example.carpark;

import com.google.android.gms.maps.model.LatLng;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GetClosestSpot{
    private HashMap<ParkingSpot, HashMap<String, String>> distanceMap;
    private HashMap<ParkingSpot, HashMap<String, String>> durationMap;
    private final String LABEL = "closetbugs";
    private final String key = "AIzaSyAx-ypAZ2zU_07ABVZpbgZtXUzyU5zNnjY";
    private final DistanceCalculation.Mode mode= DistanceCalculation.Mode.WALKING; // calculating the duration by walking or driving or bicycling
    private final double mapSensitive = 0.002; // sensitive gives the around area
    private DistanceCalculation calculation;

    public GetClosestSpot(){
        calculation = new DistanceCalculation();
    }

    public void getRecommendSpot(List<ParkingSpot> spots, LatLng currentLocation, int numberOfRecom){
        HashMap<ParkingSpot, Double> distanceTemp = new HashMap<>();
        HashMap<ParkingSpot, Double> durationTemp = new HashMap<>();

        for(ParkingSpot spot: spots){
            Double latitude = Double.parseDouble(spot.getLat());
            Double longtitude = Double.parseDouble(spot.getLon());
            if((latitude>currentLocation.latitude+mapSensitive || latitude<currentLocation.latitude-mapSensitive) ||
                    (longtitude < currentLocation.longitude-mapSensitive || longtitude<currentLocation.longitude-mapSensitive)||
                    !spot.getState().equals("Unoccupied")){
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
    public HashMap<ParkingSpot, HashMap<String, String>> getDistanceSpot(LatLng startPoint, ParkingSpot ps, DistanceCalculation.Mode mode){
        String url = setUrl(startPoint, new LatLng(Double.parseDouble(ps.getLat()), Double.parseDouble(ps.getLon())), mode);
        calculation.Calculate(url);
        HashMap<ParkingSpot, HashMap<String, String>> result = new HashMap<>();
        result.put(ps, calculation.getCalculation());
        return result;
    }
    public HashMap<ParkingSpot, HashMap<String, String>> getDistanceRecom(){
        return this.distanceMap;
    }
    public HashMap<ParkingSpot, HashMap<String, String>> getDurationMap(){
        return this.durationMap;
    }
    private HashMap<ParkingSpot, HashMap<String, String>> getSmallesetKvalue(String focusedKey, String notFocusedKey, HashMap<ParkingSpot, Double> focusedValue, HashMap<ParkingSpot, Double> notFocused, int num){
        HashMap<ParkingSpot, HashMap<String, String>> store = new HashMap<>();
        int count = 0;
        for(ParkingSpot spot: focusedValue.keySet()){
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
    private static HashMap<ParkingSpot, Double> sortByValue(HashMap<ParkingSpot, Double> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<ParkingSpot, Double> > list =
                new LinkedList<>(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<ParkingSpot, Double> >() {
            public int compare(Map.Entry<ParkingSpot, Double> o1,
                               Map.Entry<ParkingSpot, Double> o2)
            {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<ParkingSpot, Double> temp = new LinkedHashMap<>();
        for (Map.Entry<ParkingSpot, Double> aa : list) {
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
