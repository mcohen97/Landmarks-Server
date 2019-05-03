package com.acr.landmarks.service.contracts;

import android.location.Location;

import com.acr.landmarks.models.Landmark;

public interface IDistanceCalculatorService {
    public double calculateDistanceInKm(Location location, Landmark destiny);
}
