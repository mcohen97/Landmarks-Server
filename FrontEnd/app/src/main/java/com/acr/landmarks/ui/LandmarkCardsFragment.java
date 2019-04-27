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
import com.acr.landmarks.models.Landmark;

import java.util.ArrayList;
import java.util.List;

public class LandmarkCardsFragment extends android.support.v4.app.Fragment {

    private List<Landmark> mainLandmarks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainLandmarks = new ArrayList<Landmark>();
        //--TEST--
        mainLandmarks.add(new Landmark("Monumento a Damoxeno","Esto es una descripcion",-34.896398, -56.162552, R.drawable.test_image));
        mainLandmarks.add(new Landmark("Obelisco a los Constituyentes de 1830","Esto es una descripcion",-34.897322, -56.164429, R.drawable.test_image));
        mainLandmarks.add(new Landmark("Monumento a Damoxeno","Esto es una descripcion",-34.896398, -56.162552, R.drawable.test_image));
        mainLandmarks.add(new Landmark("Obelisco a los Constituyentes de 1830","Esto es una descripcion",-34.897322, -56.164429, R.drawable.test_image));
        mainLandmarks.add(new Landmark("Monumento a Damoxeno","Esto es una descripcion",-34.896398, -56.162552, R.drawable.test_image));
        mainLandmarks.add(new Landmark("Obelisco a los Constituyentes de 1830","Esto es una descripcion",-34.897322, -56.164429, R.drawable.test_image));
        mainLandmarks.add(new Landmark("Monumento a Damoxeno","Esto es una descripcion",-34.896398, -56.162552, R.drawable.test_image));
        mainLandmarks.add(new Landmark("Obelisco a los Constituyentes de 1830","Esto es una descripcion",-34.897322, -56.164429, R.drawable.test_image));
        mainLandmarks.add(new Landmark("Monumento a Damoxeno","Esto es una descripcion",-34.896398, -56.162552, R.drawable.test_image));
        mainLandmarks.add(new Landmark("Obelisco a los Constituyentes de 1830","Esto es una descripcion",-34.897322, -56.164429, R.drawable.test_image));
        mainLandmarks.add(new Landmark("Monumento a Damoxeno","Esto es una descripcion",-34.896398, -56.162552, R.drawable.test_image));
        mainLandmarks.add(new Landmark("Obelisco a los Constituyentes de 1830","Esto es una descripcion",-34.897322, -56.164429, R.drawable.test_image));
        //--------

        View view = inflater.inflate(R.layout.fragment_landmark_list, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.cards_recyclerview_id);
        LandmarkCardAdapter adapter = new LandmarkCardAdapter(inflater.getContext(), mainLandmarks);
        recyclerView.setLayoutManager(new GridLayoutManager(inflater.getContext(),3));
        recyclerView.setAdapter(adapter);
        return view;

    }
}
