package com.acr.landmarks.ui;

import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.acr.landmarks.R;
import com.acr.landmarks.adapters.LandmarkCardAdapter;
import com.acr.landmarks.models.Landmark;
import com.acr.landmarks.models.LandmarkClusterMarker;
import com.acr.landmarks.service.LandmarksService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class LandmarkCardsFragment extends android.support.v4.app.Fragment implements LandmarkCardAdapter.LandmarkCardClickListener {

    private BottomSheetBehavior sheetBehavior;
    private LandmarkCardAdapter  adapter;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_landmark_list, container, false);

        initRecyclerView(inflater, view);

        sheetBehavior = MainActivity.getSheetBehavior();
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        return view;

    }

    private void initRecyclerView(LayoutInflater inflater, View view) {
        recyclerView = view.findViewById(R.id.cards_recyclerview_id);
        adapter = new LandmarkCardAdapter(inflater.getContext(), this, new LandmarksService());
        recyclerView.setLayoutManager(new GridLayoutManager(inflater.getContext(),3));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onLandmarkClicked(int position) {
        List<Landmark> landmarks = adapter.getLandmarks();
        String selectedLandmarkId = landmarks.get(position).getName();
        GoogleMap mMap = MapFragment.getMap();
        ArrayList<LandmarkClusterMarker> mClusterMarkers = MapFragment.getMarkers();

        for(LandmarkClusterMarker clusterMarker: mClusterMarkers){
            if(selectedLandmarkId.equals(clusterMarker.getLandmark().getName())){
                mMap.animateCamera(
                        CameraUpdateFactory.newLatLng(
                                new LatLng(clusterMarker.getPosition().latitude, clusterMarker.getPosition().longitude)),
                        600,
                        null
                );
                showBottomSheet(clusterMarker.getLandmark());
                break;
            }
        }

        TabLayout tabhost = getActivity().findViewById(R.id.tabs);
        tabhost.getTabAt(1).select();
    }

    private void showBottomSheet(Landmark selectedLandmark) {
        LinearLayout layoutBottomSheet = getActivity().findViewById(R.id.bottom_sheet_layout) ;
        ImageView sheetLandmarkImage =  layoutBottomSheet.findViewById(R.id.landmarkImage) ;
        TextView sheetLandmarkName =  layoutBottomSheet.findViewById(R.id.landmarkName) ;
        TextView sheetLandmarkDescription =  layoutBottomSheet.findViewById(R.id.landmarkDescription) ;
        TextView sheetLandmarkDistance =  layoutBottomSheet.findViewById(R.id.landmarkDistance) ;

        //IMAGEN POR DEFECTO, RESOLVER CARGA DE IMÁGENES DESDE BASE DE DATOS
        int avatar = R.drawable.test_image;

        sheetLandmarkImage.setImageResource(avatar);
        sheetLandmarkName.setText(selectedLandmark.getName());
        sheetLandmarkDescription.setText(selectedLandmark.getDescription());

        DecimalFormat FORMATTER = new DecimalFormat("0.###");
        String distance = FORMATTER.format(selectedLandmark.getDistance());
        distance += " Km";

        sheetLandmarkDistance.setText(distance);

        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
}
