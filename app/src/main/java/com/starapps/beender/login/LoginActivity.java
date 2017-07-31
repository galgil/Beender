package com.starapps.beender.login;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;

import com.starapps.beender.IntroActivity;
import com.starapps.beender.R;
import com.starapps.beender.databinding.ActivityLoginBinding;
import com.starapps.beender.nav.MainActivity;
import com.starapps.beender.utils.BlackStatusActivity;
import com.starapps.beender.utils.ImageUtil;
import com.starapps.beender.utils.Prefs;



public class LoginActivity extends BlackStatusActivity implements FbLogin.LoginCallback {

    private FbLogin mFbLogin;
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFbLogin = new FbLogin(this, this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        initStatusBar(binding.imageView);

        initTermsView();

        ImageUtil.loadCenterCropImage(R.drawable.login_photo, binding.imageView, true, true);
        binding.loginButton.setOnClickListener(view -> mFbLogin.login());
        binding.backButton.setOnClickListener(v -> moveBack());
    }

    private void initTermsView() {
        Spannable s = (Spannable) Html.fromHtml(getString(R.string.terms_message));
        for (URLSpan u: s.getSpans(0, s.length(), URLSpan.class)) {
            s.setSpan(new UnderlineSpan() {
                public void updateDrawState(TextPaint tp) {
                    tp.setUnderlineText(false);
                }
            }, s.getSpanStart(u), s.getSpanEnd(u), 0);
        }
        binding.termsView.setText(s);
    }

    private void moveBack() {
        startActivity(new Intent(this, IntroActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        moveBack();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFbLogin.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSuccess() {
        Prefs.getInstance(this).setLogged(true);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onFail() {
        Prefs.getInstance(this).setLogged(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFbLogin.onDestroy();
    }
}
