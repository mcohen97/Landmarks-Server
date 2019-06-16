package com.acr.landmarks.di;


import android.app.Application;

import com.acr.landmarks.services.PicassoImageService;
import com.acr.landmarks.services.RetrofitLandmarksService;
import com.acr.landmarks.services.contracts.IImageService;
import com.acr.landmarks.services.contracts.ILandmarksService;
import com.acr.landmarks.util.Config;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {


    @Singleton
    @Provides
    static IImageService provideImageService(Application application){
        return new PicassoImageService(Config.getConfigValue(application,"api_url"));
    }

    @Singleton
    @Provides
    static ILandmarksService provideLandmarksService(Application application){
        return new RetrofitLandmarksService(Config.getConfigValue(application,"api_url"));
    }
}
