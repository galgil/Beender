package com.starapps.beender.nav.profile;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.starapps.beender.R;
import com.starapps.beender.SplashScreenActivity;
import com.starapps.beender.data.Profile;
import com.starapps.beender.data.Trip;
import com.starapps.beender.data.db.AppDb;
import com.starapps.beender.databinding.DialogLogoutBinding;
import com.starapps.beender.databinding.DialogSearchBinding;
import com.starapps.beender.databinding.FragmentProfileBinding;
import com.starapps.beender.databinding.PopupProfileBinding;
import com.starapps.beender.nav.NavigationFragment;
import com.starapps.beender.nav.access.MemoryAccessFragment;
import com.starapps.beender.nav.trip.TripCreationFragment;
import com.starapps.beender.nav.trip_preview.TripPreviewFragment;
import com.starapps.beender.nav.trips.GridMarginDecoration;
import com.starapps.beender.nav.trips.TripsRecyclerAdapter;
import com.starapps.beender.nav.trips.TripsViewModel;
import com.starapps.beender.nav.tutorial.Tutorial;
import com.starapps.beender.nav.tutorial.TutorialControl;
import com.starapps.beender.utils.AppUtil;
import com.starapps.beender.utils.Prefs;

import java.util.ArrayList;
import java.util.List;

import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;



public class ViewProfileFragment extends NavigationFragment {

    public static final String TAG = "ProfileFragment";

    private static final int REQ_IMAGES = 2555;

    private FragmentProfileBinding binding;
    private SimpleTooltip mPopup;
    private AlertDialog mDialog;
    private TutorialControl mControl;
    private TripsRecyclerAdapter mAdapter;
    private TripsRecyclerAdapter.AdapterCallback mAdapterCallback = new TripsRecyclerAdapter.AdapterCallback() {
        @Override
        public void onItemClick(View view, Trip trip) {
            openTrip(view, trip);
        }

        @Override
        public void onItemAdd() {
            addImages();
        }
    };

    public static Fragment newInstance() {
        return new ViewProfileFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initList();
        initViewModel();
        binding.moreButton.setOnClickListener(this::showMorePopup);
        binding.searchButton.setOnClickListener(v -> showSearchDialog());
    }

    private void openTrip(View view, Trip trip) {
        getInterface().openFragment(TripPreviewFragment.newInstance(trip.getId()), TripPreviewFragment.TAG);
    }

    private void initList() {
        binding.placesList.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        binding.placesList.addItemDecoration(new GridMarginDecoration(getResources().getDimensionPixelSize(R.dimen.grid_item_spacing)));
        mAdapter = new TripsRecyclerAdapter(true, mAdapterCallback);
        binding.placesList.setAdapter(mAdapter);
    }

    private void showMorePopup(View v) {
        PopupProfileBinding binding = PopupProfileBinding.inflate(LayoutInflater.from(getActivity()));
        binding.editButton.setOnClickListener(v1 -> editProfile());
        binding.logoutButton.setOnClickListener(v1 -> showLogoutDialog());
        mPopup = new SimpleTooltip.Builder(getActivity())
                .anchorView(v)
                .contentView(binding.getRoot(), 0)
                .gravity(Gravity.BOTTOM)
                .animated(false)
                .padding(0f)
                .margin(0f)
                .transparentOverlay(true)
                .showArrow(false)
                .dismissOnInsideTouch(false)
                .dismissOnOutsideTouch(true)
                .build();
        mPopup.show();
    }

    private void showSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.TransparentAlertDialog);
        DialogSearchBinding binding = DialogSearchBinding.inflate(LayoutInflater.from(getActivity()));
        binding.closeButton.setOnClickListener(v -> hideDialog());
        builder.setView(binding.getRoot());
        mDialog = builder.create();
        mDialog.show();
    }

    private void showLogoutDialog() {
        hidePopup();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.TransparentAlertDialog);
        DialogLogoutBinding binding = DialogLogoutBinding.inflate(LayoutInflater.from(getActivity()));
        binding.cancelButton.setOnClickListener(v -> hideDialog());
        binding.logoutButton.setOnClickListener(v -> logOut());
        builder.setView(binding.getRoot());
        mDialog = builder.create();
        mDialog.show();
    }

    private void hideDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    private boolean hidePopup() {
        if (mPopup != null && mPopup.isShowing()) {
            mPopup.dismiss();
            return false;
        }
        return true;
    }

    private void logOut() {
        hideDialog();
        AppDb.getDatabase(getActivity()).dao().deleteAll();
        Prefs.getInstance(getActivity()).setLogged(false);
        Prefs.getInstance(getActivity()).setCurrentUser(null);
        startActivity(new Intent(getActivity(), SplashScreenActivity.class));
        getActivity().finish();
    }

    private void editProfile() {
        hidePopup();
        getInterface().openFragment(EditProfileFragment.newInstance(), EditProfileFragment.TAG);
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

    private void addImages() {
        if (!isGranted()) return;
        AppUtil.addImage(getActivity(), REQ_IMAGES);
    }

    private boolean isGranted() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            getInterface().openFragment(MemoryAccessFragment.newInstance(), MemoryAccessFragment.TAG);
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_IMAGES:
                if (resultCode == Activity.RESULT_OK) {
                    getImageFromGallery(data);
                }
                break;
        }
    }

    private void getImageFromGallery(Intent data) {
        String name = Prefs.getInstance(getActivity()).getCurrentUser();
        getInterface().openFragment(TripCreationFragment.newInstance(data, name), TripCreationFragment.TAG);
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        if (mAdapter.getItemCount() > 1) {
            if (!Prefs.getInstance(getActivity()).isTripFirstTime()) {
                Prefs.getInstance(getActivity()).setTripFirstTime(true);
                Trip trip = mAdapter.getItem(1);
                getInterface().openFragment(TripPreviewFragment.newInstance(trip.getId()), TripPreviewFragment.TAG);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!Prefs.getInstance(getActivity()).isProfileShowed()) {
            List<Tutorial> tutorials = new ArrayList<>();
            tutorials.add(new Tutorial(
                    binding.userBio,
                    getString(R.string.welcome_to_your_profile),
                    getString(R.string.user_bio_tutorial_message),
                    getString(R.string.ok_next),
                    Gravity.BOTTOM
            ));
            tutorials.add(new Tutorial(
                    binding.moreButton,
                    getString(R.string.edit_your_profile),
                    getString(R.string.profile_edit_tutorial_message),
                    getString(R.string.ok_next),
                    Gravity.BOTTOM
            ));
            new Handler().postDelayed(() -> {
                mControl = new TutorialControl(getActivity(), tutorials);
                Prefs.getInstance(getActivity()).setProfileShowed(true);
            }, 1000);
        }
    }

    @Override
    public boolean onBackPressed() {
        return !(mControl != null && !mControl.hidePopup()) && hidePopup();
    }
}
