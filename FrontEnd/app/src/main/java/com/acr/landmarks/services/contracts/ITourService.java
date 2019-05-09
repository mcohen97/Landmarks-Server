package com.acr.landmarks.services.contracts;

import android.arch.lifecycle.LiveData;
import com.acr.landmarks.models.Tour;

import java.util.List;

public interface ITourService {

    LiveData<List<Tour>> getTours();

    LiveData<Tour> getTourById(int id);
}
