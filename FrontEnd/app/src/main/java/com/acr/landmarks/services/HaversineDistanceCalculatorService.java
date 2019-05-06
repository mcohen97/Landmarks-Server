package com.acr.landmarks.services;

import android.location.Location;

import com.acr.landmarks.models.Landmark;
import com.acr.landmarks.services.contracts.IDistanceCalculatorService;

public class HaversineDistanceCalculatorService implements IDistanceCalculatorService {
    @Override
    public double calculateDistanceInKm(Location location, Location destiny) {
        double EarthRadiusKm = 6378.1370D;
        double latDiff = Math.toRadians(destiny.getLatitude() - location.getLatitude());
        double lonDiff = Math.toRadians(destiny.getLongitude() - location.getLongitude());

        double latOr = Math.toRadians(location.getLatitude());
        double latDes = Math.toRadians(destiny.getLatitude());

        double linearDistance = Math.pow(Math.sin(latDiff/2),2D) +
                Math.cos(latOr) * Math.cos(latDes) *
                Math.pow(Math.sin(lonDiff/2),2D);

        double angularDistance = 2D * Math.atan2(Math.sqrt(linearDistance),Math.sqrt(1D - linearDistance));

        return EarthRadiusKm * angularDistance;
    }
}
