package com.starapps.beender.nav.trip_preview.places_list;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.starapps.beender.R;
import com.starapps.beender.data.Trip;
import com.starapps.beender.databinding.FragmentPlacesListBinding;
import com.starapps.beender.nav.NestedFragment;
import com.starapps.beender.nav.tutorial.Tutorial;
import com.starapps.beender.nav.tutorial.TutorialControl;
import com.starapps.beender.utils.Prefs;

import java.util.ArrayList;
import java.util.List;



public class PlacesListFragment extends NestedFragment {

    public static final String TAG = "PlacesListFragment";
    public static final String ARG_PLACES = "arg_places";
    public static final String ARG_EDIT = "arg_edit";

    private FragmentPlacesListBinding binding;
    private PlacesRecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Trip mTrip;
    private boolean mIsEditable;
    private TutorialControl mControl;

    public static Fragment newInstance(Trip trip, boolean edit) {
        Fragment fragment = new PlacesListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_PLACES, trip);
        bundle.putBoolean(ARG_EDIT, edit);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTrip = (Trip) getArguments().getSerializable(ARG_PLACES);
            mIsEditable = getArguments().getBoolean(ARG_EDIT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPlacesListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initList();
    }

    private void initList() {
        layoutManager = new LinearLayoutManager(getActivity()) {
            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                super.onLayoutChildren(recycler, state);
                int firstVisibleItemPosition = findFirstVisibleItemPosition();
                if (firstVisibleItemPosition == 0) {
                    showTutorial(findViewByPosition(firstVisibleItemPosition));
                }
            }
        };
        binding.placesList.setLayoutManager(layoutManager);
        mAdapter = new PlacesRecyclerAdapter(mIsEditable);
        binding.placesList.setAdapter(mAdapter);
        mAdapter.setPlaces(mTrip.getPlaces());
    }

    private void showTutorial(View viewByPosition) {
        if (mControl != null) return;
        if (!Prefs.getInstance(getActivity()).isListShowed()) {
            if (mAdapter.getItemCount() == 0) return;
            if (viewByPosition == null) return;
            Prefs.getInstance(getActivity()).setListShowed(true);
            List<Tutorial> tutorials = new ArrayList<>();
            tutorials.add(new Tutorial(
                    viewByPosition.findViewById(R.id.like_view),
                    getString(R.string.add_like),
                    getString(R.string.tutorial_like_message),
                    getString(R.string.ok_next),
                    Gravity.BOTTOM
            ));
            tutorials.add(new Tutorial(
                    viewByPosition.findViewById(R.id.comment_field),
                    getString(R.string.personal_comment),
                    getString(R.string.tutorial_comment_message),
                    getString(R.string.done),
                    Gravity.BOTTOM
            ));
            new Handler().postDelayed(() -> mControl = new TutorialControl(getActivity(), tutorials), 1000);
        }
    }
}
