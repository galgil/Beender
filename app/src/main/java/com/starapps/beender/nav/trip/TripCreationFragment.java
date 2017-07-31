package com.starapps.beender.nav.trip;

import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.starapps.beender.R;
import com.starapps.beender.api.RetrofitBuilder;
import com.starapps.beender.api.foursqaure.Explore;
import com.starapps.beender.api.foursqaure.FoursquareApi;
import com.starapps.beender.api.foursqaure.data.Group;
import com.starapps.beender.api.foursqaure.data.Location;
import com.starapps.beender.api.foursqaure.data.Venue;
import com.starapps.beender.data.Place;
import com.starapps.beender.data.Trip;
import com.starapps.beender.data.db.AppDb;
import com.starapps.beender.databinding.FragmentBeenderCreationBinding;
import com.starapps.beender.nav.NestedFragment;
import com.starapps.beender.nav.trip_preview.places_list.PlacesSingleton;
import com.starapps.beender.utils.ImageUtil;
import com.starapps.beender.utils.TimeUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class TripCreationFragment extends NestedFragment {

    public static final String TAG = "TripCreationFragment";
    public static final String ARG_INTENT = "arg_intent";
    public static final String ARG_AUTHOR = "arg_author";
    public static final String ARG_TRIP_NAME = "arg_trip_name";

    private FragmentBeenderCreationBinding binding;
    private Intent intent;
    private boolean isBackAllowed;
    private Map<String, List<Place>> places = new HashMap<>();
    private Map<String, Trip> tripMap = new HashMap<>();
    private List<Call<Explore>> calls = new ArrayList<>();
    private boolean hasRejectedPhotos;
    private String mName;
    private String authorName;

    public static Fragment newInstance(Intent intent, String authorName) {
        Fragment fragment = new TripCreationFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_INTENT, intent);
        bundle.putString(ARG_AUTHOR, authorName);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static Fragment newInstance(Intent intent, Trip trip) {
        Fragment fragment = new TripCreationFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_INTENT, intent);
        bundle.putSerializable(ARG_TRIP_NAME, trip);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            intent = getArguments().getParcelable(ARG_INTENT);
            Trip trip = (Trip) getArguments().getSerializable(ARG_TRIP_NAME);
            authorName = getArguments().getString(ARG_AUTHOR);
            if (trip == null) {
                List<Trip> trips = AppDb.getDatabase(getActivity()).tripDao().getTrips(authorName);
                for (Trip t : trips) {
                    tripMap.put(t.getName(), t);
                    places.put(t.getName(), t.getPlaces());
                }
            } else {
                mName = trip.getName();
                places.put(trip.getName(), trip.getPlaces());
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBeenderCreationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageUtil.loadImage(R.drawable.image_beender_creation, binding.image, true, false);
    }

    private void processImages(Intent data) {
        if (data.getData() != null) {
            if (!addPlaceFromUri(data.getData())) {
                Toast.makeText(getActivity(), R.string.photo_is_broken_or_without_any_location_data, Toast.LENGTH_SHORT).show();
                moveBack();
            }
        } else if (data.getClipData() != null) {
            ClipData mClipData = data.getClipData();
            boolean res = false;
            for (int i = 0; i < mClipData.getItemCount(); i++) {
                if (!res) {
                    if (!(res = addPlaceFromUri(mClipData.getItemAt(i).getUri()))) {
                        hasRejectedPhotos = true;
                    }
                } else {
                    if (!addPlaceFromUri(mClipData.getItemAt(i).getUri())) {
                        hasRejectedPhotos = true;
                    }
                }
            }
            if (!res) {
                Toast.makeText(getActivity(), R.string.photo_is_broken_or_without_any_location_data, Toast.LENGTH_SHORT).show();
                moveBack();
            }
        } else {
            Toast.makeText(getActivity(), R.string.photo_is_broken_or_without_any_location_data, Toast.LENGTH_SHORT).show();
            moveBack();
        }
    }

    private void moveBack() {
        isBackAllowed = true;
        new Handler().postDelayed(() -> getInterface().moveBack(), 100);
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        }
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }

    private boolean addPlaceFromUri(Uri uri) {
        if (uri == null) return false;
        ExifInterface exifInterface = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                exifInterface = new ExifInterface(getActivity().getContentResolver().openInputStream(uri));
            } else {
                String path = getRealPathFromURI(uri);
                if (path == null) {
                    return false;
                }
                exifInterface = new ExifInterface(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (exifInterface == null) return false;
        float[] gps = new float[2];
        exifInterface.getLatLong(gps);
        String date = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
        Log.d(TAG, "addPlaceFromUri: " + Arrays.toString(gps) + ", " + date);
        if (gps[0] == 0 && gps[1] == 0) {
            return false;
        }
        Call<Explore> call = RetrofitBuilder.getApi().getExplore(getQuery(gps[0], gps[1]));
        calls.add(call);
        call.enqueue(new Callback<Explore>() {
            @Override
            public void onResponse(Call<Explore> call, Response<Explore> response) {
                if (response.isSuccessful()) {
                    addPlace(response.body(), uri, date);
                }
                checkForLast(call);
            }

            @Override
            public void onFailure(Call<Explore> call, Throwable t) {
                checkForLast(call);
            }
        });
        return true;
    }

    private void addPlace(Explore body, Uri uri, String date) {
        if (body == null) return;
        if (body.getMeta().getCode() != 200) return;
        com.starapps.beender.api.foursqaure.data.Response response = body.getResponse();
        if (response == null) return;
        String key = response.getHeaderLocation();

        Place place = new Place();
        place.setPhotoUrl(uri.toString());
        if (date == null) {
            date = TimeUtil.getStamp();
        }
        place.setDate(date);
        if (!response.getGroups().isEmpty()) {
            Group group = response.getGroups().get(0);
            if (!group.getItems().isEmpty()) {
                Venue venue = group.getItems().get(0).getVenue();
                place.setName(venue.getName());
                if (!venue.getCategories().isEmpty()) {
                    place.setCategory(venue.getCategories().get(0).getName());
                }
                if (venue.getLocation() != null) {
                    Location location = venue.getLocation();
                    if (key == null) {
                        key = location.getCity();
                    }
                    place.setAddress(location.getAddress() != null ? location.getAddress() : "" + ", " + location.getCity());
                    place.setLatitude(location.getLat());
                    place.setLongitude(location.getLng());
                }
            }
        }
        if (key == null || place.getCategory() == null || place.getName() == null) {
            return;
        }
        key = key.toLowerCase();
        key = StringUtils.capitalize(key);
        List<Place> list = new ArrayList<>();
        if (places.containsKey(key)) {
            list = places.get(key);
            if (list == null) {
                list = new ArrayList<>();
            }
        }
        addToList(place, list);
        places.put(key, list);
    }

    private void addToList(Place place, List<Place> places) {
        boolean has = false;
        for (Place p : places) {
            if (p.getPhotoUrl().equals(place.getPhotoUrl())) {
                has = true;
                break;
            }
        }
        if (!has) {
            places.add(place);
        }
    }

    private void checkForLast(Call<Explore> call) {
        calls.remove(call);
        if (calls.isEmpty()) {
            processSave();
        }
    }

    private void processSave() {
        if (places.isEmpty()) {
            Toast.makeText(getActivity(), R.string.photo_is_broken_or_without_any_location_data, Toast.LENGTH_SHORT).show();
            moveBack();
            return;
        }
        if (mName != null) {
            PlacesSingleton.getInstance().setPlaces(new ArrayList<>());
            if (places.containsKey(mName)) {
                List<Place> list = places.get(mName);
                if (list.isEmpty()) {
                    Toast.makeText(getActivity(), R.string.photo_taken_in_another_place, Toast.LENGTH_SHORT).show();
                    moveBack();
                } else {
                    PlacesSingleton.getInstance().setPlaces(list);
                    moveBack();
                }
            } else {
                Toast.makeText(getActivity(), R.string.photo_taken_in_another_place, Toast.LENGTH_SHORT).show();
                moveBack();
            }
        } else {
            List<Trip> trips = new ArrayList<>();
            for (String key : places.keySet()) {
                if (key == null) {
                    continue;
                }
                List<Place> list = places.get(key);
                if (list.isEmpty()) {
                    continue;
                }
                Trip trip;
                if (tripMap.containsKey(key)) {
                    trip = tripMap.get(key);
                } else {
                    trip = new Trip();
                    trip.setAuthorName(authorName);
                    trip.setAddress(key);
                    trip.setName(key);
                    trip.setPhotoUrl(list.get(0).getPhotoUrl());
                    trip.setPrivate(false);
                }
                trip.setPlaces(list);
                addExtra(trip);
                trips.add(trip);
            }
            Log.d(TAG, "processSave: " + trips);
            AppDb.getDatabase(getActivity()).tripDao().insertTrips(trips);
            if (hasRejectedPhotos) {
                Toast.makeText(getActivity(), R.string.some_images_were_rejected, Toast.LENGTH_SHORT).show();
            }
            moveBack();
        }
    }

    private void addExtra(Trip trip) {
        if (trip.getPlaces().size() > 1) {
            Place place = trip.getPlaces().get(0);
            String dtStart = place.getDate();
            String dtEnd = place.getDate();
            for (int i = 1; i < trip.getPlaces().size(); i++) {
                Place p = trip.getPlaces().get(i);
                if (p.getDate().compareTo(dtStart) < 0) {
                    dtStart = p.getDate();
                } else if (p.getDate().compareTo(dtEnd) > 0) {
                    dtEnd = p.getDate();
                }
            }
            trip.setDtEnd(dtEnd);
            trip.setDtStart(dtStart);
        } else if (trip.getPlaces().size() > 0) {
            Place place = trip.getPlaces().get(0);
            trip.setDtEnd(place.getDate());
            trip.setDtStart(place.getDate());
        }
    }

    private Map<String, String> getQuery(float lat, float lng) {
        Map<String, String> map = new HashMap<>();
        map.put("ll", "" + lat + "," + lng);
        map.put("radius", "5000");
        map.put("sortByDistance", "1");
        map.put("client_id", FoursquareApi.CLIENT_ID);
        map.put("client_secret", FoursquareApi.CLIENT_SECRET);
        map.put("v", "20170711");
        map.put("limit", "5");
        return map;
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        getInterface().showNavigation(false);
        if (intent != null) {
            processImages(intent);
        } else {
            moveBack();
        }
    }

    @Override
    public boolean onBackPressed() {
        return isBackAllowed;
    }
}
