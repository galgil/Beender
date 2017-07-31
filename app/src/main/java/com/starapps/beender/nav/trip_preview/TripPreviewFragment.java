package com.starapps.beender.nav.trip_preview;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.starapps.beender.R;
import com.starapps.beender.data.Trip;
import com.starapps.beender.data.db.AppDb;
import com.starapps.beender.databinding.DialogDeleteTripBinding;
import com.starapps.beender.databinding.DialogSelectTagsBinding;
import com.starapps.beender.databinding.FragmentTripViewBinding;
import com.starapps.beender.databinding.PopupTripBinding;
import com.starapps.beender.nav.NestedFragment;
import com.starapps.beender.nav.trip_preview.places_list.PlacesListFragment;
import com.starapps.beender.nav.trip_preview.places_map.PlacesMapFragment;
import com.starapps.beender.nav.trip_preview.tags.TagsPartnersRecyclerAdapter;
import com.starapps.beender.nav.trip_preview.tags.TagsSpecificallyRecyclerAdapter;
import com.starapps.beender.nav.trip_preview.tags.TagsCheckedRecyclerAdapter;
import com.starapps.beender.nav.trip_preview.tags.TagsWeatherRecyclerAdapter;
import com.starapps.beender.nav.tutorial.Tutorial;
import com.starapps.beender.nav.tutorial.TutorialControl;
import com.starapps.beender.utils.Prefs;
import com.starapps.beender.utils.TimeUtil;
import com.starapps.beender.views.RadioImageView;
import com.xiaofeng.flowlayoutmanager.Alignment;
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager;

import java.util.ArrayList;
import java.util.List;

import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;



public class TripPreviewFragment extends NestedFragment {

    public static final String TAG = "TripPreviewFragment";
    public static final String ARG_ID = "arg_id";

    private FragmentTripViewBinding binding;
    private int mId;
    private Trip mTrip;

    private SimpleTooltip mPopup;
    private TutorialControl mControl;
    private AlertDialog mDialog;
    private RadioImageView.CheckControl control;
    private Fragment mCurrent;
    private int idImage;

