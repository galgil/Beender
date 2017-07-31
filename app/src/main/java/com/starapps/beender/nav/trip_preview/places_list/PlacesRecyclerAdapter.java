package com.starapps.beender.nav.trip_preview.places_list;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.starapps.beender.R;
import com.starapps.beender.data.Place;
import com.starapps.beender.databinding.ItemPlaceBinding;
import com.starapps.beender.utils.ImageUtil;
import com.starapps.beender.utils.TimeUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




public class PlacesRecyclerAdapter extends RecyclerView.Adapter<PlacesRecyclerAdapter.PlaceHolder> {

    private List<Place> places = new ArrayList<>();
    private Map<Place, Integer> daysMap = new HashMap<>();
    private boolean isEditable;

    public PlacesRecyclerAdapter(boolean isEditable) {
        this.isEditable = isEditable;
    }

    @Override
    public PlaceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PlaceHolder(ItemPlaceBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false).getRoot());
    }

    @Override
    public void onBindViewHolder(PlaceHolder holder, int position) {
        holder.binding.setPlace(places.get(position));
        holder.binding.setIsEdit(isEditable);
        if (isEditable) {
            holder.binding.pinView.setImageResource(R.drawable.ic_delete);
        } else {
            loadPin(holder.binding.pinView, places.get(position).getCategory());
        }
        addHeader(holder, position);
    }

    private void addHeader(PlaceHolder holder, int position) {
        Context context = holder.binding.getRoot().getContext();
        Place place = places.get(position);
        if (position == 0) {
            showHeader(holder, context, place);
        } else {
            Place prev = places.get(position - 1);
            if (TimeUtil.isSameDay(place.getDate(), prev.getDate())) {
                holder.binding.headerView.setVisibility(View.GONE);
            } else {
                showHeader(holder, context, place);
            }
        }
    }

    private void showHeader(PlaceHolder holder, Context context, Place place) {
        holder.binding.headerView.setVisibility(View.VISIBLE);
        holder.binding.headerView.setText(context.getString(R.string.day) + " " + daysMap.get(place) + " - " +
                TimeUtil.getFormatted(place.getDate()));
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public void setPlaces(List<Place> places) {
        Collections.sort(places, (o1, o2) -> o1.getDate().compareTo(o2.getDate()));
        calculateMap(places);
        PlacesDiffCallback diffCallback = new PlacesDiffCallback(this.places, places);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        this.places.clear();
        this.places.addAll(places);
        diffResult.dispatchUpdatesTo(this);
    }

    private void calculateMap(List<Place> places) {
        daysMap.clear();
        int day = 1;
        int prevDay = day;
        for (int i = 0; i < places.size(); i++) {
            Place place = places.get(i);
            if (i == 0) {
                daysMap.put(place, day);
                prevDay = day;
                day++;
            } else {
                Place prev = places.get(i - 1);
                if (TimeUtil.isSameDay(place.getDate(), prev.getDate())) {
                    daysMap.put(place, prevDay);
                } else {
                    daysMap.put(place, day);
                    prevDay = day;
                    day++;
                }
            }
        }
    }

    public class PlaceHolder extends RecyclerView.ViewHolder {

        ItemPlaceBinding binding;

        public PlaceHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            binding.likeView.setOnClickListener(v -> updateLike(getAdapterPosition()));
            if (isEditable) {
                binding.pinView.setOnClickListener(v -> deletePlace(getAdapterPosition()));
                binding.commentField.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        updateComment(s.toString(), getAdapterPosition());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
        }
    }

    private void updateComment(String s, int position) {
        this.places.get(position).setComment(s);
        PlacesSingleton.getInstance().notifyUpdates(this.places);
    }

    private void deletePlace(int position) {
        this.places.remove(position);
        this.notifyItemRemoved(position);
        this.notifyItemRangeChanged(0, getItemCount());
        PlacesSingleton.getInstance().notifyUpdates(this.places);
    }

    private void updateLike(int position) {
        this.places.get(position).setLiked(!this.places.get(position).isLiked());
        this.notifyItemChanged(position);
        PlacesSingleton.getInstance().notifyUpdates(this.places);
    }

    @BindingAdapter({"loadPin"})
    public static void loadPin(ImageView imageView, String category) {
        imageView.setImageResource(ImageUtil.getPinIcon(category));
    }

    public interface AdapterCallback {
        void onChanged(List<Place> places);
    }
}
