package com.acr.landmarks.di;


import android.app.Application;

import com.acr.landmarks.services.PicassoImageService;
import com.acr.landmarks.services.contracts.IImageService;
import com.acr.landmarks.util.Config;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    @Provides
    static String provideString(){
        return "Stringy";
    }

    @Provides
    static IImageService getImageService(Application application){
        return new PicassoImageService(Config.getConfigValue(application,"api_url"));
    }
}
