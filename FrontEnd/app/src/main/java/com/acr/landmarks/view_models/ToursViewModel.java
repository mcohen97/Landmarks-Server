package com.acr.landmarks.view_models;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.location.Location;
import android.util.Pair;

import com.acr.landmarks.models.Landmark;
import com.acr.landmarks.models.Tour;
import com.acr.landmarks.persistence.RoomMarkersStorage;
import com.acr.landmarks.services.RetrofitLandmarksService;
import com.acr.landmarks.services.RetrofitToursService;
import com.acr.landmarks.services.contracts.ITourService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ToursViewModel extends AndroidViewModel {
    private ITourService tourService;

    private LiveData<Tour> selectedTour;

    private Double lastRadius;
    private Location lastCenterLocation;
    private LiveData<List<Tour>> toursInRange;
    private MediatorLiveData liveDataMerger;
    private final MutableLiveData<Pair<Location,Double>> geoFence;
    private final AtomicBoolean lastDataRetrieved;


    public ToursViewModel(Application a){
        super(a);
        tourService = new RetrofitToursService();
        liveDataMerger = new MediatorLiveData();
        geoFence = new MutableLiveData<Pair<Location,Double>>();
        lastDataRetrieved = new AtomicBoolean(false);
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
        toursInRange = tourService.getTours(geoFence.getValue().first,geoFence.getValue().second);
        liveDataMerger = new MediatorLiveData<>();
        liveDataMerger.addSource(geoFence, centerRadius ->
                updateGeofence((Pair<Location, Double>) centerRadius));
        liveDataMerger.addSource(toursInRange,
                value -> updateTours(value));
    }

    private void updateTours(Object value) {
        lastDataRetrieved.set(true);
        liveDataMerger.setValue(value);
        /*new Thread(() -> {
            toursStorage.insertTours((List<Tour>) value); -> Crear room para los tours
        }).start();*/
    }

    private void updateGeofence(Pair<Location,Double> value){
        if((lastCenterLocation == null) || (lastRadius == null)
                || (!value.first.equals(lastCenterLocation)) || !value.second.equals(lastRadius)){
            lastCenterLocation = value.first;
            lastRadius = value.second;
            reload();
        }
    }


    public void reload(){
        lastDataRetrieved.set(false);
        tourService.getTours(geoFence.getValue().first,geoFence.getValue().second);

        new Thread(() ->{
            List<Landmark> cachedLandmarks=new ArrayList<>();
            if(!lastDataRetrieved.get()) {
                //cachedLandmarks = toursStorage.getSavedLandmarks(geoFence.getValue().first, geoFence.getValue().second);
            }
            if(!lastDataRetrieved.get()) {
                liveDataMerger.postValue(cachedLandmarks);
            }
        }).start();


    }

    public void setGeofence(Location aLocation, Double aRadius){
        geoFence.setValue(new Pair<>(aLocation,aRadius));
    }


    public LiveData<List<Tour>> getTours(){
        return liveDataMerger;
    }

    public void setSelectedTour(int id){
        selectedTour = tourService.getTourById(id);
    }

    public LiveData<Tour> getSelectedTour(){return selectedTour;}
}
