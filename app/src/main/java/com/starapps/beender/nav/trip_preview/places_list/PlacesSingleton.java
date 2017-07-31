package com.starapps.beender.nav.trip_preview.places_list;

import com.starapps.beender.data.Place;

import java.util.ArrayList;
import java.util.List;



public class PlacesSingleton {

    private static PlacesSingleton instance;
    private List<PlacesRecyclerAdapter.AdapterCallback> callbacks = new ArrayList<>();
    private List<Place> places = new ArrayList<>();

    private PlacesSingleton() {
    }

    public static PlacesSingleton getInstance() {
        if (instance == null) {
            synchronized (PlacesSingleton.class) {
                if (instance == null) {
                    instance = new PlacesSingleton();
                }
            }
        }
        return instance;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }

    public void addCallback(PlacesRecyclerAdapter.AdapterCallback callback) {
        if (!callbacks.contains(callback)) {
            callbacks.add(callback);
        }
    }

    public void removeCallback(PlacesRecyclerAdapter.AdapterCallback callback) {
        if (callbacks.contains(callback)) {
            callbacks.remove(callback);
        }
    }

    public void notifyUpdates(List<Place> places) {
        for (PlacesRecyclerAdapter.AdapterCallback callback : callbacks) {
            callback.onChanged(places);
        }
    }
}
