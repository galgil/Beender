package com.starapps.beender.nav.favourite;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.starapps.beender.databinding.FragmentStarBinding;
import com.starapps.beender.nav.NavigationFragment;


public class FavouriteFragment extends NavigationFragment {

    public static final String TAG = "FavouriteFragment";

    private FragmentStarBinding binding;

    public static Fragment newInstance() {
        return new FavouriteFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStarBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
