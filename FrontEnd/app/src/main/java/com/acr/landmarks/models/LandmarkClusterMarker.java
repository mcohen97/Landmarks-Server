package com.acr.landmarks.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class LandmarkClusterMarker implements ClusterItem {
    private LatLng position;
    private String title;
    private String snippet;
    private String iconPicture;
    private LandmarkMarkerInfo landmark;

    public LandmarkClusterMarker(LatLng position, String title, String snippet, String iconPicture, LandmarkMarkerInfo lm) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.iconPicture = iconPicture;
        this.landmark = lm;
    }

    @Override
    public LatLng getPosition() {
        return this.position;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String getSnippet() {
        return this.snippet;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIconPicture() {
        return iconPicture;
    }

    public LandmarkMarkerInfo getLandmark() {
        return landmark;
    }
}
