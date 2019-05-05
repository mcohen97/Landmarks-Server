package com.acr.landmarks.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.acr.landmarks.R;
import com.acr.landmarks.adapters.LandmarkCardAdapter;
import com.acr.landmarks.models.Landmark;
import com.acr.landmarks.models.LandmarkClusterMarker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;
import java.util.List;


public class LandmarkCardsFragment extends android.support.v4.app.Fragment implements LandmarkCardAdapter.LandmarkCardClickListener {

    private LandmarkCardAdapter  adapter;
    private RecyclerView recyclerView;
    private LandmarkSelectedListener mListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_landmark_list, container, false);
        initRecyclerView(inflater, view);
        return view;

    }

    private void initRecyclerView(LayoutInflater inflater, View view) {
        recyclerView = view.findViewById(R.id.cards_recyclerview_id);
        adapter = new LandmarkCardAdapter(inflater.getContext(), this);
        recyclerView.setLayoutManager(new GridLayoutManager(inflater.getContext(),3));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onLandmarkClicked(Landmark clicked) {
        mListener.onLandmarkSelected(clicked);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            mListener= (LandmarkSelectedListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()+" must implement "+ LandmarkSelectedListener.class);
        }
    }
}
