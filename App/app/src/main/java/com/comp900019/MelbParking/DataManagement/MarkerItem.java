package com.comp900019.MelbParking.DataManagement;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MarkerItem implements ClusterItem {
    private LatLng position;
    private String mTitle;
    private String mSnippet;

    public MarkerItem(LatLng position){
        this.position = position;
    }
    public MarkerItem(LatLng position, String title, String snippet){
        this.position = position;
        this.mTitle = title;
        this.mSnippet = snippet;
    }
    @Override
    public LatLng getPosition() {
        return this.position;
    }

    @Override
    public String getTitle() {
        return this.mTitle;
    }

    @Override
    public String getSnippet() {
        return this.mSnippet;
    }
}
