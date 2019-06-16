package com.acr.landmarks.di;

import android.arch.lifecycle.ViewModel;

import com.acr.landmarks.view_models.LandmarksViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(LandmarksViewModel.class)
    public abstract ViewModel bindLandmarksViewModel(LandmarksViewModel viewModel);

}
