package com.comp900019.MelbParking;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

//import com.comp900019.MelbParking.DataManagement.ClusterRenderer;
//import com.comp900019.MelbParking.DataManagement.MarkerClusterManager;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.maps.android.MarkerManager;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.widget.Toast;


public class ParkFragment extends Fragment implements OnMapReadyCallback, OnUpdateParkingSpace {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    public static final String DURATION_KEY = "duration_key";
    public static final String LATITUDE_KEY = "latitude_key";
    public static final String LONGITUDE_KEY = "longitude_key";
    public static final String ADDRESS_KEY = "ADDRESS_KEY";
    public static final String PERMISSION_KEY = "navi_permission";

    public static final String TAG = "ParkFragment";

    private final int zoomInLevel = 16;
    private GoogleMap mMap = null;
    private PlacesClient placesClient;

    private Context context;

    private static final int REQUESTCODE = 6001;
    private static final List<Place.Field> PLACE_FIELDS =
        Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    static boolean locationPermission;

    private MapView mapView;
    private ParkingMateViewModel viewModel;
    private List<SpaceWithRestriction> parkingSpaces = new ArrayList<SpaceWithRestriction>();
    private EditText searchBar;
    private Button searchButton;

    ClusterableParkingSpot closestSpot;
    Marker closestSpotMarker;


    private ArrayList<ClusterableParkingSpot> mapMarker = new ArrayList<ClusterableParkingSpot>();

    private ClusterManager<ClusterableParkingSpot> clusterManager;

    private Location currentLocation;

    // Default Location used if the user has not granted location permissions
    private static final LatLng DEFAULT_LOCATION = new LatLng(-37.8000468, 144.9600439);
    private static final LatLng CITY_SOUTH_WEST = new LatLng(-37.822720, 144.943852);
    private static final LatLng CITY_NORTH_EAST = new LatLng(-37.803725, 144.976011);

    private Spinner durationSpinner;

    private Marker searchLocationMarker;

    private Place searchTerm;
    private AutocompleteSupportFragment autocompleteSupportFragment;
    private FusedLocationProviderClient fusedLocProvider;
    private PlaceSelectionListener placeSelectionListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        placesClient = Places.createClient(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_park, container, false);

        getLocationPermission();

        fusedLocProvider = LocationServices.getFusedLocationProviderClient(getActivity());

