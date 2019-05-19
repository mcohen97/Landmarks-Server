package com.acr.landmarks.services.contracts;

import android.arch.lifecycle.LiveData;
import android.location.Location;

import com.acr.landmarks.models.LandmarkFullInfo;
import com.acr.landmarks.models.LandmarkMarkerInfo;

import java.util.List;

public interface ILandmarksService {

    //LiveData<List<LandmarkMarkerInfo>> getLandmarks();
    LiveData<List<LandmarkMarkerInfo>> getLandmarks(Location location, double radius);

    LiveData<LandmarkFullInfo> getLandmarkById(int id);

    LiveData<LandmarkFullInfo> getSelectedLandmark();
}
