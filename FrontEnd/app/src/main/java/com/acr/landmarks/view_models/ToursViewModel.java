package com.acr.landmarks.view_models;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.location.Location;
import android.util.Pair;

import com.acr.landmarks.models.Landmark;
import com.acr.landmarks.models.Tour;
import com.acr.landmarks.services.RetrofitToursService;
import com.acr.landmarks.services.contracts.ITourService;
import com.acr.landmarks.util.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ToursViewModel extends AndroidViewModel {
    private ITourService tourService;

    private MutableLiveData<Tour> selectedTour;

    private Double lastRadius;
    private Location lastCenterLocation;
    private LiveData<List<Tour>> toursInRange;
    private MediatorLiveData liveDataMerger;
    private final MutableLiveData<Pair<Location,Double>> geoFence;
    private final AtomicBoolean lastDataRetrieved;


    public ToursViewModel(Application a){
        super(a);
        tourService = new RetrofitToursService(Config.getConfigValue(a,"api_url"));
        liveDataMerger = new MediatorLiveData();
        geoFence = new MutableLiveData<Pair<Location,Double>>();
        lastDataRetrieved = new AtomicBoolean(false);
        selectedTour = new MutableLiveData<>();
        setDefaultData();
    }

    private Location  generateDefaultLocation(){
        Location defaultLocation =new Location(new String());
        defaultLocation.setLatitude(-34.909429);//harcoded,default location, take to config file
        defaultLocation.setLongitude( -56.166652);
        return defaultLocation;
    }


    private void setDefaultData(){
        geoFence.setValue(new Pair<>(generateDefaultLocation(),new Double(4)));
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
        //auxiliary tour to find original, taking advantage of Tour equals method, by id
        Tour findHelper = new Tour();
        findHelper.id=id;
        int index = toursInRange.getValue().indexOf(findHelper);
        if(index >=0) {
            selectedTour.setValue(toursInRange.getValue().get(index));
        }else{
            selectedTour.setValue(null);
        }
    }

    public LiveData<Tour> getSelectedTour(){return selectedTour;}
}
