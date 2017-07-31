package com.starapps.beender.data.db;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.starapps.beender.data.Place;

import java.lang.reflect.Type;
import java.util.List;



public class ListPlaceConverter {

    @TypeConverter
    public static String toString(List<Place> list) {
        return new Gson().toJson(list);
    }

    @TypeConverter
    public static List<Place> toList(String s) {
        Type type = new TypeToken<List<Place>>(){}.getType();
        return new Gson().fromJson(s, type);
    }

}
