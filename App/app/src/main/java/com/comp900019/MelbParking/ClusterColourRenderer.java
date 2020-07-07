package com.comp900019.MelbParking;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class ClusterColourRenderer extends DefaultClusterRenderer<ClusterableParkingSpot> {

    public ClusterColourRenderer(Context context, GoogleMap map, ClusterManager<ClusterableParkingSpot> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(ClusterableParkingSpot cps, MarkerOptions markerOptions) {


        float color;

        if (cps.getDuration() == null) {
            color = BitmapDescriptorFactory.HUE_AZURE;
        } else if (cps.getDuration() <= 10) {
            color = BitmapDescriptorFactory.HUE_BLUE;
            // 10 Minutes
        } else if (cps.getDuration() <= 15) {
            color = BitmapDescriptorFactory.HUE_GREEN;
            // 15 Minutes
        } else if (cps.getDuration() <= 30) {
            color = BitmapDescriptorFactory.HUE_ORANGE;
            // 30 minutes2
        } else if (cps.getDuration() <= 60) {
            color = BitmapDescriptorFactory.HUE_VIOLET;
            // 60 minutes
        } else if (cps.getDuration() <= 120) {
            color = BitmapDescriptorFactory.HUE_YELLOW;
            // 2 hours
        } else {
            // Anything else
            color = BitmapDescriptorFactory.HUE_MAGENTA;
        }
        BitmapDescriptor markerDescriptor = BitmapDescriptorFactory.defaultMarker(color);

        markerOptions.icon(markerDescriptor);
    }
}
