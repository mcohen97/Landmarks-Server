package com.acr.landmarks.ui;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.os.Handler;
import android.widget.Toast;

import com.acr.landmarks.R;
import com.acr.landmarks.models.Landmark;
import com.acr.landmarks.models.LandmarkClusterMarker;
import com.acr.landmarks.models.PolylineData;
import com.acr.landmarks.models.Tour;
import com.acr.landmarks.services.PicassoImageService;
import com.acr.landmarks.services.contracts.IImageService;
import com.acr.landmarks.util.ClusterManagerRenderer;
import com.acr.landmarks.util.Config;
import com.acr.landmarks.view_models.LandmarksViewModel;
import com.acr.landmarks.view_models.ToursViewModel;
import com.acr.landmarks.view_models.UserLocationViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.util.ArrayList;
import java.util.List;

import static com.acr.landmarks.Constants.MAPVIEW_BUNDLE_KEY;


public class MapFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener,
        GoogleMap.OnPolylineClickListener, ClusterManager.OnClusterItemInfoWindowClickListener<LandmarkClusterMarker>,
        GoogleMap.OnCameraIdleListener,ClusterManager.OnClusterItemClickListener<LandmarkClusterMarker> {

    private final String TAG = "MapFragment";
    private LandmarkSelectedListener mListener;

    private MapView mMapView;

    //location y camera update
    private static GoogleMap mMap;
    public static Location mUserLocation;
    private static final int DEFAULT_ZOOM = 15;
    private boolean firstCameraMovement;

    //Clustering
    private ClusterManager<LandmarkClusterMarker> mClusterManager;
    private ClusterManagerRenderer mClusterManagerRenderer;
    private  ArrayList<LandmarkClusterMarker> mClusterMarkers;

    private List<Landmark> mLandmarks;
    private List<Tour> mTours;

    //Directions
    private GeoApiContext mGeoApiContext;
    private ArrayList<PolylineData> mPolyLinesData = new ArrayList<>();

    //ViewModels
    private LandmarksViewModel landmarksViewModel;
    private UserLocationViewModel locationViewModel;
    private ToursViewModel toursViewModel;

    private LandmarkClusterMarker mSelectedMarker;
    private ArrayList<Marker> mTripMarkers = new ArrayList<>();
    private IImageService imageService;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageService = new PicassoImageService(Config.getConfigValue(getContext(),"api_url"));
        mUserLocation= null;
        mClusterMarkers = new ArrayList<>();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (LandmarkSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement " + LandmarkSelectedListener.class);
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

        mLandmarks = new ArrayList<Landmark>();
        mTours = new ArrayList<Tour>();

        landmarksViewModel = ViewModelProviders.of(getActivity()).get(LandmarksViewModel.class);
        toursViewModel = ViewModelProviders.of(getActivity()).get(ToursViewModel.class);
        locationViewModel = ViewModelProviders.of(getActivity()).get(UserLocationViewModel.class);
        firstCameraMovement = false;

        return view;
    }

    private void initGoogleMap(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);
        if (mGeoApiContext == null) {
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_map_api_key))
                    .build();
        }
    }

    private void setCameraView() {
        float currentZoom = mMap.getCameraPosition().zoom;
        setCameraViewWithZoom(currentZoom);
    }

    private void setCameraViewWithZoom(float zoom) {
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
        map.setOnPolylineClickListener(this);
        mMap = map;
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this.getContext(), R.raw.map_style));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        locationViewModel.getLocation().observe(this, location -> {
            boolean firstLocation =mUserLocation == null;

            mUserLocation = location;

            if(firstLocation){
                setCameraViewWithZoom(DEFAULT_ZOOM);
                Double radius = new Double(getMapRangeRadius());
                landmarksViewModel.setGeofence(location,radius);
                //toursViewModel.setGeofence(location,new Double(getMapRangeRadius()));
                mMap.setOnCameraIdleListener(this);
                //mMap.setOnCameraMoveStartedListener(this);
            }

        });

        landmarksViewModel.getLandmarks().observe(this, landmarks -> {
            mLandmarks = landmarks;
            Log.d(TAG,"Received "+landmarks.size()+" landmarks");
            if(!landmarks.isEmpty()) {
                addMapMarkers();
            }
        });

        toursViewModel.getSelectedTour().observe(this, tour -> {
            if (tour != null) {
                drawTour(tour);
            } else {
                resetTheMap();
                addMapMarkers();
            }

        });

        landmarksViewModel.getAskedForDirections().observe(this, isAsked ->{
            if (isAsked ) {
                //resetTheMap();
                if(isTourSelected()){
                    drawTour(toursViewModel.getSelectedTour().getValue());
                }
                LandmarkClusterMarker marker = mSelectedMarker;
                if(marker == null) {
                    Landmark selected = landmarksViewModel.getSelectedLandmark().getValue();
                    marker = getLandmarksMarker(selected);
                }
                if(marker != null) {
                    resetRoutes();
                    calculateDirections(marker);
                }
            }

        });
        setUpClusterManager();
    }

    private LandmarkClusterMarker getLandmarksMarker(Landmark landmark) {
        for(LandmarkClusterMarker marker: mClusterMarkers){
            if(marker.getLandmark().equals(landmark)){
                return marker;
            }
        }
        return null;
    }

    private boolean isTourSelected() {
        return toursViewModel.getSelectedTour().getValue()!=null;
    }


    private float getMapRangeRadius() {
        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        Location center = new Location(new String());
        center.setLatitude(bounds.getCenter().latitude);
        center.setLongitude(bounds.getCenter().longitude);

        Location northEast = new Location(new String());
        northEast.setLatitude(bounds.northeast.latitude);
        northEast.setLongitude(bounds.northeast.longitude);

        float radiusInMeters = center.distanceTo(northEast);
        float radiusInKm = radiusInMeters / 1000;

        return radiusInKm;
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
        switch (v.getId()) {
            case R.id.btn_reset_map: {
                addMapMarkers();
                break;
            }
        }
    }

    private void addMapMarkers() {
        if (mMap == null) {
            return;
        }
        resetMapPolylines();
        boolean firstTime = mClusterManager == null;
        if (firstTime) {
            setUpClusterManager();
        }
        if (mClusterManagerRenderer == null) {
            setUpClusterManagerRenderer();
        }

        updateMapMarkers();
    }

    private void setUpClusterManager() {
        mClusterManager = new ClusterManager<LandmarkClusterMarker>(getActivity().getApplicationContext(), mMap);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);

        mClusterManager.setOnClusterItemInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
    }

    private void setUpClusterManagerRenderer() {
        mClusterManagerRenderer = new ClusterManagerRenderer(
                getActivity(),
                mMap,
                mClusterManager
        );
        mClusterManager.setRenderer(mClusterManagerRenderer);
    }

    private void updateMapMarkers() {

        int landmarkIndex =0;
        for (Landmark landmark : mLandmarks) {
            try {
                addMarker(landmark,mLandmarks.size(),landmarkIndex);
                landmarkIndex++;
            } catch (NullPointerException e) {
                Log.e(TAG, "addMapMarkers: NullPointerException: " + e.getMessage());
            }
        }
        //mClusterManager.cluster();
    }

    private void addMarker(Landmark landmark, int size, int landmarkIndex) {

        boolean alreadyInMap = isMarkerInMap(landmark);
        Log.d(TAG,"ya se encuentra en el mapa: "+ alreadyInMap);
        if (!alreadyInMap) {
            Log.d(TAG, "Loading marker "+ landmarkIndex);
            String snippet = "Determine route to " + landmark.title + "?";
            LandmarkClusterMarker newClusterMarker = new LandmarkClusterMarker(
                    new LatLng(landmark.latitude, landmark.longitude),
                    landmark.title,
                    snippet,
                    null,
                    landmark
            );
            mClusterMarkers.add(newClusterMarker);
            imageService.loadBitmap(landmark.imageFiles[0], new IImageService.ImageLoadListener() {
                @Override
                public void onImageLoaded(Bitmap img) {
                    newClusterMarker.setIconPicture(img);
                    mClusterManager.addItem(newClusterMarker);
                    Log.d(TAG, "Loaded marker "+ landmarkIndex);
                    //if(landmarkIndex == (size-1) ){
                        mClusterManager.cluster();
                        Log.d(TAG,"Markers loaded");
                    //}
                }
            });
        }
    }

    /*private void removeUselessMarkers() {
        List<LandmarkClusterMarker> auxLandmarks= (List<LandmarkClusterMarker>) mClusterMarkers.clone();
        for(LandmarkClusterMarker marker: auxLandmarks){
            if(!markerContainsLandmarkInRange(marker)){
                mClusterMarkers.remove(marker);
                mClusterManager.removeItem(marker);
            }
        }

    }*/

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


    private void calculateDirections(LandmarkClusterMarker marker){
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
                int x =2;
            }
        });
    }

    private void addPolylinesToMap(final DirectionsResult result){
        new Handler(Looper.getMainLooper()).post(() -> {

            //Evitar polylines duplicadas, en mapa y lista -> controlar no borrar lineas del Tour ni landmarks al pedo
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
                //mClusterManager.removeItem(mSelectedMarker); Para cambiar el marker por otro
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

    private void resetMapPolylines(){
        if(mMap != null) {

            if(mPolyLinesData.size() > 0){
                mPolyLinesData.clear();
                mPolyLinesData = new ArrayList<>();
            }
        }
    }

    @Override
    public void onClusterItemInfoWindowClick(LandmarkClusterMarker landmarkClusterMarker) {
        landmarksViewModel.setSelectedLandmark(landmarkClusterMarker.getLandmark().id);
        mSelectedMarker = landmarkClusterMarker;
        mListener.onLandmarkSelected(landmarkClusterMarker.getLandmark());
    }
    @Override
    public boolean onClusterItemClick(LandmarkClusterMarker landmarkClusterMarker) {
        landmarksViewModel.setSelectedLandmark(landmarkClusterMarker.getLandmark().id);
        mSelectedMarker = landmarkClusterMarker;
        if(isTourSelected() && isPartOfSelectedTour(landmarkClusterMarker)){
            //resetear mapa por la cant de clicks
            resetTheMap();
            drawTour(toursViewModel.getSelectedTour().getValue());
            resetRoutes();
            calculateDirections(landmarkClusterMarker);
        }

        return false;
    }

    private boolean isPartOfSelectedTour(LandmarkClusterMarker landmarkClusterMarker) {
        Tour selectedTour = toursViewModel.getSelectedTour().getValue();

        for (int landmarkId : selectedTour.landmarksIds){
            if(landmarkId == landmarkClusterMarker.getLandmark().id){
                return true;
            }
        }
        return false;
    }


    @Override
    public void onCameraIdle() {
        if(firstCameraMovement) {
            float newRadius = getMapRangeRadius();
            LatLng center = mMap.getCameraPosition().target;
            landmarksViewModel.setGeofence(latLngToLocation(center), new Double(newRadius));
        }else{
            firstCameraMovement=true;
        }
        mClusterManager.cluster();//Nuevo
    }

    private Location latLngToLocation(LatLng googleData) {
        Location conversion = new Location(new String());
        conversion.setLatitude(googleData.latitude);
        conversion.setLongitude(googleData.longitude);
        return conversion;
    }

    //Tours
    public void drawTour(Tour selected) {
        addMapMarkers();

        ArrayList<Integer> landmarksIds = selected.landmarksIds;
        ArrayList<Landmark> landmarks = new ArrayList<Landmark>();

        for (Integer id : landmarksIds) {
            for (Landmark landmark : mLandmarks) {
                if (landmark.id == id) {
                    landmarks.add(landmark);
                }
            }
        }

        PolylineOptions options = new PolylineOptions();
        options.color(Color.RED);

        for(Landmark lm: landmarks){
            options.add(new LatLng(lm.latitude,lm.longitude));
        }


        ArrayList<PatternItem> linePattern = new ArrayList<PatternItem>();
        //linePattern.add(new Gap(2));
        //options.pattern(linePattern);


        Polyline polyline = mMap.addPolyline(options);
        //polyline.setColor(ContextCompat.getColor(getActivity(), R.color.red1));
        //polyline.setPattern(linePattern);
        polyline.setClickable(true);
        //mPolyLinesData.add(new PolylineData(polyline)); no hay ruta en este caso

        zoomRoute(polyline.getPoints());
    }

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

            if (mPolyLinesData.size() > 0) {
                mPolyLinesData.clear();
                mPolyLinesData = new ArrayList<>();
            }
        }
    }

    private void resetRoutes(){
        if (mPolyLinesData.size() > 0) {
            for(PolylineData data : mPolyLinesData){
                data.getPolyline().remove();
            }
            mPolyLinesData.clear();
            mPolyLinesData = new ArrayList<>();
        }
    }



}
