package com.acr.landmarks.services;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.location.Location;

import com.acr.landmarks.models.Landmark;
import com.acr.landmarks.services.contracts.ILandmarksService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitLandmarksService implements  ILandmarksService{

    private Retrofit retrofit;
    private RetrofitLandmarksAPI webService;
    private final MutableLiveData<List<Landmark>> landmarksData;


    public RetrofitLandmarksService(){
        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.101:5002/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .build();
        webService= retrofit.create(RetrofitLandmarksAPI.class);
        landmarksData = new MutableLiveData<>();
        landmarksData.setValue(new ArrayList<>());
    }

    @Override
    public LiveData<List<Landmark>> getLandmarks(Location currentLocation, double currentRadius) {
        Call<List<Landmark>> landmarks = webService.getLandmarksInRange(currentLocation.getLatitude(),currentLocation.getLongitude(),currentRadius);
        landmarks.enqueue(new Callback<List<Landmark>>() {
            @Override
            public void onResponse(Call<List<Landmark>> call, Response<List<Landmark>> response) {
                if(response.isSuccessful()){
                    landmarksData.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Landmark>> call, Throwable t) {

            }
        });
        return landmarksData;
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
}
