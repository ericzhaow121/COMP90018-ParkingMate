package com.comp900019.MelbParking;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class ClosestSpotFinder {
    public ClusterableParkingSpot findClosetParkingSpot(Location target, int minDuration, ArrayList<ClusterableParkingSpot> parkingSpaces) {
        ClusterableParkingSpot currentBest = null;
        Double currentBestDistance = null;

        Location spaceLoc = new Location("");
        for (ClusterableParkingSpot space : parkingSpaces) {
            LatLng spaceLocation = space.position;

            spaceLoc.setLatitude(spaceLocation.latitude);
            spaceLoc.setLongitude(spaceLocation.longitude);

            double distance = spaceLoc.distanceTo(target);

            if (space.getDuration() == null || space.getDuration() >= minDuration) {
                if (currentBestDistance == null || distance < currentBestDistance) {
                    currentBestDistance = distance;
                    currentBest = space;
                }
            }
        }

        return currentBest;
    }
}
