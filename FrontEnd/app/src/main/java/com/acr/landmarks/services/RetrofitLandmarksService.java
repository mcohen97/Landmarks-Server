package com.acr.landmarks.services;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.location.Location;

import com.acr.landmarks.models.LandmarkFullInfo;
import com.acr.landmarks.models.LandmarkMarkerInfo;
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
    private final MutableLiveData<List<LandmarkMarkerInfo>> landmarksData;
    private  final MutableLiveData<LandmarkFullInfo> selectedLandmark;



    public RetrofitLandmarksService(){
        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.110/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .build();
        webService= retrofit.create(RetrofitLandmarksAPI.class);
        landmarksData = new MutableLiveData<>();
        selectedLandmark = new MutableLiveData<>();
        landmarksData.setValue(new ArrayList<>());
    }

    @Override
    public LiveData<List<LandmarkMarkerInfo>> getLandmarks(Location currentLocation, double currentRadius) {
        Call<List<LandmarkMarkerInfo>> landmarks = webService.getLandmarksInRange(currentLocation.getLatitude(),currentLocation.getLongitude(),currentRadius);
        landmarks.enqueue(new Callback<List<LandmarkMarkerInfo>>() {
            @Override
            public void onResponse(Call<List<LandmarkMarkerInfo>> call, Response<List<LandmarkMarkerInfo>> response) {
                if(response.isSuccessful()){
                    landmarksData.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<LandmarkMarkerInfo>> call, Throwable t) {

            }
        });
        return landmarksData;
    }


    public  LiveData<LandmarkFullInfo> getSelectedLandmark(){
        return selectedLandmark;
    }

    @Override
    public LiveData<LandmarkFullInfo> getLandmarkById(int id) {
        Call<LandmarkFullInfo> landmark = webService.getLandmark(id);
        landmark.enqueue(new Callback<LandmarkFullInfo>() {
            @Override
            public void onResponse(Call<LandmarkFullInfo> call, Response<LandmarkFullInfo> response) {
                if(response.isSuccessful()){
                    selectedLandmark.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<LandmarkFullInfo> call, Throwable t) {

            }
        });
        return selectedLandmark;
    }
}
