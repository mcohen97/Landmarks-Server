package com.acr.landmarks.models;

import android.location.Location;

public class Landmark {
    public String name;
    public String description;
    public double lat;
    public double lon;
    public String img; // Dirección de memoria ,investigar como cargar imagenes, a que carpeta se descargan

    public Landmark(String name, String description, double lat, double lon ) {
        this.name = name;
        this.description = description;
        this.lat = lat;
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
