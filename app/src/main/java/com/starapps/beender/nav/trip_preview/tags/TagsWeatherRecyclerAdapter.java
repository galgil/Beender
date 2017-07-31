package com.starapps.beender.nav.trip_preview.tags;


import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.starapps.beender.R;
import com.starapps.beender.databinding.LayoutTagsWeatherBinding;


import java.util.ArrayList;
import java.util.List;

public class TagsWeatherRecyclerAdapter extends RecyclerView.Adapter<TagsWeatherRecyclerAdapter.WeatherHolder> {

    private List<TagItem> tagItems = new ArrayList<>();
    private int previousPosition = -1;


    public TagsWeatherRecyclerAdapter() {
        this.tagItems = TagItem.Factory.getWeather();
    }

    @Override
    public WeatherHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WeatherHolder(LayoutTagsWeatherBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false).getRoot());
    }

    @Override
    public void onBindViewHolder(WeatherHolder holder, int position) {
        holder.layoutWeatherTagBinding.setTag(tagItems.get(position));
        if (position == previousPosition) {
            holder.layoutWeatherTagBinding.textViewWeather.setBackgroundResource(R.drawable.rectangle_accent_filled);
            holder.layoutWeatherTagBinding.textViewWeather.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorWhite));
            tagItems.get(position).setChecked(true);
        } else {
            holder.layoutWeatherTagBinding.textViewWeather.setBackgroundResource(R.drawable.rectangle_accent_line);
            holder.layoutWeatherTagBinding.textViewWeather.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorAccent));
            tagItems.get(position).setChecked(false);
        }

    }

    @Override
    public int getItemCount() {
        return tagItems.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public List<String> getSelected() {
        List<String> tags = new ArrayList<>();
        for (TagItem item : tagItems) if (item.isChecked()) tags.add(item.getTitle());
        return tags;
    }

    public class WeatherHolder extends RecyclerView.ViewHolder {

        LayoutTagsWeatherBinding layoutWeatherTagBinding;

        public WeatherHolder(View itemView) {
            super(itemView);
            layoutWeatherTagBinding = DataBindingUtil.bind(itemView);
            layoutWeatherTagBinding.textViewWeather.setOnClickListener(v -> {
                previousPosition = getAdapterPosition();
                notifyDataSetChanged();
            });
        }


    }
}

