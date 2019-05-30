package com.acr.landmarks.services;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.location.Location;

import com.acr.landmarks.models.Tour;
import com.acr.landmarks.services.contracts.ITourService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitToursService implements ITourService {

    private Retrofit retrofit;
    private RetrofitToursAPI webService;
    private final MutableLiveData<List<Tour>> toursData;


    public RetrofitToursService(String apiBaseUrl){
        retrofit = new Retrofit.Builder()
                .baseUrl(apiBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .build();
        webService= retrofit.create(RetrofitToursAPI.class);
        toursData = new MutableLiveData<>();
        toursData.setValue(new ArrayList<>());
    }

    @Override
    public LiveData<List<Tour>> getTours(Location currentLocation, double currentRadius) {
        Call<List<Tour>> tours = webService.getToursInRange(currentLocation.getLatitude(),currentLocation.getLongitude(),currentRadius);
        tours.enqueue(new Callback<List<Tour>>() {
            @Override
            public void onResponse(Call<List<Tour>> call, Response<List<Tour>> response) {
                if(response.isSuccessful()){
                    toursData.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Tour>> call, Throwable t) {

            }
        });
        return toursData;
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
}
