package com.starapps.beender.nav.explore;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.starapps.beender.databinding.FragmentExploreBinding;
import com.starapps.beender.nav.NavigationFragment;



public class ExploreFragment extends NavigationFragment {

    public static final String TAG = "ExploreFragment";

    private FragmentExploreBinding binding;

    public static Fragment newInstance() {
        return new ExploreFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentExploreBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
