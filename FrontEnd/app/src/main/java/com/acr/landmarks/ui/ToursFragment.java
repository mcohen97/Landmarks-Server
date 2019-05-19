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
import com.acr.landmarks.adapters.TourCardAdapter;
import com.acr.landmarks.models.Tour;
import com.acr.landmarks.view_models.ToursViewModel;

import java.util.ArrayList;
import java.util.List;

public class ToursFragment extends android.support.v4.app.Fragment implements TourCardAdapter.TourClickedListener  {

    private TourCardAdapter adapter;
    private RecyclerView recyclerView;
    private ToursViewModel viewModel;
    private List<Tour> data;
    private TourSelectedListener mListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(ToursViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tours, container, false);
        data = new ArrayList<>();
        //data= viewModel.getTours().getValue();
        initRecyclerView(inflater, view);
        viewModel.getTours().observe(this, tours -> {
            //Fragment and adapter share the same reference to data
            data.clear();
            data.addAll(tours);
            adapter.notifyDataSetChanged();
        });
        return view;
    }

    private void initRecyclerView(LayoutInflater inflater, View view) {
        recyclerView = view.findViewById(R.id.tour_cards_recyclerview_id);
        adapter = new TourCardAdapter(getContext(), this,data);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),1));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onTourClicked(Tour clicked) {
        mListener.onTourSelected(clicked);

    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            mListener= (TourSelectedListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()+" must implement "+ TourSelectedListener.class);
        }
    }
}
