package com.starapps.beender.nav.trip_preview.tags;


import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.starapps.beender.databinding.LayoutTagsCheckedSpecBinding;

import java.util.ArrayList;
import java.util.List;

public class TagsCheckedRecyclerAdapter extends RecyclerView.Adapter<TagsCheckedRecyclerAdapter.TagsCheckedSpecHolder> {

    private List<TagItem> tagItems = new ArrayList<>();

    public TagsCheckedRecyclerAdapter(List<TagItem> tagItems) {
        this.tagItems = tagItems;
    }


    @Override
    public TagsCheckedSpecHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TagsCheckedSpecHolder(LayoutTagsCheckedSpecBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false).getRoot());
    }

    @Override
    public void onBindViewHolder(TagsCheckedSpecHolder holder, int position) {
        holder.layoutTagsCheckedSpecBinding.setTag(tagItems.get(position));

    }

    @Override
    public int getItemCount() {
        return tagItems.size();
    }


    public class TagsCheckedSpecHolder extends RecyclerView.ViewHolder {

        LayoutTagsCheckedSpecBinding layoutTagsCheckedSpecBinding;

        public TagsCheckedSpecHolder(View itemView) {
            super(itemView);
            layoutTagsCheckedSpecBinding = DataBindingUtil.bind(itemView);
        }


    }
}

