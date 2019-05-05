package com.acr.landmarks.services;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.acr.landmarks.models.Landmark;
import com.acr.landmarks.services.contracts.ILandmarksService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitLandmarksService implements  ILandmarksService {

    private Retrofit retrofit;
    private RetrofitLandmarksAPI webService;

    public RetrofitLandmarksService(){
        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.110/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webService= retrofit.create(RetrofitLandmarksAPI.class);
    }


    @Override
    public LiveData<List<Landmark>> getLandmarks(double latitude, double longitude, float distance) {
         final MutableLiveData<List<Landmark>> data = new MutableLiveData<>();

        Call<List<Landmark>> landmarks = webService.getLandmarksInRange(latitude,longitude,distance);
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
}
