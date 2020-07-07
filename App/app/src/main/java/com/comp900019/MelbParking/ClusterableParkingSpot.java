package com.comp900019.MelbParking;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ClusterableParkingSpot implements ClusterItem {
    LatLng position;
    private String title;

    private Integer duration;

    public ClusterableParkingSpot(SpaceWithRestriction space) {
        position = new LatLng(space.space.latitude, space.space.longitude);
        title = Integer.toString(space.space.bayID);
        AllowedDurationCalculator adc = new AllowedDurationCalculator();
        duration = adc.getCurrentInterval(space);

    }

    public Integer getDuration () {
        return duration;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return title;
    }
}
