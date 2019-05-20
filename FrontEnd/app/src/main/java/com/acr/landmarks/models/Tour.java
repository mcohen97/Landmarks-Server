package com.acr.landmarks.models;

import java.util.ArrayList;

public class Tour {
    public ArrayList<Integer> landmarksIds;
    public String imageBase64;
    public int id;
    public String title;
    public String description;
    public String category;


    public Tour() {
    }


    public Tour(ArrayList<Integer> tourLandmarks, int id, String descripcion, String category) {
        this.landmarksIds = tourLandmarks;
        this.id = id;
        this.description = descripcion;
        this.category = category;
    }

     @Override
    public boolean equals(Object o){
        return ((Tour)o).id == id;
     }
}
