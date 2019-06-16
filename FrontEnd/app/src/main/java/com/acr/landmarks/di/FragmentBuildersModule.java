package com.acr.landmarks.di;

import com.acr.landmarks.ui.MapFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract MapFragment contributeMapFragment();

}
