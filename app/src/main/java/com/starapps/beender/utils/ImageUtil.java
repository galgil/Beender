package com.starapps.beender.utils;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.starapps.beender.R;

import jp.wasabeef.picasso.transformations.BlurTransformation;



public class ImageUtil {

    @ColorRes
    public static int getBorderColor(String category) {
        if (category == null) {
            return R.color.colorPinGreen;
        }
        category = category.toLowerCase();
        if (contains(category, "cafe", "café", "coffee")) {
            return R.color.colorPinBlue;
        } else if (contains(category, "restaurant", "dinner", "pizza", "bistro", "buffet")) {
            return R.color.colorPinPink;
        } else if (contains(category, "bar", "dinner", "pub", "lounge", "nightclub")) {
            return R.color.colorPinPurple;
        } else if (contains(category, "gym", "soccer", "field", "park")) {
            return R.color.colorPinOrange;
        } else {
            return R.color.colorPinGreen;
        }
    }

    @DrawableRes
    public static int getPinIcon(String category) {
        if (category == null) {
            return R.drawable.pin_lemon_places;
        }
        category = category.toLowerCase();
        if (contains(category, "cafe", "café", "coffee")) {
            return R.drawable.pin_blue_coffee;
        } else if (contains(category, "restaurant", "dinner", "pizza", "bistro", "buffet")) {
            return R.drawable.pin_pink_food;
        } else if (contains(category, "bar", "dinner", "pub", "lounge", "nightclub")) {
            return R.drawable.pin_purple_nightlife;
        } else if (contains(category, "gym", "soccer", "field", "park")) {
            return R.drawable.pin_orange_activity;
        } else {
            return R.drawable.pin_lemon_places;
        }
    }

    private static boolean contains(String key, String... where) {
        for (String w : where) {
            if (key.contains(w)) {
                return true;
            }
        }
        return false;
    }

    public static void loadCenterCropImage(int resId, final ImageView imageView, boolean resizeToView, boolean blur) {
        if (resizeToView) {
            imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    if (imageView.getViewTreeObserver().isAlive()) {
                        imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    int width = imageView.getMeasuredWidth();
                    int height = imageView.getMeasuredHeight();
                    if (width <= 0 && height <= 0) return false;
                    if (blur) {
                        Picasso.with(imageView.getContext())
                                .load(resId)
                                .resize(width, height)
                                .centerCrop()
                                .transform(new BlurTransformation(imageView.getContext(), 20, 2))
                                .into(imageView);
                    } else {
                        Picasso.with(imageView.getContext())
                                .load(resId)
                                .resize(width, height)
                                .centerCrop()
                                .into(imageView);
                    }
                    return true;
                }
            });
        } else {
            if (blur) {
                Picasso.with(imageView.getContext())
                        .load(resId)
                        .centerCrop()
                        .transform(new BlurTransformation(imageView.getContext(), 20, 2))
                        .into(imageView);
            } else {
                Picasso.with(imageView.getContext())
                        .load(resId)
                        .centerCrop()
                        .into(imageView);
            }
        }
    }

    public static void loadImage(int resId, final ImageView imageView, boolean resizeToView, boolean blur) {
        if (resizeToView) {
            imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    if (imageView.getViewTreeObserver().isAlive()) {
                        imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    int width = imageView.getMeasuredWidth();
                    int height = imageView.getMeasuredHeight();
                    if (width <= 0 && height <= 0) return false;
                    if (blur) {
                        Picasso.with(imageView.getContext())
                                .load(resId)
                                .resize(width, height)
                                .transform(new BlurTransformation(imageView.getContext(), 20, 2))
                                .into(imageView);
                    } else {
                        Picasso.with(imageView.getContext())
                                .load(resId)
                                .resize(width, height)
                                .into(imageView);
                    }
                    return true;
                }
            });
        } else {
            if (blur) {
                Picasso.with(imageView.getContext())
                        .load(resId)
                        .transform(new BlurTransformation(imageView.getContext(), 20, 2))
                        .into(imageView);
            } else {
                Picasso.with(imageView.getContext())
                        .load(resId)
                        .into(imageView);
            }
        }
    }
}
