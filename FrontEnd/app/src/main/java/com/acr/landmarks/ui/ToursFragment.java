package com.acr.landmarks.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.acr.landmarks.R;
import com.acr.landmarks.adapters.TourCardAdapter;

public class ToursFragment extends android.support.v4.app.Fragment implements TourCardAdapter.TourClickListener  {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_tours, container, false);
    }
}
