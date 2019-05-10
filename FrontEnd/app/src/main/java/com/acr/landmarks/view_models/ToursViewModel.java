package com.acr.landmarks.view_models;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.acr.landmarks.models.Landmark;
import com.acr.landmarks.models.Tour;
import com.acr.landmarks.services.RetrofitLandmarksService;
import com.acr.landmarks.services.RetrofitToursService;
import com.acr.landmarks.services.contracts.ITourService;
import com.google.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class ToursViewModel extends ViewModel {
    private ITourService tourService;
    private LiveData<List<Tour>> toursInRange;
    private LiveData<Tour> selectedTour;

    public ToursViewModel(){
        tourService = new RetrofitToursService();
        MutableLiveData<List<Tour>> defaultData= new MutableLiveData<List<Tour>>();
        defaultData.setValue(new ArrayList<Tour>());
        toursInRange = defaultData;
        Reload();
    }

    public void Reload(){
        toursInRange = tourService.getTours();
    }

    public void setSelectedTour(int id){
        selectedTour = tourService.getTourById(id);
    }

    public LiveData<List<Tour>> getTours(){
        return toursInRange;
    }

    public LiveData<Tour> getSelectedTour(){return selectedTour;}

    private LatLng getCurrentLocation(){
        return new LatLng(-34.923844,-56.170590);
    }
}
