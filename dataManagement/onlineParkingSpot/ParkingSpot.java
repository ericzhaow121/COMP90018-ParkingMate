package com.example.carpark;


import com.google.gson.annotations.Expose;

public class ParkingSpot {
    @Expose private String bay_id;
    @Expose private String st_marker_id;
    @Expose private String status;
    @Expose private String lat;
    @Expose private String lon;

    public ParkingSpot(String bay_id, String st_marker_id, String state, String lat, String lon){
        this.bay_id = bay_id;
        this.st_marker_id = st_marker_id;
        this.status = state;
        this.lat = lat;
        this.lon = lon;
    }

    public String getBay_id() {
        return bay_id;
    }

    public String getSt_marker_id() {
        return st_marker_id;
    }

    public String getState() {
        return status;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }
}