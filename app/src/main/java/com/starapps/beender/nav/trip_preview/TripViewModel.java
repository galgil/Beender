package com.starapps.beender.nav.trip_preview;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.starapps.beender.data.Trip;
import com.starapps.beender.data.db.AppDb;



public class TripViewModel extends AndroidViewModel {

    public LiveData<Trip> trips;

    public TripViewModel(Application application, int id) {
        super(application);
        trips = AppDb.getDatabase(application).tripDao().loadTrip(id);
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        private Application application;
        private int id;

        public Factory(Application application, int id) {
            this.application = application;
            this.id = id;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new TripViewModel(application, id);
        }
    }
}
