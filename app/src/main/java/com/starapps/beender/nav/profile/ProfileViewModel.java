package com.starapps.beender.nav.profile;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.starapps.beender.data.Profile;
import com.starapps.beender.data.db.AppDb;



public class ProfileViewModel extends AndroidViewModel {

    LiveData<Profile> profile;

    public ProfileViewModel(Application application) {
        super(application);
        profile = AppDb.getDatabase(application).dao().loadProfile();
    }
}