    public static Fragment newInstance(int id) {
        TripPreviewFragment fragment = new TripPreviewFragment();
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
        binding = FragmentTripViewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.backButton.setOnClickListener(v -> getInterface().moveBack());
        binding.moreButton.setOnClickListener(this::showMorePopup);
        binding.tagView.setOnClickListener(v -> showTagDialog());
        initScreenSwitcher();

        initViewModel();
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
            binding.tagsListSpecifically.setVisibility(View.GONE);
            binding.vfSelectTags.setDisplayedChild(0);
            listTags.clear();
        });
        binding.tagsListSpecifically.setVisibility(View.GONE);

        FlowLayoutManager flowLayoutManagerSpec = new FlowLayoutManager();
        flowLayoutManagerSpec.setAutoMeasureEnabled(true);
        binding.tagsListSpecifically.setLayoutManager(flowLayoutManagerSpec.setAlignment(Alignment.LEFT));
        binding.tagsListSpecifically.setAdapter(adapterSpecifically);
        FlowLayoutManager flowLayoutManagerCheckedSpec = new FlowLayoutManager();
        flowLayoutManagerCheckedSpec.setAutoMeasureEnabled(true);
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
        binding.radioMap.setChecked(true);
        control = new RadioImageView.CheckControl(binding.radioList, binding.radioMap);
        control.setTabListener(checkableImageView -> switchScreen(checkableImageView.getId()));
    }

    private void switchScreen(int id) {
        if (mTrip == null) {
            return;
        }
        switch (id) {
            case R.id.radio_list:
                mCurrent = PlacesListFragment.newInstance(mTrip, false);
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

    private void showMorePopup(View v) {
        PopupTripBinding binding = PopupTripBinding.inflate(LayoutInflater.from(getActivity()));
        binding.visibilityGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (mTrip == null) return;
            if (checkedId == R.id.radio_public) {
                mTrip.setPrivate(false);
            } else {
                mTrip.setPrivate(true);
            }
        });
        if (mTrip != null) {
            if (mTrip.isPrivate()) {
                binding.radioPrivate.setChecked(true);
            } else {
                binding.radioPublic.setChecked(true);
            }
        }
        binding.deleteButton.setOnClickListener(v1 -> showDeleteDialog());
        binding.editButton.setOnClickListener(v12 -> editTrip());

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
                .build();
        mPopup.show();
    }

    private void editTrip() {
        hidePopup();
        hideDialog();
        getInterface().openFragment(TripEditFragment.newInstance(mId), TripEditFragment.TAG);
    }

    private void showDeleteDialog() {
        hidePopup();
        if (mTrip == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.TransparentAlertDialog);
        DialogDeleteTripBinding binding = DialogDeleteTripBinding.inflate(LayoutInflater.from(getActivity()));
        binding.cancelButton.setOnClickListener(v -> hideDialog());
        binding.deleteButton.setOnClickListener(v -> deleteTrip());
        binding.editButton.setOnClickListener(v -> editTrip());
        builder.setView(binding.getRoot());
        mDialog = builder.create();
        mDialog.show();
    }

    private void deleteTrip() {
        AppDb.getDatabase(getActivity()).tripDao().deleteTrip(mTrip);
        hideDialog();
        mTrip = null;
        getInterface().moveBack();
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

    private void initViewModel() {
        TripViewModel.Factory factory = new TripViewModel.Factory(getActivity().getApplication(), mId);
        TripViewModel viewModel = ViewModelProviders.of(this, factory).get(TripViewModel.class);
        viewModel.trips.observe(this, this::showTrip);
    }

    private void showTrip(Trip trip) {
        mTrip = trip;
        if (trip == null) return;
        switchScreen(control.getCheckedId());
        binding.nameView.setText(trip.getName());
        binding.summaryView.setText(getSummary(trip));
        showTags();
        Picasso.with(getActivity())
                .load(Uri.parse(trip.getPhotoUrl()))
//                .load(R.drawable.waterfall_photo)
                .resize(1280, 768)
                .centerCrop()
                .into(binding.tripPhoto);
        showTutorial();
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

    private String getSummary(Trip trip) {
        if (trip.getPlaces().isEmpty()) {
            return 0 + " " + getString(R.string.days) + ", " + 0 + " " + getString(R.string.beenders);
        } else {
            return TimeUtil.getRange(trip.getDtStart(), trip.getDtEnd()) + "\n" +
                    TimeUtil.getDiffDays(trip.getDtStart(), trip.getDtEnd()) + " " + getString(R.string.days) + ", " +
                    trip.getPlaces().size() + " " + getString(R.string.beenders);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mTrip != null) {
            AppDb.getDatabase(getActivity()).tripDao().insertTrip(mTrip);
        }
    }

    private void showTutorial() {
        if (!Prefs.getInstance(getActivity()).isTripShowed()) {
            List<Tutorial> tutorials = new ArrayList<>();
            tutorials.add(new Tutorial(
                    binding.tagView,
                    getString(R.string.welcome_to_your_trip),
                    getString(R.string.trip_tags_message),
                    getString(R.string.ok_next),
                    Gravity.BOTTOM
            ));
            tutorials.add(new Tutorial(
                    binding.switchView,
                    getString(R.string.switch_to_list_view),
                    getString(R.string.click_here_to_switch_to_list_view),
                    getString(R.string.ok_next),
                    Gravity.START
            ));
            tutorials.add(new Tutorial(
                    binding.moreButton,
                    getString(R.string.edit_your_trip),
                    getString(R.string.tutorial_more_button_message),
                    getString(R.string.ok_next),
                    Gravity.BOTTOM
            ));
            tutorials.add(new Tutorial(
                    binding.shareButton,
                    getString(R.string.easily_share_it_with_others),
                    getString(R.string.tutorial_share_button_message),
                    getString(R.string.done),
                    Gravity.BOTTOM
            ));
            new Handler().postDelayed(() -> {
                mControl = new TutorialControl(getActivity(), tutorials);
                Prefs.getInstance(getActivity()).setTripShowed(true);
            }, 2000);
        }
    }

    @Override
    public boolean onBackPressed() {
        if (mControl != null) {
            if (!mControl.hidePopup()) {
                return false;
            }
        }
        if (mCurrent != null) {
            if (!((NestedFragment) mCurrent).onBackPressed()) {
                return false;
            }
        }
        return hidePopup();
    }

    private void saveTrip() {
        AppDb.getDatabase(getActivity()).tripDao().insertTrip(mTrip);
    }

    private void initPartnersAdapter() {

    }
}
