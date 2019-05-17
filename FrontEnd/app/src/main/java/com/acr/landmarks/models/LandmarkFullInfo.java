package com.acr.landmarks.models;

import java.util.List;

public class LandmarkFullInfo {
    public int id;
    public String title;
    public String description;
    public double latitude;
    public double longitude;
    public String[] imagesBase64;
    public List<String> audiosBase64;

    @Override
    public boolean equals(Object o){
        if(o == null){
            return false;
        }
        return ((LandmarkMarkerInfo)o).id == this.id;
    }
}
