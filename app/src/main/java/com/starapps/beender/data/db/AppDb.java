package com.starapps.beender.data.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.starapps.beender.data.Place;
import com.starapps.beender.data.Profile;
import com.starapps.beender.data.Trip;


@Database(entities = {Profile.class, Trip.class, Place.class}, version = 1, exportSchema = false)
@TypeConverters({ListStringConverter.class, ListPlaceConverter.class})
public abstract class AppDb extends RoomDatabase {

    private static AppDb INSTANCE;

    public abstract ProfileDao dao();
    public abstract TripDao tripDao();

    public static AppDb getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDb.class, "beeder_db")
                            // To simplify the codelab, allow queries on the main thread.
                            // Don't do this on a real app! See PersistenceBasicSample for an example.
                            .allowMainThreadQueries()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
