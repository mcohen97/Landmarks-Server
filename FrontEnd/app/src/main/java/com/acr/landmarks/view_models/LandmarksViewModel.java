package com.acr.landmarks.view_models;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.location.Location;
import android.util.Pair;

import com.acr.landmarks.models.Landmark;
import com.acr.landmarks.persistence.LandmarkStorage;
import com.acr.landmarks.persistence.RoomLandmarksStorage;
import com.acr.landmarks.services.RetrofitLandmarksService;
import com.acr.landmarks.services.contracts.ILandmarksService;
import com.acr.landmarks.util.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class LandmarksViewModel extends AndroidViewModel {

    private ILandmarksService landmarksService;
    private LandmarkStorage markersStorage;

    private LiveData<Landmark> selectedLandmark;

    private Double lastRadius;
    private Location lastCenterLocation;
    private LiveData<List<Landmark>> landmarksInRange;
    private MediatorLiveData liveDataMerger;
    private final MutableLiveData<Pair<Location,Double>> geoFence;
    private final AtomicBoolean lastDataRetrieved;
    private boolean firstGeofenceAssigned;


    public LandmarksViewModel(Application a){
        super(a);
        //se utilizara Dagger mas adelante
        markersStorage = new RoomLandmarksStorage(a);
        landmarksService = new RetrofitLandmarksService(Config.getConfigValue(a,"api_url"));
        liveDataMerger = new MediatorLiveData();
        geoFence = new MutableLiveData<Pair<Location,Double>>();
        lastDataRetrieved = new AtomicBoolean(false);
        firstGeofenceAssigned = false;
        setDefaultData();
    }

    private Location  generateDefaultLocation(){
        Location defaultLocation =new Location(new String());
        defaultLocation.setLatitude(-34.858757);//harcoded,default location, take to config file
        defaultLocation.setLongitude( -56.032397);
        return defaultLocation;
    }


    private void setDefaultData(){
        geoFence.setValue(new Pair<>(generateDefaultLocation(),new Double(2)));
        lastCenterLocation = null;
        lastRadius = null;
        selectedLandmark = landmarksService.getSelectedLandmark();
        landmarksInRange = landmarksService.getLandmarks(geoFence.getValue().first,geoFence.getValue().second);
        liveDataMerger = new MediatorLiveData<>();
        liveDataMerger.addSource(geoFence, centerRadius ->
                updateGeofence((Pair<Location, Double>) centerRadius));
        liveDataMerger.addSource(landmarksInRange,
                value -> updateLandmarks(value));
    }

    private void updateLandmarks(Object value) {
        lastDataRetrieved.set(true);
        liveDataMerger.setValue(value);
        new Thread(() -> {
            markersStorage.insertLandmarks((List<Landmark>) value);
        }).start();
    }

    private void updateGeofence(Pair<Location,Double> value){
        if((lastCenterLocation == null) || (lastRadius == null)
                || (!value.first.equals(lastCenterLocation)) || !value.second.equals(lastRadius)){
           lastCenterLocation = value.first;
           lastRadius = value.second;
           //avoiding the asignation of first (default value) affect the landmarks collection
           if(firstGeofenceAssigned){
               reload();
           }else{
               firstGeofenceAssigned = true;
           }
        }
    }


    public void reload(){
        lastDataRetrieved.set(false);
        landmarksService.getLandmarks(geoFence.getValue().first,geoFence.getValue().second);

        new Thread(() ->{
            List<Landmark> cachedLandmarks=new ArrayList<>();
            if(!lastDataRetrieved.get()) {
                cachedLandmarks = markersStorage.getSavedLandmarks(geoFence.getValue().first, geoFence.getValue().second);
            }
            if(!lastDataRetrieved.get()) {
                liveDataMerger.postValue(cachedLandmarks);
            }
        }).start();


    }

    public void setGeofence(Location aLocation, Double aRadius){
        geoFence.setValue(new Pair<>(aLocation,aRadius));
    }

    public LiveData<List<Landmark>> getLandmarks(){
        return liveDataMerger;
    }

    public void setSelectedLandmark(int id){
        selectedLandmark= landmarksService.getLandmarkById(id);
    }

    public LiveData<Landmark> getSelectedLandmark(){return  selectedLandmark;}

}
