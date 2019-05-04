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

public class ToursFragment extends Fragment {


   // private BottomSheetBehavior sheetBehavior;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //sheetBehavior = MainActivity.getSheetBehavior();
        //sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tours, container, false);
    }
}
