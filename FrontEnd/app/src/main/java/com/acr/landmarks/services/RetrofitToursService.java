package com.acr.landmarks.services;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.location.Location;

import com.acr.landmarks.models.Tour;
import com.acr.landmarks.services.contracts.ILocationService;
import com.acr.landmarks.services.contracts.ITourService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitToursService implements ITourService, ILocationService.OnGeofenceChangeListener {

    private Retrofit retrofit;
    private RetrofitToursAPI webService;
    private ILocationService locationService;
    private Location currentLocation;
    private double currentRadius;
    private final MutableLiveData<List<Tour>> data;


    public RetrofitToursService(){
        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.100/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webService= retrofit.create(RetrofitToursAPI.class);
        locationService= LocationService.getInstance();
        locationService.addGeofenceChangeListener(this);
        currentLocation = locationService.getLocation();
        currentRadius = locationService.getRadius();
        data = new MutableLiveData<List<Tour>>();
    }


    @Override
    public LiveData<List<Tour>> getTours() {
        Call<List<Tour>> tours = webService.getToursInRange(currentLocation.getLatitude(),currentLocation.getLongitude(),currentRadius);
        tours.enqueue(new Callback<List<Tour>>() {
            @Override
            public void onResponse(Call<List<Tour>> call, Response<List<Tour>> response) {
                if(response.isSuccessful()){
                    data.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Tour>> call, Throwable t) {

            }
        });
        return data;
    }


    @Override
    public LiveData<Tour> getTourById(int id) {
        final MutableLiveData<Tour> data = new MutableLiveData<>();
        Call<Tour> tour = webService.getTour(id);
        tour.enqueue(new Callback<Tour>() {
            @Override
            public void onResponse(Call<Tour> call, Response<Tour> response) {
                if(response.isSuccessful()){
                    data.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<Tour> call, Throwable t) {

            }
        });
        return  data;
    }

    @Override
    public void onGeofenceRadiusChange(Location newLocation, double newRadius) {
        currentLocation=newLocation;
        currentRadius= newRadius;
        getTours();
    }

}
