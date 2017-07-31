package com.starapps.beender.nav.trips;

import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.starapps.beender.R;
import com.starapps.beender.data.Trip;
import com.starapps.beender.databinding.ItemTripAddBinding;
import com.starapps.beender.databinding.ItemTripBinding;

import java.util.ArrayList;
import java.util.List;



public class TripsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Trip> trips = new ArrayList<>();
    private boolean hasAdd;
    private AdapterCallback mCallback;

    public TripsRecyclerAdapter(boolean hasAdd, AdapterCallback callback) {
        this.hasAdd = hasAdd;
        this.mCallback = callback;
        if (hasAdd) {
            this.trips.add(new Trip());
        }
    }

    public void setTrips(List<Trip> trips) {
        if (hasAdd) {
            trips.add(0, this.trips.get(0));
        }
        TripsDiffCallback diffCallback = new TripsDiffCallback(this.trips, trips);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        this.trips.clear();
        this.trips.addAll(trips);
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            return new TripAddHolder(ItemTripAddBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false).getRoot());
        } else {
            return new TripHolder(ItemTripBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false).getRoot());
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TripHolder) {
            ((TripHolder) holder).binding.setTrip(trips.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (hasAdd && position == 0) return 1;
        return 0;
    }

    public Trip getItem(int position) {
        return trips.get(position);
    }

    private class TripAddHolder extends RecyclerView.ViewHolder {

        ItemTripAddBinding binding;

        TripAddHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            binding.getRoot().setOnClickListener(v -> addTrip());
            Picasso.with(itemView.getContext())
                    .load(R.drawable.add_button_bg)
                    .resize(200, 200)
                    .centerCrop()
                    .into(binding.bgImage);
        }
    }

    private void addTrip() {
        if (mCallback != null) {
            mCallback.onItemAdd();
        }
    }

    private class TripHolder extends RecyclerView.ViewHolder {

        ItemTripBinding binding;

        TripHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            binding.getRoot().setOnClickListener(v -> openTrip(v, getAdapterPosition()));
        }
    }

    private void openTrip(View view, int position) {
        if (mCallback != null) {
            mCallback.onItemClick(view, trips.get(position));
        }
    }

    @BindingAdapter({"loadImage"})
    public static void loadImage(ImageView imageView, String url) {
        Picasso.with(imageView.getContext())
                .load(Uri.parse(url))
//                .load(R.drawable.waterfall_photo)
                .resize(150, 150)
                .centerCrop()
                .into(imageView);
    }

    public interface AdapterCallback {
        void onItemClick(View view, Trip trip);

        void onItemAdd();
    }
}
