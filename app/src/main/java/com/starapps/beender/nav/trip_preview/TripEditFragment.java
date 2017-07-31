package com.starapps.beender.nav.trip_preview;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.starapps.beender.R;
import com.starapps.beender.data.Place;
import com.starapps.beender.data.Trip;
import com.starapps.beender.data.db.AppDb;
import com.starapps.beender.databinding.DialogSelectTagsBinding;
import com.starapps.beender.databinding.FragmentTripEditBinding;
import com.starapps.beender.nav.NestedFragment;
import com.starapps.beender.nav.access.MemoryAccessFragment;
import com.starapps.beender.nav.trip.TripCreationFragment;
import com.starapps.beender.nav.trip_preview.places_list.PlacesListFragment;
import com.starapps.beender.nav.trip_preview.places_list.PlacesRecyclerAdapter;
import com.starapps.beender.nav.trip_preview.places_list.PlacesSingleton;
import com.starapps.beender.nav.trip_preview.places_map.PlacesMapFragment;
import com.starapps.beender.nav.trip_preview.tags.TagsPartnersRecyclerAdapter;
import com.starapps.beender.nav.trip_preview.tags.TagsSpecificallyRecyclerAdapter;
import com.starapps.beender.nav.trip_preview.tags.TagsCheckedRecyclerAdapter;
import com.starapps.beender.nav.trip_preview.tags.TagsWeatherRecyclerAdapter;
import com.starapps.beender.utils.AppUtil;
import com.starapps.beender.utils.TimeUtil;
import com.starapps.beender.views.RadioImageView;
import com.xiaofeng.flowlayoutmanager.Alignment;
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager;

import java.util.ArrayList;
import java.util.List;



public class TripEditFragment extends NestedFragment implements PlacesRecyclerAdapter.AdapterCallback {

    public static final String TAG = "TripEditFragment";
    public static final String ARG_ID = "arg_id";

    private static final int REQ_IMAGES = 2555;
    private static final int REQ_COVER = 2557;

    private FragmentTripEditBinding binding;
    private int mId;
    private Trip mTrip;
    private int idImage;

    private RadioImageView.CheckControl control;
    private AlertDialog mDialog;
    private Fragment mCurrent;
    private List<String> tagsList;

