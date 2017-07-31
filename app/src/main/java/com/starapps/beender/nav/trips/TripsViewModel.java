package com.starapps.beender.nav.trips;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.starapps.beender.data.Trip;
import com.starapps.beender.data.db.AppDb;
import com.starapps.beender.utils.Prefs;

import java.util.List;


public class TripsViewModel extends AndroidViewModel {

    public LiveData<List<Trip>> trips;

    public TripsViewModel(Application application) {
        super(application);
        String name = Prefs.getInstance(application).getCurrentUser();
        trips = AppDb.getDatabase(application).tripDao().loadTrips(name);
    }
}
