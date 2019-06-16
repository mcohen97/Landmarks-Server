package com.acr.landmarks.di;

import com.acr.landmarks.ui.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuildersModule {

    @ContributesAndroidInjector
    abstract MainActivity provideMainActivity();
}
