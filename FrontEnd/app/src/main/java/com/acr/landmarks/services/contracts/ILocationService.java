package com.acr.landmarks.services.contracts;

import android.arch.lifecycle.LiveData;
import android.location.Location;

public interface ILocationService {

    void setLocation(Location current);

    void setRadius(double radius);

    LiveData<Location> getLocation();

    LiveData<Double> getRadius();

    /*void addGeofenceChangeListener(OnGeofenceChangeListener newListener);

    public interface OnGeofenceChangeListener{
        void onGeofenceRadiusChange(Location newLocation, double newRadius);
    }*/
}
