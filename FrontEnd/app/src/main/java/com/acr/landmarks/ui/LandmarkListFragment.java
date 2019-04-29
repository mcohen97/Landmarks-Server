package com.acr.landmarks.ui;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.acr.landmarks.R;
import com.acr.landmarks.adapters.LandmarkListAdapter;
import com.acr.landmarks.models.Landmark;
import com.acr.landmarks.models.LandmarkClusterMarker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


public class LandmarkListFragment extends Fragment implements
        LandmarkListAdapter.LandmarkListRecyclerClickListener {

    private RecyclerView mLandmarkListRecyclerView;
    private LandmarkListAdapter mLandmarkListRecyclerAdapter;
    private ArrayList<Landmark> mLandmarks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_landmark_list, container, false);
        updateLandmarks();
        mLandmarkListRecyclerView = view.findViewById(R.id.landmark_list_recycler_view);
        initLandmarkListRecyclerView();
        // Inflate the layout for this fragment
        return view;
    }

    private void initLandmarkListRecyclerView() {
        mLandmarkListRecyclerAdapter = new LandmarkListAdapter(mLandmarks, this);
        mLandmarkListRecyclerView.setAdapter(mLandmarkListRecyclerAdapter);
        mLandmarkListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    //Hardcoded
    private void updateLandmarks() {
        mLandmarks = MainActivity.getLandmarks();
    }

    @Override
    public void onLandmarkClicked(int position) {
        String selectedUserId = mLandmarks.get(position).getName();
        GoogleMap mMap = MapFragment.getMap();
        ArrayList<LandmarkClusterMarker> mClusterMarkers = MapFragment.getMarkers();

        for(LandmarkClusterMarker clusterMarker: mClusterMarkers){
            if(selectedUserId.equals(clusterMarker.getLandmark().getName())){
                mMap.animateCamera(
                        CameraUpdateFactory.newLatLng(
                                new LatLng(clusterMarker.getPosition().latitude, clusterMarker.getPosition().longitude)),
                        600,
                        null
                );
                break;
            }
        }
    }
}