    public static Fragment newInstance(int id) {
        TripEditFragment fragment = new TripEditFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_ID, id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mId = getArguments().getInt(ARG_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTripEditBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.backButton.setOnClickListener(v -> getInterface().moveBack());
        binding.doneButton.setOnClickListener(v -> saveTrip());
        binding.tagView.setOnClickListener(v -> showTagDialog());
        binding.addButton.setOnClickListener(v -> addPlace());
        binding.tripPhoto.setOnClickListener(v -> addCover());
        initScreenSwitcher();

        initViewModel();
        PlacesSingleton.getInstance().addCallback(this);
    }


    private void addCover() {
        if (!isGranted()) return;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_images)), REQ_COVER);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PlacesSingleton.getInstance().removeCallback(this);
    }

    private void addPlace() {
        if (!isGranted()) return;
        AppUtil.addImage(getActivity(), REQ_IMAGES);
    }

    private void saveTrip() {
        AppDb.getDatabase(getActivity()).tripDao().insertTrip(mTrip);
        getInterface().moveBack();
    }

    private void showTagDialog() {
        if (mTrip == null) {
            return;
        }
        List<String> listTags = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.TransparentAlertDialog);
        DialogSelectTagsBinding binding = DialogSelectTagsBinding.inflate(LayoutInflater.from(getActivity()));
        TagsPartnersRecyclerAdapter adapterPartners = new TagsPartnersRecyclerAdapter();
        binding.tagsListPartners.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        adapterPartners.selectTags(mTrip.getTags());
        TagsSpecificallyRecyclerAdapter adapterSpecifically = new TagsSpecificallyRecyclerAdapter();
        binding.tagsListPartners.setAdapter(adapterPartners);

        binding.nextButtonPartnersTags.setOnClickListener(v -> {
            if (adapterPartners.getSelectedItem() != null) {
                binding.tagsListSpecifically.setVisibility(View.VISIBLE);
                listTags.addAll(adapterPartners.getSelected());
                idImage = adapterPartners.getSelectedItem().getIconIdCheck();
                binding.imageViewSinglePartners.setImageResource(adapterPartners.getSelectedItem().getIconIdCheck());
                binding.vfSelectTags.showNext();
            }
        });

        binding.imageViewSinglePartners.setOnClickListener(v -> {
            binding.vfSelectTags.setDisplayedChild(0);
            listTags.clear();
        });

        FlowLayoutManager flowLayoutManagerSpec = new FlowLayoutManager();
        flowLayoutManagerSpec.setAutoMeasureEnabled(true);
        binding.tagsListSpecifically.setLayoutManager(flowLayoutManagerSpec.setAlignment(Alignment.LEFT));
        binding.tagsListSpecifically.setAdapter(adapterSpecifically);
        FlowLayoutManager flowLayoutManagerCheckedSpec = new FlowLayoutManager();
        flowLayoutManagerCheckedSpec.setAutoMeasureEnabled(true);

        binding.tagsListSpecifically.setVisibility(View.GONE);
        binding.tagsListCheckedSpecifically.setLayoutManager(flowLayoutManagerCheckedSpec.setAlignment(Alignment.LEFT));


        binding.nextButtonSpecifically.setOnClickListener(v -> {
            binding.tagsListSpecifically.setVisibility(View.GONE);
            listTags.addAll(adapterSpecifically.getSelected());
            binding.imageViewSinglePartnerWeather.setImageResource(idImage);
            binding.tagsListCheckedSpecifically.setAdapter(new TagsCheckedRecyclerAdapter(adapterSpecifically.getSelectedItems()));
            binding.vfSelectTags.showNext();

        });

        binding.imageViewSinglePartnerWeather.setOnClickListener(v -> {
            binding.tagsListSpecifically.setVisibility(View.GONE);
            binding.vfSelectTags.setDisplayedChild(0);
            listTags.clear();
        });

        TagsWeatherRecyclerAdapter adapterWeather = new TagsWeatherRecyclerAdapter();
        adapterWeather.setHasStableIds(true);
        FlowLayoutManager flowLayoutManagerWeather = new FlowLayoutManager();
        flowLayoutManagerWeather.setAutoMeasureEnabled(true);
        binding.tagsListWeather.setLayoutManager(flowLayoutManagerWeather);


        binding.tagsListWeather.setAdapter(adapterWeather);

        binding.doneButton3.setOnClickListener(v -> {
            listTags.addAll(adapterWeather.getSelected());
            saveTags(listTags);
            saveTrip();
        });


        builder.setView(binding.getRoot());
        mDialog = builder.create();
        mDialog.show();
    }


    private void saveTags(List<String> selected) {
        hideDialog();
        if (mTrip == null) return;
        mTrip.setTags(selected);
        showTags();
    }

    private void initScreenSwitcher() {
        binding.radioList.setChecked(true);
        control = new RadioImageView.CheckControl(binding.radioList, binding.radioMap);
        control.setTabListener(checkableImageView -> switchScreen(checkableImageView.getId()));
    }

    private void switchScreen(int id) {
        if (mTrip == null) {
            return;
        }
        switch (id) {
            case R.id.radio_list:
                mCurrent = PlacesListFragment.newInstance(mTrip, true);
                add(mCurrent, PlacesListFragment.TAG);
                break;
            case R.id.radio_map:
                mCurrent = PlacesMapFragment.newInstance(mTrip);
                add(mCurrent, PlacesMapFragment.TAG);
                break;
        }
    }

    private void add(Fragment fragment, String tag) {
        getChildFragmentManager().beginTransaction()
                .add(binding.subContent.getId(), fragment, tag)
                .commit();
    }

    private void hideDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    private void initViewModel() {
        TripViewModel.Factory factory = new TripViewModel.Factory(getActivity().getApplication(), mId);
        TripViewModel viewModel = ViewModelProviders.of(this, factory).get(TripViewModel.class);
        viewModel.trips.observe(this, this::showTrip);
    }

    private void showTrip(Trip trip) {
        if (mTrip != null) return;
        mTrip = trip;
        if (trip == null) return;
        switchScreen(control.getCheckedId());
        showSummary();
        showTags();
        showCover();
    }

    private void showCover() {
        Picasso.with(getActivity())
                .load(Uri.parse(mTrip.getPhotoUrl()))
//                .load(R.drawable.waterfall_photo)
                .resize(1280, 768)
                .centerCrop()
                .into(binding.tripPhoto);
    }

    private void showSummary() {
        binding.summaryView.setText(getSummary());
    }

    private void showTags() {
        if (mTrip == null) return;
        binding.tagView.setText(getTags(mTrip));
    }

    private String getTags(Trip trip) {
        String res = "";
        for (String t : trip.getTags()) res = res + "#" + t + " ";
        return res;
    }

    private String getSummary() {
        if (mTrip.getPlaces().isEmpty()) {
            return 0 + " " + getString(R.string.days) + ", " + 0 + " " + getString(R.string.beenders);
        } else {
            return TimeUtil.getRange(mTrip.getDtStart(), mTrip.getDtEnd()) + "\n" +
                    TimeUtil.getDiffDays(mTrip.getDtStart(), mTrip.getDtEnd()) + " " + getString(R.string.days) + ", " +
                    mTrip.getPlaces().size() + " " + getString(R.string.beenders);
        }
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

    private void getImageFromGallery(Intent data) {
        getInterface().openFragment(TripCreationFragment.newInstance(data, mTrip), TripCreationFragment.TAG);
    }

    private void calculateDates(Trip trip) {
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
        } else if (!trip.getPlaces().isEmpty()) {
            Place place = trip.getPlaces().get(0);
            trip.setDtEnd(place.getDate());
            trip.setDtStart(place.getDate());
        } else {
            trip.setDtStart("");
            trip.setDtEnd("");
        }
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
            case REQ_COVER:
                if (resultCode == Activity.RESULT_OK) {
                    addCoverFromGallery(data);
                }
                break;
        }
    }

    private void addCoverFromGallery(Intent data) {
        if (data.getData() != null) {
            mTrip.setPhotoUrl(data.getData().toString());
            showCover();
        }
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        getInterface().showNavigation(false);
        if (!PlacesSingleton.getInstance().getPlaces().isEmpty() && mTrip != null) {
            onChanged(PlacesSingleton.getInstance().getPlaces());
            switchScreen(control.getCheckedId());
            PlacesSingleton.getInstance().setPlaces(new ArrayList<>());
        }
    }

    @Override
    public void onChanged(List<Place> places) {
        mTrip.setPlaces(places);
        calculateDates(mTrip);
        showSummary();
    }

    @Override
    public boolean onBackPressed() {
        if (mCurrent != null) {
            if (!((NestedFragment) mCurrent).onBackPressed()) {
                return false;
            }
        }
        return true;
    }
}
