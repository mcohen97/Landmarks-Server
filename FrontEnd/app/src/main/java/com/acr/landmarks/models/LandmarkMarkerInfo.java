package com.acr.landmarks.models;

public class LandmarkMarkerInfo {

    public int id;
    public String title;
    public double latitude;
    public double longitude;
    public String iconBase64;

    public LandmarkMarkerInfo(int id, String title, double latitude, double longitude, String iconBase64) {
        this.id = id;
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.iconBase64 = iconBase64;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        return ((LandmarkMarkerInfo) o).id == this.id;
    }
}
