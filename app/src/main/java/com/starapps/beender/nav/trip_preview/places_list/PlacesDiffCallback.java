package com.starapps.beender.nav.trip_preview.places_list;

import android.support.v7.util.DiffUtil;

import com.starapps.beender.data.Place;

import java.util.ArrayList;
import java.util.List;



public class PlacesDiffCallback extends DiffUtil.Callback {

    private List<Place> mOldPlaces = new ArrayList<>();
    private List<Place> mNewPlaces = new ArrayList<>();

    public PlacesDiffCallback(List<Place> mOldPlaces, List<Place> mNewPlaces) {
        this.mOldPlaces = mOldPlaces;
        this.mNewPlaces = mNewPlaces;
    }

    @Override
    public int getOldListSize() {
        return mOldPlaces.size();
    }

    @Override
    public int getNewListSize() {
        return mNewPlaces.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldPlaces.get(oldItemPosition).getId() == mNewPlaces.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        boolean l1 = mOldPlaces.get(oldItemPosition).isLiked();
        boolean l2 = mNewPlaces.get(newItemPosition).isLiked();

        String c1 = mOldPlaces.get(oldItemPosition).getComment();
        String c2 = mNewPlaces.get(newItemPosition).getComment();
        return c1.equals(c2) && l1 == l2;
    }
}
