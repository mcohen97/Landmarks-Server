package com.acr.landmarks.models;

import java.util.ArrayList;

public class Tour {
    private ArrayList<Integer> landmarksIds;
    private String imageBase64;
    private int id;
    private String name;
    private String description;
    private String category;


    public Tour() {
    }


    public Tour(ArrayList<Integer> tourLandmarks, int id, String descripcion, String category) {
        this.landmarksIds = tourLandmarks;
        this.id = id;
        this.description = descripcion;
        this.category = category;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Integer> getTourLandmarks() {
        return landmarksIds;
    }

    public int getId() {
        return id;
    }

    public void setTourLandmarks(ArrayList<Integer> tourLandmarks) {
        this.landmarksIds = tourLandmarks;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
