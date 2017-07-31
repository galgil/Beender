package com.starapps.beender.views;

import android.content.Context;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;

import com.starapps.beender.R;



public class FlatRadioButton extends AppCompatRadioButton {
    public FlatRadioButton(Context context) {
        super(context);
        init(context, null);
    }

    public FlatRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FlatRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setButtonDrawable(null);
        updateView();
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        updateView();
    }

    private void updateView() {
        if (isChecked()) {
            setTextColor(getResources().getColor(R.color.colorWhite));
            setBackgroundResource(R.drawable.rectangle_accent_filled);
        } else {
            setTextColor(getResources().getColor(R.color.colorAccent));
            setBackgroundResource(R.drawable.rectangle_accent_line);
        }
    }
}
