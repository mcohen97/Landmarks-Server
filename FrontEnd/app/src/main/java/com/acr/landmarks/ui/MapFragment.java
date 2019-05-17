package com.acr.landmarks.ui;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TimingLogger;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.acr.landmarks.R;
import com.acr.landmarks.models.Landmark;
import com.acr.landmarks.models.LandmarkClusterMarker;
import com.acr.landmarks.models.PolylineData;
import com.acr.landmarks.models.Tour;
import com.acr.landmarks.services.LocationService;
import com.acr.landmarks.services.contracts.ILocationService;
import com.acr.landmarks.services.contracts.MapCommunicator;
import com.acr.landmarks.util.ClusterManagerRenderer;
import com.acr.landmarks.view_models.LandmarksViewModel;
import com.acr.landmarks.view_models.UserLocationViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static com.acr.landmarks.Constants.MAPVIEW_BUNDLE_KEY;


public class MapFragment extends Fragment implements OnMapReadyCallback , View.OnClickListener ,
         GoogleMap.OnPolylineClickListener, ClusterManager.OnClusterItemInfoWindowClickListener<LandmarkClusterMarker>, MapCommunicator {
         GoogleMap.OnPolylineClickListener,
        ClusterManager.OnClusterItemInfoWindowClickListener<LandmarkClusterMarker>,
        GoogleMap.OnCameraIdleListener {

    private final String TAG = "MapFragment";
    private LandmarkSelectedListener mListener;

    private MapView mMapView;

    //location y camera update
    private static GoogleMap mMap;
    public static Location mUserLocation;
    private static final int DEFAULT_ZOOM = 15;

    //Clustering
    private ClusterManager<LandmarkClusterMarker> mClusterManager;
    private ClusterManagerRenderer mClusterManagerRenderer;
    private static ArrayList<LandmarkClusterMarker> mClusterMarkers = new ArrayList<>();
    private List<Landmark> mLandmarks;

    //Directions
    private GeoApiContext mGeoApiContext;
    private ArrayList<PolylineData> mPolyLinesData = new ArrayList<>();

    //ViewModels
    private LandmarksViewModel  landmarksViewModel;
    private UserLocationViewModel locationViewModel;


    private Marker mSelectedMarker;
    private ArrayList<Marker> mTripMarkers = new ArrayList<>();



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = view.findViewById(R.id.fragmented_map);

        initGoogleMap(savedInstanceState);

        view.findViewById(R.id.btn_reset_map).setOnClickListener(this);
        RelativeLayout mMapContainer = view.findViewById(R.id.map_container);
        mLandmarks= new ArrayList<Landmark>();


        landmarksViewModel = ViewModelProviders.of(getActivity()).get(LandmarksViewModel.class);
        locationViewModel = ViewModelProviders.of(getActivity()).get(UserLocationViewModel.class);

        return view;
    }

    private void initGoogleMap(Bundle savedInstanceState){
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);
        if(mGeoApiContext == null){
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_map_api_key))
                    .build();
        }
    }

    private void setCameraView() {
        float currentZoom = mMap.getCameraPosition().zoom;
        setCameraViewWithZoom(currentZoom);
    }

    private void setCameraViewWithZoom(float zoom){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mUserLocation.getLatitude(),
                        mUserLocation.getLongitude()), zoom));
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
            return;
        }
        map.setMyLocationEnabled(true);
        mMap = map;

        locationViewModel.getLocation().observe(this, location -> {
            boolean firstLocation =mUserLocation == null;

            mUserLocation = location;

            if(firstLocation){
                setCameraViewWithZoom(DEFAULT_ZOOM);
                landmarksViewModel.setGeofence(location,new Double(getMapRangeRadius()));
                mMap.setOnCameraIdleListener(this);
                //mMap.setOnCameraMoveStartedListener(this);
            }

        });

        landmarksViewModel.getLandmarks().observe(this, landmarks -> {
            mLandmarks = landmarks;
            addMapMarkers();
        });
    }


    private float getMapRangeRadius() {
        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        Location center = new Location(new String());
        center.setLatitude(bounds.getCenter().latitude);
        center.setLongitude(bounds.getCenter().longitude);

        Location northEast = new Location(new String());
        northEast.setLatitude(bounds.northeast.latitude);
        northEast.setLongitude(bounds.northeast.longitude);

        float radiusInMeters= center.distanceTo(northEast);
        float radiusInKm = radiusInMeters/1000;

        return  radiusInKm;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_reset_map:{
                addMapMarkers();
                break;
            }
        }
    }

    private void addMapMarkers(){
        if(mMap == null){
            return;
        }
        resetMap();
        boolean firstTime = mClusterManager == null;
        if(firstTime){
            setUpClusterManager();
        }
        if(mClusterManagerRenderer == null){
            setUpClusterManagerRenderer();
        }

        updateMapMarkers();
    }

    private void setUpClusterManager(){
        mClusterManager = new ClusterManager<LandmarkClusterMarker>(getActivity().getApplicationContext(), mMap);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);
    }

    private void setUpClusterManagerRenderer(){
        mClusterManagerRenderer = new ClusterManagerRenderer(
                getActivity(),
                mMap,
                mClusterManager
        );
        mClusterManager.setRenderer(mClusterManagerRenderer);
    }

    private void updateMapMarkers() {
        //mClusterManager.clearItems();
        //mClusterMarkers.clear();

        for(Landmark landmark: mLandmarks){
            try{
                addMarker(landmark);
            }catch (NullPointerException e){
                Log.e(TAG, "addMapMarkers: NullPointerException: " + e.getMessage() );
            }
        }
        mClusterManager.cluster();
        //removeUselessMarkers();
    }

    private void addMarker(Landmark landmark) {

        boolean alreadyInMap = isMarkerInMap(landmark);
        if(!alreadyInMap) {
            String snippet = "Determine route to " + landmark.getName() + "?";
            LandmarkClusterMarker newClusterMarker = new LandmarkClusterMarker(
                    new LatLng(landmark.getLat(), landmark.getLon()),
                    landmark.getName(),
                    snippet,
                    landmark.getImg(),
                    landmark
            );
            mClusterManager.addItem(newClusterMarker);
            mClusterMarkers.add(newClusterMarker);
        }
    }

    private void removeUselessMarkers() {
        List<LandmarkClusterMarker> auxLandmarks= (List<LandmarkClusterMarker>) mClusterMarkers.clone();
        for(LandmarkClusterMarker marker: auxLandmarks){
            if(!markerContainsLandmarkInRange(marker)){
                mClusterMarkers.remove(marker);
                mClusterManager.removeItem(marker);
            }
        }

    }

    private boolean markerContainsLandmarkInRange(LandmarkClusterMarker marker) {
        return mLandmarks.contains(marker.getLandmark());
    }

    private boolean isMarkerInMap(Landmark landmark) {
        for(LandmarkClusterMarker marker :mClusterMarkers){
            if(marker.getLandmark().equals(landmark)){
                return true;
            }
        }
        return false;
    }

    private void calculateDirections(Marker marker){

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        directions.alternatives(true);
        directions.origin(
                new com.google.maps.model.LatLng(
                        mUserLocation.getLatitude(),
                        mUserLocation.getLongitude()
                )
        );
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {

            }
        });
    }

    private void addPolylinesToMap(final DirectionsResult result){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                //Evitar polylines duplicadas, en mapa y lista
                if(mPolyLinesData.size() > 0){
                    for(PolylineData polylineData: mPolyLinesData){
                        polylineData.getPolyline().remove();
                    }
                    mPolyLinesData.clear();
                    mPolyLinesData = new ArrayList<>();
                }

                double duration = 999999999;
                for(DirectionsRoute route: result.routes){

                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for(com.google.maps.model.LatLng latLng: decodedPath){

                        //Log.d(TAG, "run: latlng: " + latLng.toString());

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(getActivity(), R.color.darkGrey));
                    polyline.setClickable(true);
                    mPolyLinesData.add(new PolylineData(polyline,route.legs[0]));

                    // highlight the fastest route and adjust camera
                    double tempDuration = route.legs[0].duration.inSeconds;
                    if(tempDuration < duration){
                        duration = tempDuration;
                        onPolylineClick(polyline);
                        zoomRoute(polyline.getPoints());
                    }

                    mSelectedMarker.setVisible(false);
                }
            }


        });
    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        int index = 0;
        for(PolylineData polylineData: mPolyLinesData){
            index++;

            if(polyline.getId().equals(polylineData.getPolyline().getId())){
                polylineData.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.blue1));
                polylineData.getPolyline().setZIndex(1);

                LatLng endLocation = new LatLng(
                        polylineData.getLeg().endLocation.lat,
                        polylineData.getLeg().endLocation.lng
                );

                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(endLocation)
                        .title("Trip #" + index)
                        .snippet("Duration: " + polylineData.getLeg().duration
                        ));


                marker.showInfoWindow();
                mTripMarkers.add(marker);
            }
            else{
                polylineData.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.darkGrey));
                polylineData.getPolyline().setZIndex(0);
            }
        }
    }

    private void removeTripMarkers(){
        for(Marker marker: mTripMarkers){
            marker.remove();
        }
    }

    public void zoomRoute(List<LatLng> lstLatLngRoute) {

        if (mMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 120;
        LatLngBounds latLngBounds = boundsBuilder.build();

        mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null
        );
    }

    private void resetMap(){
        if(mMap != null) {

            if(mPolyLinesData.size() > 0){
                mPolyLinesData.clear();
                mPolyLinesData = new ArrayList<>();
            }
        }
    }

    @Override
    public void onClusterItemInfoWindowClick(LandmarkClusterMarker landmarkClusterMarker) {
        mListener.onLandmarkSelected(landmarkClusterMarker.getLandmark());
    }

    //Tours
    @Override
    public void drawTour(Tour selected) {
        addMapMarkers();

        ArrayList<Integer> landmarksIds = selected.getTourLandmarks();
        ArrayList<Landmark> landmarks =new ArrayList<Landmark>();

        for(Integer id : landmarksIds){
            for(Landmark landmark:mLandmarks){
                if(landmark.getId()== id){
                    landmarks.add(landmark);
                }
            }
        }

        PolylineOptions options = new PolylineOptions();

        for(Landmark lm: landmarks){
            options.add(new LatLng(lm.getLat(),lm.getLon()));
        }
        options.color(Color.RED);
        options.width(5);

        ArrayList<PatternItem> linePattern = new ArrayList<PatternItem>();
        linePattern.add(new Gap(2));
        options.pattern(linePattern);


    }

    @Override
    public void resetTheMap() {
        if(mMap != null) {
            mMap.clear();

            if(mClusterManager != null){
                mClusterManager.clearItems();
            }

            if (mClusterMarkers.size() > 0) {
                mClusterMarkers.clear();
                mClusterMarkers = new ArrayList<>();
            }

            if(mPolyLinesData.size() > 0){
                mPolyLinesData.clear();
                mPolyLinesData = new ArrayList<>();
            }
        }
    }

    @Override
    public void onCameraIdle() {
        float newRadius = getMapRangeRadius();
        LatLng center = mMap.getCameraPosition().target;
        landmarksViewModel.setGeofence(latLngToLocation(center),new Double(newRadius));
    }

    private Location latLngToLocation(LatLng googleData){
        Location conversion = new Location(new String());
        conversion.setLatitude(googleData.latitude);
        conversion.setLongitude(googleData.longitude);
        return conversion;
    }

}
