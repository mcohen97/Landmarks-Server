package com.acr.landmarks.services;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitLocationAPI {
    @GET("location/update/{token}")
    Call<OkResult> updateLocation(@Path("token") String token, @Query("lat") double lat, @Query("lng") double lng);
}
