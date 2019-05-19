package com.acr.landmarks.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.acr.landmarks.R;
import com.acr.landmarks.adapters.LandmarkCardAdapter;
import com.acr.landmarks.models.LandmarkMarkerInfo;
import com.acr.landmarks.view_models.LandmarksViewModel;

import java.util.ArrayList;
import java.util.List;


public class LandmarkCardsFragment extends android.support.v4.app.Fragment implements LandmarkCardAdapter.LandmarkCardClickListener {

    private LandmarkCardAdapter  adapter;
    private RecyclerView recyclerView;
    private LandmarksViewModel viewModel;
    private List<LandmarkMarkerInfo> data;

    private LandmarkSelectedListener mListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(LandmarksViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_landmark_list, container, false);
        data= new ArrayList<LandmarkMarkerInfo>();

        initRecyclerView(inflater, view);
        viewModel.getLandmarks().observe(this, landmarks -> {
            //Fragment and adapter share the same reference to data
            data.clear();;
            data.addAll(landmarks);
            adapter.notifyDataSetChanged();
        });
        return view;
    }

    private void initRecyclerView(LayoutInflater inflater, View view) {
        recyclerView = view.findViewById(R.id.cards_recyclerview_id);
        adapter = new LandmarkCardAdapter(getContext(), this,data);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onLandmarkClicked(LandmarkMarkerInfo clicked) {
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
