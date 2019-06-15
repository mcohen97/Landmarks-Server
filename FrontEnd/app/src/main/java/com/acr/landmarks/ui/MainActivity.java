package com.acr.landmarks.ui;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.Toast;

import com.acr.landmarks.ConnectivityReceiver;
import com.acr.landmarks.R;
import com.acr.landmarks.adapters.SectionsPagerAdapter;
import com.acr.landmarks.models.Tour;
import com.acr.landmarks.models.Landmark;
import com.acr.landmarks.services.DebugConstants;
import com.acr.landmarks.services.LocationUpdatesService;
import com.acr.landmarks.services.ServerErrorHandler;
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

public class MainActivity extends AppCompatActivity implements TourSelectedListener {


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

    private boolean darkThemeActivated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setIconVisibility(true);

        /* Create the adapter that will return a fragment for each of the three primary sections of the activity.*/
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        createViewPager();
        mBottomSheetManager = new BottomSheetManager(this);
        setUpBottomSheetManager();
        setViewPager();
        createLocationCallback();
        setViewModels();
        setServerErrorHandler();
    }

    private void setIconVisibility(boolean show) {
        getSupportActionBar().setDisplayShowHomeEnabled(show);
        getSupportActionBar().setIcon(R.drawable.icon);
    }

    private void loadTheme() {
        SharedPreferences preferences = getSharedPreferences("PREFS",0);
        darkThemeActivated = preferences.getBoolean("darkTheme", false);
        if(darkThemeActivated){
            setTheme(R.style.NightTheme);
        }else{
            setTheme(R.style.AppTheme);
        }
    }

    private void checkThemeUpdate(){
        SharedPreferences preferences = getSharedPreferences("PREFS",0);
        boolean savedOption = preferences.getBoolean("darkTheme", false);
        if(savedOption != darkThemeActivated){
            finish();
            startActivity(getIntent());
        }

    }

    private void setViewModels() {
        locationViewModel = ViewModelProviders.of(this).get(UserLocationViewModel.class);
        landmarksViewModel = ViewModelProviders.of(this).get(LandmarksViewModel.class);
        toursViewModel = ViewModelProviders.of(this).get(ToursViewModel.class);
        landmarksViewModel.getSelectedLandmark().observe( this,
                landmark -> onLandmarkSelected(landmark) );
    }

    private void setServerErrorHandler() {
        ServerErrorHandler handler = ServerErrorHandler.getInstance();

        handler.serverError().observe(this, throwable -> {
            View appView = findViewById(R.id.main_content);
            Snackbar snackbar = Snackbar
                    .make(appView, getString(R.string.server_error_message), Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(getString(R.string.server_error_action_btntext), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();
            handler.serverError().removeObservers(this);
        });
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
                setBackButtonVisibility(false);
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
                Log.d(DebugConstants.AP_DEX,"Directions to landmark requested, time: "+System.currentTimeMillis());
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
        checkThemeUpdate();
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
        setConnectivityMonitor();
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
            Toast.makeText(this, getString(R.string.google_maps_error_message), Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertManager.buildAlertMessageNoGps(this);
            return false;
        }
        return true;
    }



    private void getLocationPermission() {
        if (hasPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            mLocationPermissionGranted = true;
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
                    this.recreate();
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
                AlertManager.buildAlertMessageConnectionLost(MainActivity.this);
            }
        };
        mConnectionMonitor = new ConnectivityReceiver(listener);
        this.registerReceiver(mConnectionMonitor, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
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

        switch (id){
            case R.id.action_settings:
                Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingsIntent);
                break;

            case android.R.id.home:
                TabLayout tabs = findViewById(R.id.tabs);
                TabLayout.Tab tab = tabs.getTabAt(0);
                tab.select();
                setBackButtonVisibility(false);
                toursViewModel.setSelectedTour(-1);
        }

        return super.onOptionsItemSelected(item);
    }

    public void onLandmarkSelected(Landmark selectedLandmark) {
        mBottomSheetManager.onLandmarkSelected(selectedLandmark,mCurrentLocation,mViewPager.getHeight());
    }

    @Override
    public void onTourSelected(Tour selected) {
        setBackButtonVisibility(true);
        TabLayout tabs = findViewById(R.id.tabs);
        TabLayout.Tab tab = tabs.getTabAt(1);
        tab.select();
    }

    public void setBackButtonVisibility(boolean show) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(show);
        setIconVisibility(!show);
    }

}
