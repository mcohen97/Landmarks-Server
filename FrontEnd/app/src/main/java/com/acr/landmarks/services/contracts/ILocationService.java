package com.acr.landmarks.services.contracts;

import android.location.Location;

public interface ILocationService {

    void setLocation(Location current);

    void setRadius(double radius);

    Location getLocation();

    double getRadius();

    void addGeofenceChangeListener(OnGeofenceChangeListener newListener);

    public interface OnGeofenceChangeListener{
        void onGeofenceRadiusChange(Location newLocation, double newRadius);
    }
}
