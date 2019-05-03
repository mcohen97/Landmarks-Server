package com.acr.landmarks.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.acr.landmarks.R;
import com.acr.landmarks.adapters.LandmarkCardAdapter;
import com.acr.landmarks.service.LandmarksService;


public class LandmarkCardsFragment extends android.support.v4.app.Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_landmark_list, container, false);

        initRecyclerView(inflater, view);

        return view;

    }

    private void initRecyclerView(LayoutInflater inflater, View view) {
        RecyclerView recyclerView = view.findViewById(R.id.cards_recyclerview_id);
        LandmarkCardAdapter adapter = new LandmarkCardAdapter(inflater.getContext(), new LandmarksService());
        recyclerView.setLayoutManager(new GridLayoutManager(inflater.getContext(),3));
        recyclerView.setAdapter(adapter);
    }
}
