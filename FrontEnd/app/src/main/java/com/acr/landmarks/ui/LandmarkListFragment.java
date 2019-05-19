package com.acr.landmarks.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.acr.landmarks.R;
import com.acr.landmarks.models.LandmarkMarkerInfo;

import java.util.List;

import com.acr.landmarks.adapters.LandmarkListAdapter;


public class LandmarkListFragment extends Fragment implements
        LandmarkListAdapter.LandmarkListRecyclerClickListener {

    private RecyclerView mLandmarkListRecyclerView;
    private LandmarkListAdapter mLandmarkListRecyclerAdapter;
    private List<LandmarkMarkerInfo> mLandmarks;

    //Bottom sheet
    //BottomSheetBehavior sheetBehavior;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_landmark_list, container, false);
        updateLandmarks();
        mLandmarkListRecyclerView = view.findViewById(R.id.cards_recyclerview_id);
        initLandmarkListRecyclerView();
        return view;
    }

    private void initLandmarkListRecyclerView() {
        mLandmarkListRecyclerAdapter = new LandmarkListAdapter(mLandmarks, this);
        mLandmarkListRecyclerView.setAdapter(mLandmarkListRecyclerAdapter);
        mLandmarkListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    //Hardcoded
    private void updateLandmarks() {
   //     mLandmarks = MainActivity.getLandmarks();
    }

    @Override
    public void onLandmarkClicked(int position) {
        /*String selectedLandmarkId = mLandmarks.get(position).getName();
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
                //showBottomSheet(clusterMarker.getLandmark());
                break;
            }
        }

        TabLayout tabhost = (TabLayout) getActivity().findViewById(R.id.tabs);
        tabhost.getTabAt(1).select();*/

    }

    /*private void showBottomSheet(LandmarkMarkerInfo selectedLandmark) {
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
    }*/
}
