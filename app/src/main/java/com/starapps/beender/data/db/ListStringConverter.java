package com.starapps.beender.data.db;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;



public class ListStringConverter {

    @TypeConverter
    public static String toString(List<String> list) {
        return new Gson().toJson(list);
    }

    @TypeConverter
    public static List<String> toList(String s) {
        Type type = new TypeToken<List<String>>(){}.getType();
        return new Gson().fromJson(s, type);
    }

}
