package com.starapps.beender.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.starapps.beender.R;



public class RadioImageView extends AppCompatImageView {

    private int icon;
    private boolean isChecked;
    private boolean isRadio;
    private OnCheckedChangeListener onCheckedChangeListener;

    public RadioImageView(Context context) {
        super(context);
        init(context, null);
    }

    public RadioImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RadioImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CheckableImageView, 0, 0);
            try {
                icon = a.getResourceId(R.styleable.CheckableImageView_civ_image, 0);
            } catch (Exception ignored) {
            } finally {
                a.recycle();
            }
        }
        updateIcon();
        setOnClickListener(v -> {
            if (isRadio && !isChecked) {
                setChecked(true);
            }
        });
    }

    public void setRadio(boolean radio) {
        isRadio = radio;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
        updateIcon();
        notifyChanges();
    }

    private void notifyChanges() {
        if (onCheckedChangeListener != null) {
            onCheckedChangeListener.onCheckedChanged(this, isChecked);
        }
    }

    private void updateIcon() {
        if (isChecked()) {
            setBackgroundResource(R.drawable.rectangle_grey_filled);
        } else {
            setBackgroundResource(R.drawable.rectangle_grey_line);
        }
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public OnCheckedChangeListener getOnCheckedChangeListener() {
        return onCheckedChangeListener;
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(RadioImageView imageView, boolean isChecked);
    }

    public static class CheckControl implements RadioImageView.OnCheckedChangeListener {

        private RadioImageView mLastChecked;
        private OnTabSelectedListener mTabListener;

        public CheckControl(RadioImageView... imageViews) {
            for (RadioImageView iv : imageViews) {
                iv.setOnCheckedChangeListener(this);
                iv.setRadio(true);
                if (iv.isChecked()) {
                    mLastChecked = iv;
                }
            }
        }

        public void setTabListener(OnTabSelectedListener mTabListener) {
            this.mTabListener = mTabListener;
        }

        @Override
        public void onCheckedChanged(RadioImageView imageView, boolean isChecked) {
            if (isChecked) {
                if (mLastChecked != null && mLastChecked != imageView) {
                    mLastChecked.setChecked(false);
                    if (mTabListener != null) {
                        mTabListener.onTabSelect(imageView);
                    }
                }
                mLastChecked = imageView;
            }
        }

        public int getCheckedId() {
            if (mLastChecked != null) {
                return mLastChecked.getId();
            }
            return -1;
        }
    }

    public interface OnTabSelectedListener {
        void onTabSelect(RadioImageView checkableImageView);
    }
}
