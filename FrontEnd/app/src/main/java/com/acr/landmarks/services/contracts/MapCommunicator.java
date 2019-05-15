package com.acr.landmarks.services.contracts;

import com.acr.landmarks.models.Tour;

public interface MapCommunicator {
    void drawTour(Tour selected);
    void resetTheMap();
}
