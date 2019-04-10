package com.acr.landmarks.ui;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.acr.landmarks.R;
import com.acr.landmarks.models.Landmark;
import com.acr.landmarks.models.LandmarkClusterMarker;
import com.acr.landmarks.util.ClusterManagerRenderer;
import com.acr.landmarks.util.ViewWeightAnimationWrapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;

import static com.acr.landmarks.Constants.MAPVIEW_BUNDLE_KEY;


public class MapFragment extends Fragment implements OnMapReadyCallback , View.OnClickListener {

    private static final String TAG = "UserListFragment";

    //widgets
    private RecyclerView mLandmarkListRecyclerView;
    private MapView mMapView;

    //location y camara update
    private GoogleMap mMap;
    private LatLngBounds mMapBoundary;
    public static Location mUserLocation;
    private RelativeLayout mMapContainer;

    //Click variables

    private static final int MAP_LAYOUT_STATE_CONTRACTED = 0;
    private static final int MAP_LAYOUT_STATE_EXPANDED = 1;
    private int mMapLayoutState = 0;

    //Clustering
    private ClusterManager<LandmarkClusterMarker> mClusterManager;
    private ClusterManagerRenderer mClusterManagerRenderer;
    private ArrayList<LandmarkClusterMarker> mClusterMarkers = new ArrayList<>();
    private ArrayList<Landmark> mLandmarks = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mLandmarkListRecyclerView = view.findViewById(R.id.landmark_list_recycler_view);
        mMapView = view.findViewById(R.id.fragmented_map);
        initGoogleMap(savedInstanceState);
        view.findViewById(R.id.btn_full_screen_map).setOnClickListener(this);
        mMapContainer = view.findViewById(R.id.map_container);
        return view;
    }

    private void initGoogleMap(Bundle savedInstanceState){
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);
    }

    private void setCameraView() {
        mUserLocation = MainActivity.mUserLocation;
        // Set a boundary to start
        double bottomBoundary = mUserLocation.getLatitude() - .1;
        double leftBoundary = mUserLocation.getLongitude() - .1;
        double topBoundary = mUserLocation.getLatitude() + .1;
        double rightBoundary = mUserLocation.getLongitude() + .1;

        mMapBoundary = new LatLngBounds(
                new LatLng(bottomBoundary, leftBoundary),
                new LatLng(topBoundary, rightBoundary)
        );

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 0));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);
        mMap = map;
        addMapMarkers();
    }


    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    //Animation
    private void expandMapAnimation(){
        ViewWeightAnimationWrapper mapAnimationWrapper = new ViewWeightAnimationWrapper(mMapContainer);
        ObjectAnimator mapAnimation = ObjectAnimator.ofFloat(mapAnimationWrapper,
                "weight",
                50,
                100);
        mapAnimation.setDuration(800);

        ViewWeightAnimationWrapper recyclerAnimationWrapper = new ViewWeightAnimationWrapper(mLandmarkListRecyclerView);
        ObjectAnimator recyclerAnimation = ObjectAnimator.ofFloat(recyclerAnimationWrapper,
                "weight",
                50,
                0);
        recyclerAnimation.setDuration(800);

        recyclerAnimation.start();
        mapAnimation.start();
    }

    private void contractMapAnimation(){
        ViewWeightAnimationWrapper mapAnimationWrapper = new ViewWeightAnimationWrapper(mMapContainer);
        ObjectAnimator mapAnimation = ObjectAnimator.ofFloat(mapAnimationWrapper,
                "weight",
                100,
                50);
        mapAnimation.setDuration(800);

        ViewWeightAnimationWrapper recyclerAnimationWrapper = new ViewWeightAnimationWrapper(mLandmarkListRecyclerView);
        ObjectAnimator recyclerAnimation = ObjectAnimator.ofFloat(recyclerAnimationWrapper,
                "weight",
                0,
                50);
        recyclerAnimation.setDuration(800);

        recyclerAnimation.start();
        mapAnimation.start();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_full_screen_map:{

                if(mMapLayoutState == MAP_LAYOUT_STATE_CONTRACTED){
                    mMapLayoutState = MAP_LAYOUT_STATE_EXPANDED;
                    expandMapAnimation();
                }
                else if(mMapLayoutState == MAP_LAYOUT_STATE_EXPANDED){
                    mMapLayoutState = MAP_LAYOUT_STATE_CONTRACTED;
                    contractMapAnimation();
                }
                break;
            }

        }
    }

    private void addMapMarkers(){
        downloadLandmarks();//Hardcoded ver donde colocarlo para el flujo correcto
        if(mMap != null){

            if(mClusterManager == null){
                mClusterManager = new ClusterManager<LandmarkClusterMarker>(getActivity().getApplicationContext(), mMap);
            }
            if(mClusterManagerRenderer == null){
                mClusterManagerRenderer = new ClusterManagerRenderer(
                        getActivity(),
                        mMap,
                        mClusterManager
                );
                mClusterManager.setRenderer(mClusterManagerRenderer);
            }

            for(Landmark landmark: mLandmarks){

                try{
                    String snippet = "";

                    snippet = "Determine route to " + landmark.getName() + "?";

                    int avatar = R.drawable.test_image; // set the default avatar
                    /* por ahora seteo la imagen poor defecto queda a implementar el cargar la imagen
                    try{
                        avatar = Integer.parseInt(landmark.getImg());
                    }catch (NumberFormatException e){
                        Log.d(TAG, "addMapMarkers: no avatar for " + landmark.getName() + ", setting default.");
                    }*/
                    LandmarkClusterMarker newClusterMarker = new LandmarkClusterMarker(
                            new LatLng(landmark.getLat(), landmark.getLon()),
                            landmark.getName(),
                            snippet,
                            avatar,
                            landmark
                    );
                    mClusterManager.addItem(newClusterMarker);
                    mClusterMarkers.add(newClusterMarker);

                }catch (NullPointerException e){
                    Log.e(TAG, "addMapMarkers: NullPointerException: " + e.getMessage() );
                }

            }
            mClusterManager.cluster();

            setCameraView();
        }
    }
    //Hardcoded
    private void downloadLandmarks() {
        Landmark lm1 = new Landmark("Prueba1","Landmark de prueba 1",-34.859270,-56.034038);
        Landmark lm2 = new Landmark("Prueba2","Landmark de prueba 2",-34.859258,-56.030105);
        Landmark lm3 = new Landmark("Prueba3","Landmark de prueba 3",-34.864186,-56.027584);
        mLandmarks.add(lm1);
        mLandmarks.add(lm2);
        mLandmarks.add(lm3);
    }

}
