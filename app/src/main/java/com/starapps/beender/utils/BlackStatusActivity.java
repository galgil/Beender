package com.starapps.beender.utils;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.view.View;



public abstract class BlackStatusActivity extends AppCompatActivity {

    protected void initStatusBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
}
