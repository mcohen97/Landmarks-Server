package com.acr.landmarks.ui;

import android.Manifest;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.acr.landmarks.R;
import com.acr.landmarks.adapters.SectionsPagerAdapter;
import com.acr.landmarks.models.LandmarkFullInfo;
import com.acr.landmarks.models.LandmarkMarkerInfo;
import com.acr.landmarks.view_models.LandmarksViewModel;
import com.acr.landmarks.view_models.UserLocationViewModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import static com.acr.landmarks.Constants.ERROR_DIALOG_REQUEST;
import static com.acr.landmarks.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.acr.landmarks.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

public class MainActivity extends AppCompatActivity implements LandmarkSelectedListener{


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private static final String TAG = "Maps Activity";
    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationClient;

    private LocationCallback locationCallback;
    private UserLocationViewModel locationViewModel;
    private LandmarksViewModel landmarksViewModel;

    private BottomSheetBehavior mBottomSheetBehaviour;
    private Location mCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /* Create the adapter that will return a fragment for each of the three primary sections of the activity.*/
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        mViewPager.setCurrentItem(1);

        createLocationCallback();

        createBottomSheet();

        //locationService = LocationService.getInstance();
        locationViewModel = ViewModelProviders.of(this).get(UserLocationViewModel.class);
        landmarksViewModel = ViewModelProviders.of(this).get(LandmarksViewModel.class);
        landmarksViewModel.getSelectedLandmark().observe(this,selected ->
                addFullLandmarkInfo(selected));
    }

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                mCurrentLocation = locationResult.getLastLocation();
                locationViewModel.setLocation(mCurrentLocation);
            };
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapServicesAvailable()) {
            if (mLocationPermissionGranted) {
                startTrackingLocation();
            } else {
                getLocationPermission();
            }
        }
    }

    private void startTrackingLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,locationCallback,null);
    }

    private boolean mapServicesAvailable(){
        return isPlayServicesOK() && isMapsEnabled();
    }

    public boolean isPlayServicesOK(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void getLocationPermission() {
        if (hasPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            mLocationPermissionGranted = true;
            startTrackingLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private boolean hasPermission(String aPermission){
        return ContextCompat.checkSelfPermission(this.getApplicationContext(),
                aPermission)
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    onResume();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(mLocationPermissionGranted){
                    startTrackingLocation();
                }
                else{
                    getLocationPermission();
                }
            }
        }
    }

    private void createBottomSheet() {
        View bottomSheet = findViewById(R.id.bottom_sheet_layout);
        mBottomSheetBehaviour= BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLandmarkSelected(LandmarkMarkerInfo selectedLandmark) {
        addBasicInfo( selectedLandmark.title,selectedLandmark.latitude,selectedLandmark.longitude);
        addSingleImage(selectedLandmark.iconBase64);
        mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void addFullLandmarkInfo(LandmarkFullInfo landmark){
        addBasicInfo(landmark.title, landmark.latitude,landmark.longitude);
        LinearLayout layoutBottomSheet= findViewById(R.id.bottom_sheet_layout) ;
        TextView sheetLandmarkDescription =  layoutBottomSheet.findViewById(R.id.landmarkDescription) ;
        sheetLandmarkDescription.setText(landmark.description);
    }


    //The main usage of this method is to fill the drawer with info while waiting for the full landmark information.
    private void addBasicInfo(String title, double latitude, double longitude){
        LinearLayout layoutBottomSheet= findViewById(R.id.bottom_sheet_layout) ;
        ImageView sheetLandmarkImage =  layoutBottomSheet.findViewById(R.id.landmarkImage) ;
        TextView sheetLandmarkName =  layoutBottomSheet.findViewById(R.id.landmarkName) ;
        TextView sheetLandmarkDistance =  layoutBottomSheet.findViewById(R.id.landmarkDistance) ;



        sheetLandmarkName.setText(title);
        //sheetLandmarkDescription.setText(selectedLandmark.getDescription());

        String distance = ""+ (mCurrentLocation.distanceTo(createLocation(latitude,longitude))/1000);
        distance += " Km";

        sheetLandmarkDistance.setText(distance);
    }

    private void addSingleImage(String image){
        LinearLayout layoutBottomSheet= findViewById(R.id.bottom_sheet_layout) ;
        ImageView sheetLandmarkImage =  layoutBottomSheet.findViewById(R.id.landmarkImage) ;

        byte[] imageData = Base64.decode(image, Base64.DEFAULT);
        Bitmap landmark = BitmapFactory.decodeByteArray(imageData,0,imageData.length);
        sheetLandmarkImage.setImageBitmap(landmark);
    }

    private Location createLocation( double lat, double lng){
        Location conversion = new Location(new String());
        conversion.setLatitude(lat);
        conversion.setLongitude(lng);
        return conversion;
    }
}
