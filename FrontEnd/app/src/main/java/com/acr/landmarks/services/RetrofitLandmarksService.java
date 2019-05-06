package com.acr.landmarks.services;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.location.Location;

import com.acr.landmarks.models.Landmark;
import com.acr.landmarks.services.contracts.ILandmarksService;
import com.acr.landmarks.services.contracts.ILocationService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitLandmarksService implements  ILandmarksService, ILocationService.OnGeofenceChangeListener {

    private Retrofit retrofit;
    private RetrofitLandmarksAPI webService;
    private ILocationService locationService;
    private Location currentLocation;
    private double currentRadius;
    private final MutableLiveData<List<Landmark>> data;


    public RetrofitLandmarksService(){
        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.110/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webService= retrofit.create(RetrofitLandmarksAPI.class);
        locationService= LocationService.getInstance();
        locationService.addGeofenceChangeListener(this);
        currentLocation = locationService.getLocation();
        currentRadius = locationService.getRadius();
        data = new MutableLiveData<>();
    }


    @Override
    public LiveData<List<Landmark>> getLandmarks() {
        Call<List<Landmark>> landmarks = webService.getLandmarksInRange(currentLocation.getLatitude(),currentLocation.getLongitude(),currentRadius);
        landmarks.enqueue(new Callback<List<Landmark>>() {
            @Override
            public void onResponse(Call<List<Landmark>> call, Response<List<Landmark>> response) {
                if(response.isSuccessful()){
                    data.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Landmark>> call, Throwable t) {

            }
        });
        return data;
    }


    @Override
    public LiveData<Landmark> getLandmarkById(int id) {
        final MutableLiveData<Landmark> data = new MutableLiveData<>();
        Call<Landmark> landmark = webService.getLandmark(id);
        landmark.enqueue(new Callback<Landmark>() {
            @Override
            public void onResponse(Call<Landmark> call, Response<Landmark> response) {
                if(response.isSuccessful()){
                    data.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<Landmark> call, Throwable t) {

            }
        });
        return  data;
    }

    @Override
    public void onGeofenceRadiusChange(Location newLocation, double newRadius) {
        currentLocation=newLocation;
        currentRadius= newRadius;
        getLandmarks();
    }
}
