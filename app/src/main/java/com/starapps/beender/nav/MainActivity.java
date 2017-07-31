package com.starapps.beender.nav;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import com.starapps.beender.R;
import com.starapps.beender.databinding.ActivityMainBinding;
import com.starapps.beender.nav.explore.ExploreFragment;
import com.starapps.beender.nav.favourite.FavouriteFragment;
import com.starapps.beender.nav.profile.ViewProfileFragment;
import com.starapps.beender.utils.BlackStatusActivity;
import com.starapps.beender.views.CheckableImageView;

public class MainActivity extends BlackStatusActivity implements
        CheckableImageView.OnTabSelectedListener, NavigationInterface, FragmentManager.OnBackStackChangedListener {

    private static final String TAG = "MainActivity";

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initStatusBar(binding.content);

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        initBottomNavigation();
    }

    private void initBottomNavigation() {
        binding.tabProfile.setChecked(true);
        CheckableImageView.CheckControl mControl = new CheckableImageView.CheckControl(binding.tabStar,
                binding.tabExplore, binding.tabProfile);
        mControl.setTabListener(this);
        switchScreen(mControl.getCheckedId());
    }

    @Override
    public void onTabSelect(CheckableImageView checkableImageView) {
        int id = checkableImageView.getId();
        switchScreen(id);
    }

    private void replace(Fragment fragment, String tag) {
        clearBackStack();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, fragment, tag)
                .addToBackStack(tag)
                .commit();
    }

    private void clearBackStack() {
        FragmentManager fm = getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    private void switchScreen(int id) {
        switch (id) {
            case R.id.tab_explore:
                replace(ExploreFragment.newInstance(), ExploreFragment.TAG);
                break;
            case R.id.tab_star:
                replace(FavouriteFragment.newInstance(), FavouriteFragment.TAG);
                break;
            case R.id.tab_profile:
                replace(ViewProfileFragment.newInstance(), ViewProfileFragment.TAG);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        NavigationFragment fragment = (NavigationFragment) getCurrent();
        if (!fragment.onBackPressed()) {
            return;
        }
        if (fragment instanceof NestedFragment) {
            try {
                super.onBackPressed();
            } catch (IllegalStateException ignored) {
            }
        } else {
            finish();
        }
    }

    private Fragment getCurrent() {
        return getSupportFragmentManager().findFragmentById(R.id.content);
    }

    @Override
    public void onBackStackChanged() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager != null) {
            Log.d(TAG, "onBackStackChanged: " + manager.getBackStackEntryCount());
            NavigationFragment fragment = (NavigationFragment) manager.findFragmentById(R.id.content);
            if (fragment != null) {
                fragment.onFragmentResume();
            }
        }
    }

    @Override
    public void openFragment(Fragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content, fragment, tag)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(tag)
                .commit();
    }

    @Override
    public void setCurrent(Fragment fragment) {
    }

    @Override
    public void moveBack() {
        onBackPressed();
    }

    @Override
    public void showNavigation(boolean show) {
        if (show && !isNavVisible()) {
            binding.navView.setVisibility(View.VISIBLE);
        } else if (!show && isNavVisible()) {
            binding.navView.setVisibility(View.GONE);
        }
    }

    private boolean isNavVisible() {
        return binding.navView.getVisibility() == View.VISIBLE;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (getCurrent() != null) {
            getCurrent().onActivityResult(requestCode, resultCode, data);
        }
    }
}
