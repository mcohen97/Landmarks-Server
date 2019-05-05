package com.acr.landmarks.view_models;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.location.Location;


public class UserLocationViewModel extends ViewModel {

    private final MutableLiveData<Location> current;
    public UserLocationViewModel(){
        current = new MutableLiveData<Location>();
    }

    public void setLocation(Location update){
      current.setValue(update);
    }

    public LiveData<Location> getLocation(){
        return current;
    }
}
