package com.starapps.beender.nav.trip_preview.tags;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.starapps.beender.databinding.ItemTagBinding;
import com.starapps.beender.views.FlatCheckBox;

import java.util.ArrayList;
import java.util.List;



public class TagsSpecificallyRecyclerAdapter extends RecyclerView.Adapter<TagsSpecificallyRecyclerAdapter.TagHolder> {

    private List<TagItem> tagItems = new ArrayList<>();
    private int count;


    public TagsSpecificallyRecyclerAdapter() {
            this.tagItems = TagItem.Factory.getSpecifically();

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
    public TagHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TagHolder(ItemTagBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false).getRoot());
    }

    @Override
    public void onBindViewHolder(TagHolder holder, int position) {
        holder.binding.setTag(tagItems.get(position));
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

    public List<TagItem> getSelectedItems() {
        List<TagItem> tags = new ArrayList<>();
        for (TagItem item : tagItems) if (item.isChecked()) tags.add(item);
        return tags;
    }

    class TagHolder extends RecyclerView.ViewHolder {

        ItemTagBinding binding;

        public TagHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);

            binding.tagCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
                updateCheck(isChecked, getAdapterPosition(), binding.tagCheck);
            });
        }
    }

    private void updateCheck(boolean isChecked, int position, FlatCheckBox checkBox) {

            if (count < 4) {
                tagItems.get(position).setChecked(isChecked);
                checkBox.setChecked(isChecked);
            } else if (isChecked) {
                tagItems.get(position).setChecked(false);
                checkBox.setChecked(false);
                count--;
            }
            if (isChecked) {
                count++;
            } else
                count--;


    }
}
