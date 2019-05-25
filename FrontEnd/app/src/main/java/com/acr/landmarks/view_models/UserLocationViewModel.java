package com.acr.landmarks.view_models;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.location.Location;

import com.acr.landmarks.services.LocationService;
import com.acr.landmarks.services.contracts.ILocationService;


public class UserLocationViewModel extends ViewModel {

    private final ILocationService locationService;
    private final LiveData<Location> current;

    public UserLocationViewModel(){
        locationService = LocationService.getInstance();
        current = locationService.getLocation();
    }

    public void setLocation(Location update){
        locationService.setLocation(update);
    }

    public LiveData<Location> getLocation(){
        return current;
    }
}
