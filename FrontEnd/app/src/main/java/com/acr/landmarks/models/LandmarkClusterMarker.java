package com.acr.landmarks.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterItem;

public class LandmarkClusterMarker implements ClusterItem {
    private LatLng position; // required field
    private String title; // required field
    private String snippet; // required field
    private String iconPicture; //dirección de memoria, investigar como cargar imagenes, a que carpeta se descargan
    private LandmarkMarkerInfo landmark;
    private Marker marker;

    public LandmarkClusterMarker(LatLng position, String title, String snippet, String iconPicture , LandmarkMarkerInfo lm) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.iconPicture = iconPicture;
        this.landmark = lm;
    }
    public LandmarkClusterMarker() {
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

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getIconPicture() {
        return iconPicture;
    }

    public void setIconPicture(String iconPicture) {
        this.iconPicture = iconPicture;
    }

    public LandmarkMarkerInfo getLandmark() {
        return landmark;
    }

    public void setLandmark(LandmarkMarkerInfo landmark) {
        this.landmark = landmark;
    }
}
