package com.starapps.beender.nav;

import android.support.v4.app.Fragment;



public interface NavigationInterface {
    void openFragment(Fragment fragment, String tag);

    void setCurrent(Fragment fragment);

    void moveBack();

    void showNavigation(boolean show);
}
