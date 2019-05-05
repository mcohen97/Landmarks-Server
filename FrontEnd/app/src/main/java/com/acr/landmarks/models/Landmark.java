package com.acr.landmarks.models;

import android.support.annotation.NonNull;

public class Landmark implements Comparable<Landmark> {
    private String title;
    private double latitude;
    private double longitude;
    private String iconBase64; // Dirección de memoria ,investigar como cargar imagenes, a que carpeta se descargan
    private double distance;
    private String description;

    public Landmark(String name,String description ,double lat, double lon ) {
        this.title = name;
        this.latitude = lat;
        this.longitude = lon;
        this.description=description;
        this.distance = 9999;
    }

    public Landmark(String name, double lat, double lon, String iconBase64) {
        this.title = name;
        this.latitude = lat;
        this.longitude = lon;
        this.iconBase64 = iconBase64;
    }

    public Landmark(String name, String description, double lat, double lon, int imgResourceId ) {
        this.title = name;
        this.latitude = lat;
        this.longitude = lon;
        this.description=description;
        this.iconBase64 = imgResourceId + "";
    }

    public String getName() {
        return title;
    }

    public void setName(String name) {
        this.title = name;
    }

    public String getDescription(){return description;}

    public double getLat() {
        return latitude;
    }


    public double getLon() {
        return longitude;
    }


    public String getImg() {
        return iconBase64;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public int compareTo(@NonNull Landmark landmark) {
        if(this.distance < landmark.distance) return -1;
        if(this.distance > landmark.distance) return 1;
        return 0;
    }
}
