package com.acr.landmarks.view_models;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.location.Location;

import com.acr.landmarks.models.Landmark;
import com.acr.landmarks.services.LocationService;
import com.acr.landmarks.services.RetrofitLandmarksService;
import com.acr.landmarks.services.contracts.ILandmarksService;
import com.acr.landmarks.services.contracts.ILocationService;
import com.google.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class LandmarksViewModel extends ViewModel {

    private ILandmarksService landmarksService;
    private LiveData<List<Landmark>> landmarksInRange;
    private LiveData<Landmark> selectedLandmark;

    public LandmarksViewModel(){
        //se utilizara Dagger mas adelante
        landmarksService = new RetrofitLandmarksService();
        MutableLiveData<List<Landmark>> defaultData= new MutableLiveData<List<Landmark>>();
        defaultData.setValue(new ArrayList<Landmark>());
        landmarksInRange = defaultData;
        Reload();
    }

    public void Reload(){
        landmarksInRange = landmarksService.getLandmarks();
    }

    public void SetSelectedLandmark(int id){
        selectedLandmark= landmarksService.getLandmarkById(id);
    }

    public LiveData<List<Landmark>> getLandmarks(){
        return landmarksInRange;
    }

    public LiveData<Landmark> getSelectedLandmark(){return  selectedLandmark;}

    private LatLng getCurrentLocation(){
        return new LatLng(-34.923844,-56.170590);
    }
}
