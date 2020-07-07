package com.comp900019.MelbParking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.BannerInstructions;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.services.android.navigation.ui.v5.NavigationView;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.listeners.BannerInstructionsListener;
import com.mapbox.services.android.navigation.ui.v5.listeners.InstructionListListener;
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener;
import com.mapbox.services.android.navigation.ui.v5.listeners.SpeechAnnouncementListener;
import com.mapbox.services.android.navigation.ui.v5.voice.SpeechAnnouncement;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.services.android.navigation.v5.routeprogress.ProgressChangeListener;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NavigationActivity extends AppCompatActivity implements ProgressChangeListener, OnNavigationReadyCallback, NavigationListener, SpeechAnnouncementListener, BannerInstructionsListener, InstructionListListener {
    private Point origin;
    private Point destination;

    private static final int INITIAL_ZOOM = 16;
    private static DirectionsRoute currentRoute;
    private static final String TAG = "NaviagationActivity";

    private NavigationView navigationView;
    private View spacer;

    private double latitude;
    private double longitude;

    private boolean bottomSheetVisible = true;
    private boolean instructionListShown = false;
    SharedPreferences sharedPref;

    private Boolean started = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_navigation);

        Intent intent = getIntent();
        getLocation();

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        //TODO Get Desination from Intent
        double destLat = intent.getDoubleExtra(OnClickSpaceInfoBottomSheet.latitude_key, 0);
        double destLon = intent.getDoubleExtra(OnClickSpaceInfoBottomSheet.longitude_key, 0);

        destination = Point.fromLngLat(destLon, destLat);

        CameraPosition initialPosition = new CameraPosition.Builder()
                .target(new LatLng(origin.latitude(), origin.longitude()))
                .zoom(INITIAL_ZOOM)
                .build();
        spacer = findViewById(R.id.spacer);

        navigationView = findViewById(R.id.navigationView);
        navigationView.onCreate(savedInstanceState);
        navigationView.initialize(this, initialPosition);
    }

    /** Gets the Users current location, while ensuring that the Permissions have already been granted.
     *  A finish() approch is used here, as these permissions should of have been granted already*/
    private void getLocation() {
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            Location location = Objects.requireNonNull(lm).getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
            } else {
                longitude = 0;
                latitude = 0;
            }
            origin = Point.fromLngLat(longitude, latitude);
        } else {
            Toast.makeText(this, "Location Permissions Required by this Application", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // Taken from the MapBox API sample code. Builds a route, however options modified to ensure that
    // we are facing the same curb side.
    private void getRoute(Point origin, Point destination) {
        NavigationRoute.Builder builder;
        builder = NavigationRoute.builder(this);
        builder.accessToken(Mapbox.getAccessToken());
        builder.origin(origin);
        builder.destination(destination);

        boolean avoidTolls = sharedPref.getBoolean(getString(R.string.avoid_toll_pref), true);
        if (avoidTolls) {
            builder.exclude(DirectionsCriteria.EXCLUDE_TOLL);
        }
        // Ensure that we are on the same side of the curb to the parking spot.
        builder.addApproaches("unrestricted", "curb");
        builder.build().getRoute(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(
                    Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                Log.d(TAG, "Response code: " + response.code());
                if (response.body() == null) {
                    Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                    return;
                } else if (response.body().routes().size() < 1) {
                    Log.e(TAG, "No routes found");
                    return;
                }

                currentRoute = response.body().routes().get(0);

                // Start the Navigation
                startNavigation();
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
            }
        });
    }

    /**
     * Starts Navigation. This may also be called a number of times. If it is called while navigation
     * is already underway, it will re-route the user using the newly calculated route, to the
     * new destination.
     */
    public void startNavigation() {
        boolean mockLocation = sharedPref.getBoolean(getString(R.string.mocking_pref), false);

        NavigationViewOptions.Builder options =
                NavigationViewOptions.builder()
                        .navigationListener(this)
                        .directionsRoute(currentRoute)
                        .shouldSimulateRoute(mockLocation)
                        .progressChangeListener(this)
                        .speechAnnouncementListener(this)
                        .bannerInstructionsListener(this);

        if (!started) {
            started = true;
        } else {
            // Route changes may become necessary in future iterations, so that a feature
            // where a user is rerouted if their parking spot is taken while they are driving there
            Log.d(TAG, "Changing Route");
            navigationView.stopNavigation();
        }
        navigationView.startNavigation(options.build());
        setBottomSheetCallback(options);
    }

    // Taken From Mapbox API Sample Code. Shows the directions box
    private void setBottomSheetCallback(NavigationViewOptions.Builder options) {
        options.bottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                return;
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                return;
            }
        });
    }

    /*
        @Override
    public void changeDestination(Point point) {
        getRoute(origin, point);
        startNavigation();
    }

     */


    @Override
    public void onNavigationReady(boolean isRunning) {
        getRoute(origin, destination);
    }

    @Override
    public BannerInstructions willDisplay(BannerInstructions instructions) {
        return instructions;
    }

    @Override
    public void onInstructionListVisibilityChanged(boolean visible) {

    }

    @Override
    public void onCancelNavigation() {
        navigationView.stopNavigation();
        finish();
    }

    @Override
    public void onNavigationFinished() {
        // TODO Go back to main screen
    }

    @Override
    public void onNavigationRunning() {
        // Not necessary to our implementation
    }

    @Override
    public void onBackPressed() {
        if (!navigationView.onBackPressed()) {
            navigationView.stopNavigation();
            super.onBackPressed();
        } else {
            Log.d(TAG, "Navi Back");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        navigationView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        navigationView.onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        navigationView.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        navigationView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        navigationView.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        navigationView.onPause();
    }


    @Override
    public void onStop() {
        super.onStop();
        navigationView.onStop();
    }

    @Override
    protected void onDestroy() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        super.onDestroy();
        navigationView.onDestroy();
    }

    @Override
    public SpeechAnnouncement willVoice(SpeechAnnouncement announcement) {
        return announcement;
    }

    @Override
    public void onProgressChange(Location location, RouteProgress routeProgress) {

    }
}
