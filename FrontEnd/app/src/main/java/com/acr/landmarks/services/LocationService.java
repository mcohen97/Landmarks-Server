package com.acr.landmarks.services;

import android.location.Location;

import com.acr.landmarks.services.contracts.ILocationService;

import java.util.ArrayList;
import java.util.List;


public class LocationService implements ILocationService {

    private Location currentLocation;
    private double currentRadius;
    private List<OnGeofenceChangeListener> mListeners;

    private static final LocationService instance = new LocationService();

    public static  LocationService getInstance(){
        return instance;
    }

    private LocationService(){
        mListeners = new ArrayList<>();
        currentLocation = generateDefaultLocation();
        currentRadius = 2;//harcoded,default location, take to config file
    }

    private Location  generateDefaultLocation(){
        Location defaultLocation =new Location(new String());
        defaultLocation.setLatitude(-34.923844);//harcoded,default location, take to config file
        defaultLocation.setLongitude(-56.170590);
        return defaultLocation;
    }

    @Override
    public void addGeofenceChangeListener(OnGeofenceChangeListener newListener){
        mListeners.add(newListener);
    }

    @Override
    public void setLocation(Location current) {
        boolean hasChanged = currentLocation != current;
        currentLocation = current;
        if(hasChanged) {
            notifyChange();
        }
    }

    @Override
    public void setRadius(double radius) {
        boolean hasChanged = currentRadius != radius;
        currentRadius = radius;
        if(hasChanged) {
            notifyChange();
        }
    }

    private void notifyChange() {
        for(OnGeofenceChangeListener l :mListeners){
            l.onGeofenceRadiusChange(currentLocation,currentRadius);
        }
    }

    @Override
    public Location getLocation() {
        return currentLocation;
    }

    @Override
    public double getRadius() {
        return currentRadius;
    }
}