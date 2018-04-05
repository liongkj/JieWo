package com.jiewo.kj.jiewo.view.fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jiewo.kj.jiewo.R;
import com.jiewo.kj.jiewo.ViewModel.CategoryViewModel;
import com.jiewo.kj.jiewo.databinding.FragmentHomeBinding;
import com.jiewo.kj.jiewo.util.MapMarkerAdapter;
import com.jiewo.kj.jiewo.view.activity.MainActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static android.app.Activity.RESULT_OK;
import static com.jiewo.kj.jiewo.util.Constants.DATABASE_REF;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements
        OnMapReadyCallback,
        LocationListener,
        GeoQueryEventListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnCameraIdleListener {

    private static final int RESULT_CANCEL = 0;
    private static final GeoLocation INITIAL_CENTER = new GeoLocation(37.7789, -122.4017);
    private static final int INITIAL_ZOOM_LEVEL = 14;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 18f;
    FragmentHomeBinding binding;
    LocationManager locationManager;
    String provider;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    ActionBarDrawerToggle mToggle = MainActivity.result.getActionBarDrawerToggle();
    CategoryViewModel viewModel;
    private DatabaseReference GEO_FIRE_DB = DATABASE_REF.child("Item-Location").getRef();
    private GoogleMap mMap;
    private Boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean doubleBackToExitPressedOnce;
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doubleBackToExitPressedOnce = false;
        }
    };
    private Handler mHandler = new Handler();
    private Circle searchCircle;
    private GeoFire geoFire;
    private GeoQuery geoQuery;
    private GeoLocation geoLocation;
    private Map<String, Marker> markerMap;
    private String TAG = "gMap";

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(CategoryViewModel.class);
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    Toast.makeText(getContext(), "Connected", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Not connected", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) getActivity()).hideFab();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        View view = binding.getRoot();
        binding.setView(this);

        getActivity().setTitle("JieWo");
        mToggle.setDrawerIndicatorEnabled(true);
        ((MainActivity) getActivity()).showFab();
        setHasOptionsMenu(true);

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_UP
                    && keyCode == KeyEvent.KEYCODE_BACK) {
                if (doubleBackToExitPressedOnce) {
                    // super.onBackPressed();
                    if (mHandler != null) {
                        mHandler.removeCallbacks(mRunnable);
                    }
                    getActivity().finish();
                    return true;
                }
                doubleBackToExitPressedOnce = true;
                Toast.makeText(getActivity(),
                        "Please click BACK again to exit",
                        Toast.LENGTH_SHORT).show();
                mHandler.postDelayed(mRunnable, 2000);
            }
            return true;
        });

        getLocationPermission();
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);
        @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(provider);
        this.geoFire = new GeoFire(GEO_FIRE_DB);
        this.geoQuery = geoFire.queryAtLocation(INITIAL_CENTER, 1);
        if (location != null) {
            onLocationChanged(location);
        }
        this.markerMap = new HashMap<>();

        return view;
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        try {
            if (mLocationPermissionsGranted) {

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            if (currentLocation != null) {

                                viewModel.setCurrentLocation(currentLocation);
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                        DEFAULT_ZOOM);
                            } else {
                                Toast.makeText(getContext(), "Please enable Location service", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(getContext(), "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void initMap() {
        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            getDeviceLocation();
            mapFragment.getMapAsync(this);

        }
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(getContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void createMarker(String id, String title, String category, Uri img, GeoLocation location) {

        Random r = new Random();

        LatLng position = new LatLng(location.latitude, location.longitude);

        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(position)
                .title(category)
                .snippet(title)
                .anchor(r.nextFloat(), r.nextFloat())
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher_round)));
        marker.setTag(img);

        mMap.setInfoWindowAdapter(new MapMarkerAdapter(getContext()));
        mMap.setOnInfoWindowClickListener(this);

        markerMap.put(id, marker);

    }

    private void animateMarkerTo(final Marker marker, final double lat, final double lng) {

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long DURATION_MS = 3000;
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final LatLng startPosition = marker.getPosition();
        handler.post(new Runnable() {
            @Override
            public void run() {
                float elapsed = SystemClock.uptimeMillis() - start;
                float t = elapsed / DURATION_MS;
                float v = interpolator.getInterpolation(t);
                double currentLat = (lat - startPosition.latitude) * v + startPosition.latitude;
                double currentLng = (lng - startPosition.longitude) * v + startPosition.longitude;
                marker.setPosition(new LatLng(currentLat, currentLng));
                // if animation is not finished yet, repeat
                if (t < 1) {
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    private double zoomLevelToRadius(double zoomLevel) {
        // Approximation to fit circle into view
        return 16384000 / Math.pow(1.9, zoomLevel);
    }

    public void findPlace() {
        try {
            AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                    .setCountry("MY")
                    .build();
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .setFilter(autocompleteFilter)
                            .build(getActivity());
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_options_menu, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                findPlace();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onResume() {
        super.onResume();
        initMap();
        locationManager.requestLocationUpdates(provider, 1000, 1, this);
        this.geoQuery.addGeoQueryEventListener(this);

    }

    @Override
    public void onPause() {
        super.onPause();

        locationManager.removeUpdates(this);
        this.geoQuery.removeAllListeners();
        for (Marker marker : markerMap.values()) {
            marker.remove();
        }
        markerMap.clear();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (searchCircle != null) searchCircle.remove();
        if (mLocationPermissionsGranted) {
//            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.setOnCameraMoveListener(this);
            mMap.setOnCameraIdleListener(this);
            LatLng latLngCenter = new LatLng(INITIAL_CENTER.latitude, INITIAL_CENTER.longitude);
            this.searchCircle = mMap.addCircle(new CircleOptions().center(latLngCenter).radius(1000));
            Snackbar.make(getView(), "Drag around or search for a address", Snackbar.LENGTH_INDEFINITE).setAction("Okay", v -> {

            }).show();
//            this.searchCircle.setFillColor(Color.argb(80, 255, 255, 255));
//           this.searchCircle.setStrokeColor(Color.argb(66, 0, 0, 0));
            searchCircle.setVisible(false);
            onCameraMove();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        geoLocation = new GeoLocation(location.getLatitude(), location.getLongitude());

        this.geoQuery = geoFire.queryAtLocation(geoLocation, 1);

        viewModel.setCurrentLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    //geoquery
    @Override
    public void onKeyEntered(String key, GeoLocation location) {
        DATABASE_REF.child("Item").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    String item = dataSnapshot.child("title").getValue().toString();
                    Uri uri = Uri.parse(dataSnapshot.child("picture/pic1").getValue().toString());
                    String category = dataSnapshot.child("category").getValue().toString();
                    createMarker(key, item, category, uri, location);
                } catch (Exception e) {
                    Log.e("error", e.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onKeyExited(String key) {
        Marker marker = markerMap.get(key);
        if (marker != null) {
            marker.remove();
//            marker.setTag(null);
            markerMap.remove(key);

        }
    }

    @Override
    public void onKeyMoved(String key, GeoLocation location) {
        Marker marker = this.markerMap.get(key);
        if (marker != null) {
            this.animateMarkerTo(marker, location.latitude, location.longitude);
        }
    }

    @Override
    public void onGeoQueryReady() {

    }

    @Override
    public void onGeoQueryError(DatabaseError error) {
        new AlertDialog.Builder(getContext())
                .setTitle("Error")
                .setMessage("There was an unexpected error querying GeoFire: " + error.getMessage())
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    @Override
    public void onCameraMove() {

        LatLng center = mMap.getCameraPosition().target;
        double radius = zoomLevelToRadius(mMap.getCameraPosition().zoom);
        searchCircle.setCenter(center);
        searchCircle.setRadius(radius);
        geoQuery.setCenter(new GeoLocation(center.latitude, center.longitude));
        // radius in km
        geoQuery.setRadius(radius / 1000);

    }

    @Override
    public void onCameraIdle() {
        if (markerMap.size() > 1) {
            Toast.makeText(getContext(), "Found " + markerMap.size() + " items", Toast.LENGTH_SHORT).show();
        } else if (markerMap.size() == 1) {
            Toast.makeText(getContext(), "Found " + markerMap.size() + " item", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "No item found in this location", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        String key = null;
        for (Map.Entry entry : markerMap.entrySet()) {
            if (marker.equals(entry.getValue())) {
                key = entry.getKey().toString();
                break;
            }
        }
        Location location = new Location("item");
        location.setLatitude(marker.getPosition().latitude);
        location.setLongitude(marker.getPosition().longitude);
        viewModel.setItemLocation(location);
        viewModel.selectItem(key);

        Fragment fragment = new ItemDetailFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_placeholder, fragment, "tag")
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getContext(), data);
//                CameraUpdate location = CameraUpdateFactory.newCameraPosition(new CameraPosition(place.getLatLng(), 15,0,25));
                moveCamera(place.getLatLng(), 15);
                onCameraMove();
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getContext(), data);
                Snackbar snackbar = Snackbar
                        .make(getView(), status.toString(), Snackbar.LENGTH_LONG);
                snackbar.show();
            } else if (resultCode == RESULT_CANCEL) {
                Status status = PlaceAutocomplete.getStatus(getContext(), data);
                // Snackbar.make(getView(), "An error occured", Snackbar.LENGTH_LONG).show();
            }
        }
    }


}
