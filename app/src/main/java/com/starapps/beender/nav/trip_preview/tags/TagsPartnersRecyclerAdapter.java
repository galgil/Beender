package com.starapps.beender.nav.trip_preview.tags;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.starapps.beender.databinding.LayoutTagsPartnersBinding;

import java.util.ArrayList;
import java.util.List;

public class TagsPartnersRecyclerAdapter extends RecyclerView.Adapter<TagsPartnersRecyclerAdapter.TagsPartnersHolder> {

    private List<TagItem> tagItems = new ArrayList<>();
    private int previousPosition = -1;

    public TagsPartnersRecyclerAdapter() {
        this.tagItems = TagItem.Factory.get();
    }

    public void selectTags(List<String> list) {
        for (String tag : list) {
            select(tag);
        }
        notifyDataSetChanged();
    }

    private void select(String tag) {
        for (TagItem item : tagItems) {
            if (item.getTitle().equals(tag)) {
                item.setChecked(true);
            }
        }
    }

    @Override
    public TagsPartnersRecyclerAdapter.TagsPartnersHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TagsPartnersRecyclerAdapter.TagsPartnersHolder(LayoutTagsPartnersBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false).getRoot());
    }

    @Override
    public void onBindViewHolder(TagsPartnersRecyclerAdapter.TagsPartnersHolder holder, int position) {
        holder.layoutTagsPartnersBinding.setTag(tagItems.get(position));
        if (position == previousPosition) {
            holder.layoutTagsPartnersBinding.imageViewPartners.setImageResource(tagItems.get(position).getIconIdCheck());
            tagItems.get(position).setChecked(true);
        } else {
            holder.layoutTagsPartnersBinding.imageViewPartners.setImageResource(tagItems.get(position).getIconIdUnCheck());
            tagItems.get(position).setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return tagItems.size();
    }

    public List<String> getSelected() {
        List<String> tags = new ArrayList<>();
        for (TagItem item : tagItems) if (item.isChecked()) tags.add(item.getTitle());
        return tags;
    }

    public TagItem getSelectedItem() {

        for (TagItem item : tagItems) if (item.isChecked()) {
            return item;
        }
           return null;
    }

    public class TagsPartnersHolder extends RecyclerView.ViewHolder {

        LayoutTagsPartnersBinding layoutTagsPartnersBinding;

        public TagsPartnersHolder(View itemView) {
            super(itemView);
            layoutTagsPartnersBinding = DataBindingUtil.bind(itemView);

            layoutTagsPartnersBinding.imageViewPartners.setOnClickListener(v -> {
                previousPosition = getAdapterPosition();
                notifyDataSetChanged();
            });
        }


    }
}
