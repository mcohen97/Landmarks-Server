package com.acr.landmarks.persistence;

import android.location.Location;

import com.acr.landmarks.models.LandmarkMarkerInfo;

import java.util.List;

public interface LandmarkMarkersStorage {

    List<LandmarkMarkerInfo> getSavedLandmarks(Location location, double radius);

    void deleteStorage();

    void insertLandmarks(List<LandmarkMarkerInfo> landmarks);
}
