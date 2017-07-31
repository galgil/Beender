package com.starapps.beender.data.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.starapps.beender.data.Profile;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;


@Dao
public interface ProfileDao {

    @Query("select * from Profile limit 1")
    LiveData<Profile> loadProfile();

    @Insert(onConflict = REPLACE)
    void insertProfile(Profile item);

    @Delete
    void deleteProfile(Profile item);

    @Query("DELETE FROM Profile")
    void deleteAll();
}
