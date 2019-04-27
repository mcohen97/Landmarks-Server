package com.acr.landmarks.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class LandmarkClusterMarker implements ClusterItem {
    private LatLng position; // required field
    private String title; // required field
    private String snippet; // required field
    private int iconPicture; //dirección de memoria, investigar como cargar imagenes, a que carpeta se descargan
    private Landmark landmark;

    public LandmarkClusterMarker(LatLng position, String title, String snippet, int iconPicture , Landmark lm) {
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

    public int getIconPicture() {
        return iconPicture;
    }

    public void setIconPicture(int iconPicture) {
        this.iconPicture = iconPicture;
    }

    public Landmark getLandmark() {
        return landmark;
    }

    public void setLandmark(Landmark landmark) {
        this.landmark = landmark;
    }
}
