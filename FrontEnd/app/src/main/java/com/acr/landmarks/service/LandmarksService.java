package com.acr.landmarks.service;

import com.acr.landmarks.R;
import com.acr.landmarks.models.Landmark;

import java.util.ArrayList;
import java.util.List;

public class LandmarksService {

    private List<Landmark> landmarks;

    public LandmarksService(){
        landmarks = new ArrayList<Landmark>();
        addTestData();

    }

    public List<Landmark> getAllLandmarks(){
        return landmarks;
    }

    public int getLandmarksCount(){
        return landmarks.size();
    }

    private void addTestData(){
        //--TEST DATA---
        landmarks.add(new Landmark("Monumento a Damoxeno","Esto es una descripcion",-34.896398, -56.162552, R.drawable.test_image));
        landmarks.add(new Landmark("Obelisco a los Constituyentes de 1830","Esto es una descripcion",-34.897322, -56.164429, R.drawable.test_image));
        //--------------
    }
}
