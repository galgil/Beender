package com.starapps.beender;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.starapps.beender.databinding.ActivityIntroBinding;
import com.starapps.beender.login.LoginActivity;
import com.starapps.beender.utils.BlackStatusActivity;
import com.starapps.beender.utils.ImageUtil;

public class IntroActivity extends BlackStatusActivity {

    private ActivityIntroBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro);
        initStatusBar(binding.imageView);

        ImageUtil.loadCenterCropImage(R.drawable.waterfall_photo, binding.imageView, true, true);
        binding.nextButton.setOnClickListener(view -> goToNext());
    }

    private void goToNext() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
