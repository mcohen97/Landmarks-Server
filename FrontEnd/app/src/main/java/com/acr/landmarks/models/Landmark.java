package com.acr.landmarks.models;

import android.support.annotation.NonNull;

public class Landmark implements Comparable<Landmark> {
    private int id;
    private String title;
    private double latitude;
    private double longitude;
    private String iconBase64; // Dirección de memoria ,investigar como cargar imagenes, a que carpeta se descargan
    private double distance;
    private String description;

    public Landmark(int id,String name,String description ,double lat, double lon ) {
        this.id = id;
        this.title = name;
        this.latitude = lat;
        this.longitude = lon;
        this.description=description;
        this.distance = 9999;
    }

    public Landmark(int id, String name, double lat, double lon, String iconBase64) {
        this.id = id;
        this.title = name;
        this.latitude = lat;
        this.longitude = lon;
        this.iconBase64 = iconBase64;
    }

    public Landmark(int id, String name, String description, double lat, double lon, int imgResourceId ) {
        this.id = id;
        this.title = name;
        this.latitude = lat;
        this.longitude = lon;
        this.description=description;
        this.iconBase64 = imgResourceId + "";
    }

    public int getId(){
        return id;
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

    @Override
    public boolean equals(Object o){
        if(o == null){
            return false;
        }
        return ((Landmark)o).getId() == this.id;
    }
}
