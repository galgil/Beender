package com.starapps.beender.nav.trips;

import android.support.v7.util.DiffUtil;

import com.starapps.beender.data.Trip;

import java.util.ArrayList;
import java.util.List;



public class TripsDiffCallback extends DiffUtil.Callback {

    private List<Trip> mOld = new ArrayList<>();
    private List<Trip> mNew = new ArrayList<>();

    public TripsDiffCallback(List<Trip> mOld, List<Trip> mNew) {
        this.mOld = mOld;
        this.mNew = mNew;
    }

    @Override
    public int getOldListSize() {
        return mOld.size();
    }

    @Override
    public int getNewListSize() {
        return mNew.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOld.get(oldItemPosition).getId() == mNew.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        boolean l1 = mOld.get(oldItemPosition).isPrivate();
        boolean l2 = mNew.get(newItemPosition).isPrivate();

        int s1 = mOld.get(oldItemPosition).getTags().size();
        int s2 = mNew.get(newItemPosition).getTags().size();

        int p1 = mOld.get(oldItemPosition).getPlaces().size();
        int p2 = mNew.get(newItemPosition).getPlaces().size();

        String photo1 = mOld.get(oldItemPosition).getPhotoUrl();
        String photo2 = mNew.get(newItemPosition).getPhotoUrl();
        if (photo1 == null) photo1 = "";
        if (photo2 == null) photo2 = "";
        return s1 == s2 && l1 == l2 && p1 == p2 && photo1.equals(photo2);
    }
}
