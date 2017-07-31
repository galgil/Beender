package com.starapps.beender.utils;

import android.content.Context;
import android.content.SharedPreferences;



public class Prefs {

    private static final String PREFS_NAME = "beeder_prefs";
    private static final String LOGGED = "is_logged";
    private static final String IS_PROFILE_SHOWED = "is_profile_tutorial_showed";
    private static final String IS_TRIP_SHOWED = "is_trip_tutorial_showed";
    private static final String IS_LIST_SHOWED = "is_places_tutorial_showed";
    private static final String IS_TRIP_FIRST_TIME = "is_trip_first_time";
    private static final String CURRENT_USER = "current_user";

    private static Prefs instance;
    private SharedPreferences prefs;

    private Prefs(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static Prefs getInstance(Context context) {
        if (instance == null) {
            synchronized (Prefs.class) {
                if (instance == null) {
                    instance = new Prefs(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public String getCurrentUser() {
        return prefs.getString(CURRENT_USER, "");
    }

    public void setCurrentUser(String currentUser) {
        prefs.edit().putString(CURRENT_USER, currentUser).apply();
    }

    public boolean isListShowed() {
        return prefs.getBoolean(IS_LIST_SHOWED, false);
    }

    public void setListShowed(boolean value) {
        prefs.edit().putBoolean(IS_LIST_SHOWED, value).apply();
    }

    public boolean isTripShowed() {
        return prefs.getBoolean(IS_TRIP_SHOWED, false);
    }

    public void setTripShowed(boolean value) {
        prefs.edit().putBoolean(IS_TRIP_SHOWED, value).apply();
    }

    public boolean isTripFirstTime() {
        return prefs.getBoolean(IS_TRIP_FIRST_TIME, false);
    }

    public void setTripFirstTime(boolean value) {
        prefs.edit().putBoolean(IS_TRIP_FIRST_TIME, value).apply();
    }

    public boolean isProfileShowed() {
        return prefs.getBoolean(IS_PROFILE_SHOWED, false);
    }

    public void setProfileShowed(boolean value) {
        prefs.edit().putBoolean(IS_PROFILE_SHOWED, value).apply();
    }

    public boolean isLogged() {
        return prefs.getBoolean(LOGGED, false);
    }

    public void setLogged(boolean value) {
        prefs.edit().putBoolean(LOGGED, value).apply();
    }
}
