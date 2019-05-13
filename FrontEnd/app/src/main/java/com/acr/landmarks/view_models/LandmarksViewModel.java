package com.acr.landmarks.view_models;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.location.Location;
import android.util.Pair;

import com.acr.landmarks.models.Landmark;
import com.acr.landmarks.services.RetrofitLandmarksService;
import com.acr.landmarks.services.contracts.ILandmarksService;

import java.util.List;

public class LandmarksViewModel extends ViewModel {

    private ILandmarksService landmarksService;

    private LiveData<Landmark> selectedLandmark;

    private Double lastRadius;
    private Location lastCenterLocation;
    private LiveData<List<Landmark>> landmarksInRange;
    private MediatorLiveData liveDataMerger;
    private final MutableLiveData<Pair<Location,Double>> geoFence;


    public LandmarksViewModel(){
        //se utilizara Dagger mas adelante
        landmarksService = new RetrofitLandmarksService();
        liveDataMerger = new MediatorLiveData();
        geoFence = new MutableLiveData<Pair<Location,Double>>();
        setDefaultData();
    }

    private Location  generateDefaultLocation(){
        Location defaultLocation =new Location(new String());
        defaultLocation.setLatitude(-34.923844);//harcoded,default location, take to config file
        defaultLocation.setLongitude(-56.170590);
        return defaultLocation;
    }


    private void setDefaultData(){
        geoFence.setValue(new Pair<>(generateDefaultLocation(),new Double(2)));
        lastCenterLocation = null;
        lastRadius = null;
        landmarksInRange = landmarksService.getLandmarks(geoFence.getValue().first,geoFence.getValue().second);
        liveDataMerger = new MediatorLiveData<>();
        liveDataMerger.addSource(geoFence, centerRadius ->
                updateGeofence((Pair<Location, Double>) centerRadius));
        liveDataMerger.addSource(landmarksInRange,
                value -> liveDataMerger.setValue(value));
    }

    private void updateGeofence(Pair<Location,Double> value){
        if((lastCenterLocation == null) || (lastRadius == null)
                || (!value.first.equals(lastCenterLocation)) || !value.second.equals(lastRadius)){
           lastCenterLocation = value.first;
           lastRadius = value.second;
           reload();
        }
    }

    private void updateLandmarksIfLocationChanges(Location value) {
        if((lastCenterLocation == null) || (!value.equals(lastCenterLocation))) {
            lastCenterLocation = value;
            reload();
        }
    }

    private void updateLandmarksIfRadiusChanges(Double value) {
        if((lastRadius == null) || !value.equals(lastRadius)) {
            lastRadius = value;
            reload();
        }
    }

    public void reload(){
        landmarksService.getLandmarks(geoFence.getValue().first,geoFence.getValue().second);
    }

    public void setGeofence(Location aLocation, Double aRadius){
        geoFence.setValue(new Pair<>(aLocation,aRadius));
    }

    public void setRadius(Double aRadius){
        Pair<Location,Double> newGeoFence = new Pair<>(geoFence.getValue().first,aRadius);
        geoFence.setValue(newGeoFence);
    }

    public void setCenter(Location location){
        Pair<Location,Double> newGeoFence = new Pair<>(location,geoFence.getValue().second);
        geoFence.setValue(newGeoFence);
    }

    public LiveData<List<Landmark>> getLandmarks(){
        return liveDataMerger;
    }

    public void SetSelectedLandmark(int id){
        selectedLandmark= landmarksService.getLandmarkById(id);
    }

    public LiveData<Landmark> getSelectedLandmark(){return  selectedLandmark;}

}
