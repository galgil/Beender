package com.starapps.beender.nav.trip_preview.tags;

import com.starapps.beender.R;

import java.util.ArrayList;
import java.util.List;



public class TagItem {

    private String title;
    private boolean isChecked;
    private int iconIdUnCheck;
    private int iconIdCheck;


    public TagItem(String title) {
        this.title = title;
    }

    public TagItem(String title, int iconIdUnCheck, int iconIdCheck) {
        this.title = title;
        this.iconIdUnCheck = iconIdUnCheck;
        this.iconIdCheck = iconIdCheck;
    }


    public int getIconIdCheck() {
        return iconIdCheck;
    }

    public void setIconIdCheck(int iconIdCheck) {
        this.iconIdCheck = iconIdCheck;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getIconIdUnCheck() {
        return iconIdUnCheck;
    }

    public void setIconIdUnCheck(int iconIdUnCheck) {
        this.iconIdUnCheck = iconIdUnCheck;
    }

    public static class Factory {
        public static List<TagItem> get() {
            List<TagItem> tagItems = new ArrayList<>();
            tagItems.add(new TagItem("Me, Myself & I", R.drawable.single_unfilled, R.drawable.single_filled));
            tagItems.add(new TagItem("My Love", R.drawable.couple_unfilled, R.drawable.couple_filled));
            tagItems.add(new TagItem("My Gang", R.drawable.group_unfilled, R.drawable.friends_filled));
            tagItems.add(new TagItem("La Familia", R.drawable.family_unfilled, R.drawable.family_filled));
            return tagItems;
        }

        public static List<TagItem> getSpecifically() {
            List<TagItem> tagItems = new ArrayList<>();
            tagItems.add(new TagItem("Leisure"));
            tagItems.add(new TagItem("Foodie"));
            tagItems.add(new TagItem("Relax"));
            tagItems.add(new TagItem("Nature"));
            tagItems.add(new TagItem("Shopping"));
            tagItems.add(new TagItem("Art & Culture"));
            tagItems.add(new TagItem("Bars"));
            tagItems.add(new TagItem("Beach"));
            tagItems.add(new TagItem("Drinks n' Parties"));
            tagItems.add(new TagItem("Live show & Festival"));
            tagItems.add(new TagItem("Spiritual"));
            tagItems.add(new TagItem("Business"));
            tagItems.add(new TagItem("Yacht"));
            tagItems.add(new TagItem("Glamping"));
            tagItems.add(new TagItem("Sustainable"));
            tagItems.add(new TagItem("Snowboard"));
            tagItems.add(new TagItem("Honeymoon"));
            tagItems.add(new TagItem("Spa"));
            tagItems.add(new TagItem("Advantures"));
            tagItems.add(new TagItem("My Island"));
            tagItems.add(new TagItem("Camping"));
            tagItems.add(new TagItem("Ski"));
            tagItems.add(new TagItem("Surfing"));
            tagItems.add(new TagItem("Hiking"));
            tagItems.add(new TagItem("Diving"));
            return tagItems;
        }
        public static List<TagItem> getWeather() {
            List<TagItem> tagItems = new ArrayList<>();
            tagItems.add(new TagItem("Hot As Hell"));
            tagItems.add(new TagItem("Perfect"));
            tagItems.add(new TagItem("Under My Umbrella"));
            tagItems.add(new TagItem("Freezing"));
            tagItems.add(new TagItem("50 Shades of Gray"));
            tagItems.add(new TagItem("Tropic"));
            return tagItems;
        }
    }
}