        durationSpinner = view.findViewById(R.id.duration_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(container.getContext(), R.array.duration_spinner_array, android.R.layout.simple_spinner_item);
        // Inflate the layout for this fragment
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        durationSpinner.setAdapter(adapter);
        mapView = view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(ParkingMateViewModel.class);

        final Observer<List<SpaceWithRestriction>> bayStatusObserver = new Observer<List<SpaceWithRestriction>>() {
            @Override
            public void onChanged(List<SpaceWithRestriction> parkingSpace) {
                parkingSpaces = parkingSpace;
                // Redraw the map
                addClusterItems();
            }
        };


        autocompleteSupportFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteSupportFragment.setPlaceFields(PLACE_FIELDS);

        placeSelectionListener = new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                if (place != null && place.getLatLng() != null) {
                    searchTerm = place;
                }
                autocompleteSupportFragment.setText(place.getName());
            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(getContext(), "Cannot find Location.", Toast.LENGTH_SHORT).show();
            }
        };
        autocompleteSupportFragment.setOnPlaceSelectedListener(placeSelectionListener);


        viewModel.getAllParkingSpaces().observe(this, bayStatusObserver);
        mapView = view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        searchButton = view.findViewById(R.id.search_car_parks);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchTerm != null) {
                    geoLocation(v);
                } else{
                    Toast.makeText(getContext(), "Location not specified", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d("reqCode", Integer.toString(requestCode));
        Log.d("reqCodeMy", Integer.toString(MY_PERMISSIONS_REQUEST_LOCATION));

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.locationPermission = true;
                    getLocation();
                }
            }
        }
    }

    private FindAutocompletePredictionsRequest buildRequest(String query) {
        return FindAutocompletePredictionsRequest.builder()
            .setLocationBias(RectangularBounds.newInstance(CITY_SOUTH_WEST, CITY_NORTH_EAST))
            .setCountry("au")
            //.setTypeFilter(TypeFilter.ADDRESS)
            .setSessionToken(AutocompleteSessionToken.newInstance())
            .setQuery(query)
            .build();
    }

    private void processFetchRequest(FetchPlaceRequest request) {
        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            placeSelectionListener.onPlaceSelected(place);
            searchButton.callOnClick();
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                Toast.makeText(getContext(), "No matching location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processPredictionRequest(FindAutocompletePredictionsRequest request) {
        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            List<AutocompletePrediction> predictions = response.getAutocompletePredictions();
            if (predictions.size() == 0) {
                Toast.makeText(getContext(), "Location not found", Toast.LENGTH_SHORT).show();
            } else {
                AutocompletePrediction firstPrediction = response.getAutocompletePredictions().get(0);
                String placeID = firstPrediction.getPlaceId();
                FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(placeID, PLACE_FIELDS);
                processFetchRequest(placeRequest);
            }
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                Toast.makeText(getContext(), "Location not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Search for parking spaces at a pre-defined location. Used for appointments
     */
    public void searchPredefinedDestination(String placeName) {
        autocompleteSupportFragment.setText(placeName);
        processPredictionRequest(buildRequest(placeName));
    }

    private void geoLocation(View view){
        Place place = searchTerm;
        LatLng searchGeo = place.getLatLng();
        // Remove the old P symbol for the closest parking spot
        // and insert a cluster marker in its place
        if (closestSpot != null && closestSpotMarker != null) {
            clusterManager.addItem(closestSpot);
            closestSpotMarker.remove();
        }
        if (searchLocationMarker != null) {
            searchLocationMarker.remove();
        }

        // Look for the best parking spot
        ClosestSpotFinder closetSpot = new ClosestSpotFinder();
        Location searchLocation = new Location("");
        searchLocation.setLongitude(searchGeo.longitude);
        searchLocation.setLatitude(searchGeo.latitude);

        int selectedDuration = durationSpinner.getSelectedItemPosition();

        // Resolve the duration tags
        if (selectedDuration == 0) {
            selectedDuration = 30;
        } else if(selectedDuration == 1) {
            selectedDuration = 60;
        } else if (selectedDuration == 2) {
            selectedDuration = 120;
        } else if (selectedDuration == 3) {
            selectedDuration = 180;
        }

        closestSpot = closetSpot.findClosetParkingSpot(searchLocation, selectedDuration, mapMarker);
        LatLng closest = closestSpot.getPosition();

        clusterManager.removeItem(closestSpot);

        MarkerOptions selectedLocation = new MarkerOptions().position(searchGeo).title("Selected").icon(BitmapDescriptorFactory.fromBitmap(currentLocationIcon(150,150)));

        searchLocationMarker = mMap.addMarker(selectedLocation);

        if (closest != null) {
            MarkerOptions closestMarkerOpt = new MarkerOptions().position(closest).title("Closest").icon(BitmapDescriptorFactory.fromBitmap(bestSpotLocation(150, 150)));
            mMap.setOnMarkerClickListener(clusterManager.getMarkerManager());
            MarkerManager.Collection closestSpotCollection = clusterManager.getMarkerManager().newCollection();
            closestSpotMarker = closestSpotCollection.addMarker(closestMarkerOpt);

            closestSpotCollection.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Bundle bundle = new Bundle();

                    if (closestSpot.getDuration() != null) {
                        bundle.putString(DURATION_KEY, Integer.toString(closestSpot.getDuration()));
                    } else {
                        bundle.putString(DURATION_KEY, "null");
                    }

                    bundle.putSerializable(LATITUDE_KEY, closestSpot.getPosition().latitude);
                    bundle.putSerializable(LONGITUDE_KEY, closestSpot.getPosition().longitude);
                    bundle.putSerializable(ADDRESS_KEY, searchTerm.getAddress());
                    bundle.putBoolean(PERMISSION_KEY, ParkFragment.locationPermission);

                    BottomSheetDialogFragment bottomSheet = new OnClickClosestSpaceBottomSheet();
                    bottomSheet.setArguments(bundle);

                    bottomSheet.show(getChildFragmentManager(), TAG);

                    return true;
                }
            });

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(closestSpotMarker.getPosition());
            builder.include(searchLocationMarker.getPosition());
            LatLngBounds zoom = builder.build();
            LatLng center = zoom.getCenter();
            builder.include(new LatLng(center.latitude-0.001f, center.longitude-0.001f));
            builder.include(new LatLng(center.latitude+0.001f, center.longitude+0.001f));
            zoom = builder.build();
            closestSpotMarker.showInfoWindow();

            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(zoom, 150));
        } else {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(searchGeo, zoomInLevel));
            Toast.makeText(getContext(), "Cannot find parking space within the specified Parameters", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap bestSpotLocation(int width, int height) {
        BitmapDrawable bitmapDraw = (BitmapDrawable)getResources().getDrawable(R.drawable.closet);
        Bitmap b = bitmapDraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        return smallMarker;
    }

    private Bitmap currentLocationIcon(int width, int height){
        BitmapDrawable bitmapDraw=(BitmapDrawable)getResources().getDrawable(R.drawable.usr_marker);
        Bitmap b=bitmapDraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        return smallMarker;
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            this.locationPermission = true;
        } else {
            requestPermissions(new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }

    private void addClusterItems() {
        if (clusterManager == null) {
            return;
        }

        for (ClusterableParkingSpot cps : mapMarker) {
            clusterManager.removeItem(cps);
        }

        if (mapMarker != null) {
            mapMarker.clear();
        }

        for (SpaceWithRestriction space : parkingSpaces) {
            if (space.space.occupied == false) {
                ClusterableParkingSpot cps = new ClusterableParkingSpot(space);
                clusterManager.addItem(cps);
                mapMarker.add(cps);
            }
        }

    }

    private void getLocation() {
        if (locationPermission) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

            Task<Location> locationResult = fusedLocProvider.getLastLocation();
            locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        currentLocation = task.getResult();

                        if (currentLocation == null) {
                            setCameraToDEFAULT_LOCATION();
                        } else {
                            CameraPosition camPos = new CameraPosition.Builder()
                                    .target(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).zoom(13).build();

                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));

                        }

                    } else {
                        // Use Defaults
                        setCameraToDEFAULT_LOCATION();
                    }
                }
            });

        } else {
            setCameraToDEFAULT_LOCATION();
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            getLocationPermission();
        }
    }

    private void setCameraToDEFAULT_LOCATION() {
        currentLocation = new Location("");
        currentLocation.setLatitude(DEFAULT_LOCATION.latitude);
        currentLocation.setLongitude(DEFAULT_LOCATION.longitude);

        CameraPosition camPos = new CameraPosition.Builder()
                .target(new LatLng(DEFAULT_LOCATION.latitude, DEFAULT_LOCATION.longitude)).zoom(13).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        clusterManager = new ClusterManager<>(getContext() , mMap);
        clusterManager.setRenderer(new ClusterColourRenderer(getContext(), mMap, clusterManager));
        clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<ClusterableParkingSpot>() {
            @Override
            public boolean onClusterItemClick(ClusterableParkingSpot cps) {
                Bundle bundle = new Bundle();

                if (cps.getDuration() != null) {
                    bundle.putString(DURATION_KEY, Integer.toString(cps.getDuration()));
                } else {
                    bundle.putString(DURATION_KEY, "null");
                }

                bundle.putSerializable(LATITUDE_KEY, cps.getPosition().latitude);
                bundle.putSerializable(LONGITUDE_KEY, cps.getPosition().longitude);

                BottomSheetDialogFragment bottomSheet = new OnClickSpaceInfoBottomSheet();
                bottomSheet.setArguments(bundle);

                bottomSheet.show(getChildFragmentManager(), TAG);

                return true;
            }
        } );

        googleMap.setOnCameraIdleListener(clusterManager);
        googleMap.setOnMarkerClickListener(clusterManager);

        getLocation();
        addClusterItems();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }



    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onUpdate(ArrayList<ParkingSpace> parkingSpaces) {

    }

}

