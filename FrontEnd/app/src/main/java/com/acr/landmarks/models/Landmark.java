package com.acr.landmarks.models;

public class Landmark {
    public int id;
    public String title;
    public String description;
    public double latitude;
    public double longitude;
    public String[] imageFiles;
    public String[] audioFiles;

    public Landmark(int id, String title, String description, double latitude, double longitude, String[] images, String[] audios) {
        this.id = id;
        this.title =title;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageFiles = images;
        this.audioFiles = audios;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        return ((Landmark) o).id == this.id;
    }
}
