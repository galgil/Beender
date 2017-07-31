package com.starapps.beender.data.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.starapps.beender.data.Trip;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;


@Dao
public interface TripDao {

    @Query("select * from Trip where authorName = :name order by dtStart DESC")
    LiveData<List<Trip>> loadTrips(String name);

    @Query("select * from Trip where authorName = :name order by dtStart DESC")
    List<Trip> getTrips(String name);

    @Query("select * from Trip where authorName = :authorName and name = :name order by dtStart DESC")
    List<Trip> getTrips(String authorName, String name);

    @Query("select * from Trip where id = :id")
    LiveData<Trip> loadTrip(int id);

    @Insert(onConflict = REPLACE)
    void insertTrip(Trip item);

    @Insert(onConflict = REPLACE)
    void insertTrips(List<Trip> trips);

    @Delete
    void deleteTrip(Trip item);

    @Query("DELETE FROM Trip where id = :id")
    void deleteTrip(int id);

    @Query("DELETE FROM Trip")
    void deleteAll();
}
