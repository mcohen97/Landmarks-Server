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
    //private ILocationService locationService;

    private LiveData<Landmark> selectedLandmark;

    private Double lastRadius;
    private Location lastCenterLocation;
    private LiveData<List<Landmark>> landmarksInRange;
    private MediatorLiveData liveDataMerger;
    private final MutableLiveData<Location> rangeCenterLocation;
    private final MutableLiveData<Double> currentRadius;
    //private final MutableLiveData<Pair<Location,Double>> geoFence;


    public LandmarksViewModel(){
        //se utilizara Dagger mas adelante
        landmarksService = new RetrofitLandmarksService();

        //locationService = LocationService.getInstance();
        liveDataMerger = new MediatorLiveData();
        rangeCenterLocation = new MutableLiveData<>();
        currentRadius = new MutableLiveData<>();
        //geoFence = new MutableLiveData<Pair<Location,Double>>();

        setDefaultData();
        //reload();
    }

    private Location  generateDefaultLocation(){
        Location defaultLocation =new Location(new String());
        defaultLocation.setLatitude(-34.923844);//harcoded,default location, take to config file
        defaultLocation.setLongitude(-56.170590);
        return defaultLocation;
    }


    private void setDefaultData(){
        rangeCenterLocation.setValue(generateDefaultLocation());
        currentRadius.setValue(new Double(2));
        lastCenterLocation = null;
        lastRadius = null;
        landmarksInRange = landmarksService.getLandmarks(rangeCenterLocation.getValue(),currentRadius.getValue());
        liveDataMerger = new MediatorLiveData<>();
        liveDataMerger.addSource(rangeCenterLocation,
                value -> updateLandmarksIfLocationChanges((Location)value));
        liveDataMerger.addSource(currentRadius,
                value -> updateLandmarksIfRadiusChanges((Double)value));
        //liveDataMerger.addSource(geoFence, centerRadius ->updateGeofence(centerRadius));
        liveDataMerger.addSource(landmarksInRange,
                value -> liveDataMerger.setValue(value));
    }

    //private void updateGeofence(Pair<Location,Double> ){}

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
        landmarksService.getLandmarks(rangeCenterLocation.getValue(),currentRadius.getValue());
//        landmarksInRange = landmarksService.getLandmarks();
    }

    public void setGeofence(Location aLocation, Double aRadius){
        rangeCenterLocation.setValue(aLocation);
        currentRadius.setValue(aRadius);

    }

    public void setRadius(Double aRadius){
        currentRadius.setValue(aRadius);
    }

    public void setCenter(Location location){
        rangeCenterLocation.setValue(location);
    }

    public LiveData<List<Landmark>> getLandmarks(){
        return liveDataMerger;
    }

    public void SetSelectedLandmark(int id){
        selectedLandmark= landmarksService.getLandmarkById(id);
    }

    public LiveData<Landmark> getSelectedLandmark(){return  selectedLandmark;}


}
