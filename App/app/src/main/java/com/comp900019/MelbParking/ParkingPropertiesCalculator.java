package com.comp900019.MelbParking;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class ParkingPropertiesCalculator {
    private static final double GPOlat = -37.81384;
    private static final double GPOlon = 144.963028;

    private static final double innerCityRate = 7;
    private static final double outerCityRate = 4;

    private static final Location GPO = new Location("");

    public ParkingPropertiesCalculator() {
        GPO.setLatitude(GPOlat);
        GPO.setLongitude(GPOlon);
    }

    public float distanceBetweenTwoSpaces(Location location1, Location location2) {
        return location1.distanceTo(location2);
    }

    public double parkingCost(double latitiude, double longtitude) {
        Location parkingSpace = new Location("");
        parkingSpace.setLongitude(longtitude);
        parkingSpace.setLatitude(latitiude);

        float distanceFromGPO = distanceBetweenTwoSpaces(parkingSpace, GPO);

        if (distanceFromGPO <= 1000) {
            // Inside Inner city rate
            return innerCityRate;
        } else {
            return outerCityRate;
        }
    }
}
