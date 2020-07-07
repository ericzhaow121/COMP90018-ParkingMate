package com.comp900019.MelbParking.DataManagement;


import com.google.gson.annotations.Expose;

public class ParkingSpot {
    @Expose private String bay_id;
    @Expose private String st_marker_id;
    @Expose private String status;
    @Expose private double lat;
    @Expose private double lon;

    public ParkingSpot(String bay_id, String st_marker_id, String state, Double lat, Double lon){
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

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
}