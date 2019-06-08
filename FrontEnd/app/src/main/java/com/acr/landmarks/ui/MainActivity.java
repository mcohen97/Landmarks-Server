package com.acr.landmarks.ui;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.Toast;

import com.acr.landmarks.ConnectivityReceiver;
import com.acr.landmarks.R;
import com.acr.landmarks.adapters.SectionsPagerAdapter;
import com.acr.landmarks.models.Tour;
import com.acr.landmarks.models.Landmark;
import com.acr.landmarks.services.LocationUpdatesService;
import com.acr.landmarks.view_models.LandmarksViewModel;
import com.acr.landmarks.view_models.ToursViewModel;
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

public class MainActivity extends AppCompatActivity implements LandmarkSelectedListener, TourSelectedListener {


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationClient;
    private ConnectivityReceiver mConnectionMonitor;

    private LocationCallback locationCallback;
    private UserLocationViewModel locationViewModel;
    private LandmarksViewModel landmarksViewModel;
    private ToursViewModel toursViewModel;

    private Location mCurrentLocation;
    private BottomSheetManager mBottomSheetManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /* Create the adapter that will return a fragment for each of the three primary sections of the activity.*/
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        createViewPager();
        mBottomSheetManager = new BottomSheetManager(this);
        setUpBottomSheetManager();
        setViewPager();
        createLocationCallback();
        setViewModels();
        setConnectivityMonitor();
    }

    private void setViewModels() {
        locationViewModel = ViewModelProviders.of(this).get(UserLocationViewModel.class);
        landmarksViewModel = ViewModelProviders.of(this).get(LandmarksViewModel.class);
        toursViewModel = ViewModelProviders.of(this).get(ToursViewModel.class);
        landmarksViewModel.getSelectedLandmark().observe( this,
                landmark -> onLandmarkSelected(landmark) );
    }

    private void createViewPager() {
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }
    private void setViewPager() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mBottomSheetManager.hideSheetIfExpanded();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (toursViewModel != null && tab.getPosition() == 1) {
                    toursViewModel.setSelectedTour(-1);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        mViewPager.setCurrentItem(1);
    }

    private void setUpBottomSheetManager() {
        mBottomSheetManager.setDirectionsButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TabLayout tabs = findViewById(R.id.tabs);
                TabLayout.Tab tab = tabs.getTabAt(1);
                tab.select();
                mBottomSheetManager.hideSheetIfExpanded();
                landmarksViewModel.getAskedForDirections().postValue(true);
            }
        });
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
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapServicesAvailable()) {
            if (mLocationPermissionGranted) {
                startTrackingLocation();
                startLocationService();
                setLandmarkIfCommingFromNotification();
            } else {
                getLocationPermission();
            }
        }
        mBottomSheetManager.hideSheetIfExpanded();
    }

    private void setLandmarkIfCommingFromNotification() {
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            int value = extras.getInt("landmarkId", 0);
            if (value > 0) {
                landmarksViewModel.setSelectedLandmark(value);
            }
        }
    }

    private void startLocationService() {
        if (!isLocationServicesRunning()) {
            Intent serviceIntent = new Intent(this, LocationUpdatesService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                MainActivity.this.startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
        }
    }

    private boolean isLocationServicesRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (LocationUpdatesService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void startTrackingLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, null);
    }

    private boolean mapServicesAvailable() {
        return isPlayServicesOK() && isMapsEnabled();
    }

    public boolean isPlayServicesOK() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
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
            //startTrackingLocation();
            onResume();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private boolean hasPermission(String aPermission) {
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
                if (mLocationPermissionGranted) {
                    startTrackingLocation();
                } else {
                    getLocationPermission();
                }
            }
        }
    }


    private void setConnectivityMonitor() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        ConnectivityReceiver.ConnectivityLossListener listener = new ConnectivityReceiver.ConnectivityLossListener() {
            @Override
            public void onConnectionLost() {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();

                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Internet not available, Cross check your internet connectivity and try again");
                alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "continue offline", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "go to network settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                        startActivity(intent);
                    }
                });
                alertDialog.show();
            }
        };
        mConnectionMonitor = new ConnectivityReceiver(listener);
        this.registerReceiver(mConnectionMonitor, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(mConnectionMonitor);
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
        if (id == android.R.id.home) {
            TabLayout tabs = findViewById(R.id.tabs);
            TabLayout.Tab tab = tabs.getTabAt(0);
            tab.select();
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            toursViewModel.setSelectedTour(-1);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLandmarkSelected(Landmark selectedLandmark) {
        mBottomSheetManager.onLandmarkSelected(selectedLandmark,mCurrentLocation,mViewPager.getHeight());
    }

    @Override
    public void onTourSelected(Tour selected) {
        generateBackButton();
        TabLayout tabs = findViewById(R.id.tabs);
        TabLayout.Tab tab = tabs.getTabAt(1);
        tab.select();
    }

    public void generateBackButton() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
