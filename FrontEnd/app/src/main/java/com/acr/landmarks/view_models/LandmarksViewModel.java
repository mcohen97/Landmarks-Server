package com.acr.landmarks.view_models;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.acr.landmarks.models.Landmark;
import com.acr.landmarks.services.RetrofitLandmarksService;
import com.acr.landmarks.services.contracts.ILandmarksService;
import com.google.maps.model.LatLng;

import java.util.List;

public class LandmarksViewModel extends ViewModel {

    ILandmarksService landmarksService;
    private LiveData<List<Landmark>> landmarksInRange;

    public LandmarksViewModel(){
        //se utilizara Dagger mas adelante
        landmarksService = new RetrofitLandmarksService();
        Reload();
    }

    public void Reload(){
        landmarksInRange = landmarksService.getLandmarks(getCurrentLocation().lat,getCurrentLocation().lng,2);
    }

    public LiveData<List<Landmark>> getLandmarks(){
        return landmarksInRange;
    }

    private LatLng getCurrentLocation(){
        return new LatLng(-34.923844,-56.170590);
    }
}
