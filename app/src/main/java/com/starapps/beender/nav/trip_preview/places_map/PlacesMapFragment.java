package com.starapps.beender.nav.trip_preview.places_map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.starapps.beender.data.Place;
import com.starapps.beender.data.Trip;
import com.starapps.beender.databinding.FragmentPlacesMapBinding;
import com.starapps.beender.nav.NestedFragment;
import com.starapps.beender.utils.ImageUtil;

import java.util.HashMap;
import java.util.Map;



public class PlacesMapFragment extends NestedFragment implements LocationListener {

    public static final String TAG = "PlacesMapFragment";
    public static final String ARG_PLACES = "arg_places";
    private static final int REQ_LOCATION = 1256;

    private FragmentPlacesMapBinding binding;
    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private Location mCurrentLocation;
    private Map<Marker, Place> markerPlaceMap = new HashMap<>();
    private boolean isLoaded;

    private Trip mTrip;

    private OnMapReadyCallback mReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            binding.mapHolder.onResume();
            mMap = googleMap;
            setupMap();
            load();
        }
    };

    public static Fragment newInstance(Trip trip) {
        Fragment fragment = new PlacesMapFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_PLACES, trip);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTrip = (Trip) getArguments().getSerializable(ARG_PLACES);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPlacesMapBinding.inflate(inflater, container, false);

        binding.mapHolder.onCreate(savedInstanceState);
        binding.mapHolder.getMapAsync(mReadyCallback);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.myLocationButton.setOnClickListener(v -> centerUserMarker());
    }

    private void setupMap() {
        if (mMap != null) {
            mMap.getUiSettings().setMapToolbarEnabled(false);
            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.setOnMarkerClickListener(this::markerClick);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            if (isLocationAllowed()) {
                updateListener();
            }
        }
        MapsInitializer.initialize(getActivity());
    }

    private boolean isLocationAllowed() {
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, REQ_LOCATION);
            return false;
        }
        return true;
    }

    private boolean markerClick(Marker marker) {
        if (markerPlaceMap.containsKey(marker)) {
            Place place = markerPlaceMap.get(marker);
            Log.d(TAG, "markerClick: " + place);
            showPlace(place);
            centerMarker(marker);
        }
        return true;
    }

    private void centerMarker(Marker marker) {
        if (mMap == null || marker == null) {
            return;
        }
        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 13);
        mMap.animateCamera(cu);
    }

    private void showPlace(Place place) {
        binding.placeAddress.setText(place.getAddress());
        binding.placeCategory.setText(place.getCategory());
        binding.placeName.setText(place.getName());
        binding.borderBottom.setBackgroundResource(ImageUtil.getBorderColor(place.getCategory()));
        binding.borderTop.setBackgroundResource(ImageUtil.getBorderColor(place.getCategory()));
        Picasso.with(getActivity())
                .load(Uri.parse(place.getPhotoUrl()))
//                .load(R.drawable.waterfall_photo)
                .resize(250, 250)
                .centerCrop()
                .into(binding.placePhoto);
        showSheet();
    }

    private void showSheet() {
        if (!isSheetVisible()) {
            binding.placeSheet.setVisibility(View.VISIBLE);
            if (mMap != null) mMap.setPadding(0, 0, 0, 250);
        }
    }

    private Marker addMarker(Place place, @DrawableRes int icon) {
        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(place.getLatitude(), place.getLongitude()))
                .title(place.getName())
                .snippet(place.getAddress())
                .icon(BitmapDescriptorFactory.fromResource(icon)));
    }

    public void load() {
        if (!isLoaded && mMap != null && isResumed()) {
            markerPlaceMap.clear();
            for (Place place : mTrip.getPlaces()) {
                Marker m = addMarker(place, ImageUtil.getPinIcon(place.getCategory()));
                markerPlaceMap.put(m, place);
            }
            isLoaded = true;
            zoomToBounds();
        }
    }

    private void zoomToBounds() {
        if (markerPlaceMap.isEmpty()) {
            return;
        }
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (Marker m : markerPlaceMap.keySet()) {
            builder.include(m.getPosition());
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 150));
    }

    private void centerUserMarker() {
        if (mMap != null && mCurrentLocation != null) {
            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()),
                    14
            );
            mMap.animateCamera(cu);
        }
    }

    @Override
    public boolean onBackPressed() {
        if (isSheetVisible()) {
            hideSheet();
            return false;
        }
        return true;
    }

    private void hideSheet() {
        binding.placeSheet.setVisibility(View.GONE);
        if (mMap != null) mMap.setPadding(0, 0, 0, 0);
    }

    private boolean isSheetVisible() {
        return binding.placeSheet.getVisibility() == View.VISIBLE;
    }

    @Override
    public void onResume() {
        binding.mapHolder.onResume();
        super.onResume();
        load();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        binding.mapHolder.onLowMemory();
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.mapHolder.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        binding.mapHolder.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.mapHolder.onDestroy();
        removeUpdates();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length == 0) {
            return;
        }
        switch (requestCode) {
            case REQ_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateListener();
                }
                break;
        }
    }

    private void removeUpdates() {
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(this);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void updateListener() {
        if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }
        if (getActivity() != null) {
            mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
            } else {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, this);
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        updateListener();
    }

    @Override
    public void onProviderEnabled(String provider) {
        updateListener();
    }

    @Override
    public void onProviderDisabled(String provider) {
        updateListener();
    }
}
