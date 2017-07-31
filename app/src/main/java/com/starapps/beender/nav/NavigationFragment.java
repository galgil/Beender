package com.starapps.beender.nav;

import android.app.Activity;
import android.arch.lifecycle.LifecycleFragment;
import android.content.Context;



public abstract class NavigationFragment extends LifecycleFragment {

    private NavigationInterface mInterface;

    public boolean onBackPressed() {
        return true;
    }

    public void onFragmentResume() {
        if (getInterface() != null) {
            getInterface().setCurrent(this);
            getInterface().showNavigation(true);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mInterface == null) {
            mInterface = (NavigationInterface) context;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (mInterface == null) {
            mInterface = (NavigationInterface) activity;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getInterface().setCurrent(this);
    }

    protected NavigationInterface getInterface() {
        return mInterface;
    }
}
