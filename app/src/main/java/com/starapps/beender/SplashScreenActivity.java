package com.starapps.beender;

import android.content.Intent;

import com.starapps.beender.nav.MainActivity;
import com.starapps.beender.utils.BlackStatusActivity;
import com.starapps.beender.utils.Prefs;



public class SplashScreenActivity extends BlackStatusActivity {

    @Override
    protected void onResume() {
        super.onResume();
        if (Prefs.getInstance(this).isLogged()) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            startActivity(new Intent(this, IntroActivity.class));
        }
        finish();
    }
}
