package com.starapps.beender.nav.profile;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.starapps.beender.R;
import com.starapps.beender.data.Profile;
import com.starapps.beender.data.Trip;
import com.starapps.beender.data.db.AppDb;
import com.starapps.beender.databinding.FragmentEditProfileBinding;
import com.starapps.beender.nav.NestedFragment;
import com.starapps.beender.nav.trip_preview.TripPreviewFragment;
import com.starapps.beender.nav.trips.GridMarginDecoration;
import com.starapps.beender.nav.trips.TripsRecyclerAdapter;
import com.starapps.beender.nav.trips.TripsViewModel;
import com.starapps.beender.utils.AppUtil;

import java.util.List;



public class EditProfileFragment extends NestedFragment {

    public static final String TAG = "EditProfileFragment";

    private FragmentEditProfileBinding binding;
    private Profile mProfile;

    private TripsRecyclerAdapter mAdapter;
    private TripsRecyclerAdapter.AdapterCallback mAdapterCallback = new TripsRecyclerAdapter.AdapterCallback() {
        @Override
        public void onItemClick(View view, Trip trip) {
            openTrip(view, trip);
        }

        @Override
        public void onItemAdd() {
        }
    };

    public static Fragment newInstance() {
        return new EditProfileFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initList();
        initViewModel();
        binding.backButton.setOnClickListener(v -> getInterface().moveBack());
        binding.doneButton.setOnClickListener(v -> saveProfile());

        binding.userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mProfile.setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.userBio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mProfile.setBio(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void openTrip(View view, Trip trip) {
        getInterface().openFragment(TripPreviewFragment.newInstance(trip.getId()), TripPreviewFragment.TAG);
    }

    private void initList() {
        binding.placesList.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        binding.placesList.addItemDecoration(new GridMarginDecoration(getResources().getDimensionPixelSize(R.dimen.grid_item_spacing)));
        mAdapter = new TripsRecyclerAdapter(false, mAdapterCallback);
        binding.placesList.setAdapter(mAdapter);
    }

    private void saveProfile() {
        AppDb.getDatabase(getActivity()).dao().insertProfile(mProfile);
        getInterface().moveBack();
    }

    private void initViewModel() {
        ProfileViewModel viewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        viewModel.profile.observe(this, this::showUser);

        TripsViewModel tripsViewModel = ViewModelProviders.of(this).get(TripsViewModel.class);
        tripsViewModel.trips.observe(this, this::showTrips);
    }

    private void showTrips(List<Trip> trips) {
        binding.placesCount.setText(getNumberOfBeenders(trips) + "");
        if (mAdapter != null) {
            mAdapter.setTrips(trips);
        }
    }

    private int getNumberOfBeenders(List<Trip> trips) {
        int count = 0;
        for (Trip t : trips) count += t.getPlaces().size();
        return count;
    }

    private void showUser(Profile profile) {
        mProfile = profile;
        if (profile != null) {
            binding.userName.setText(profile.getName());
            binding.userBio.setText(profile.getBio());
            Picasso.with(getActivity())
                    .load(profile.getPhotoUrl())
                    .resize(500, 500)
                    .centerCrop()
                    .into(binding.userPhoto);
        }
    }

    @Override
    public boolean onBackPressed() {
        AppUtil.hideSoftKeyboard(getActivity());
        return super.onBackPressed();
    }
}
