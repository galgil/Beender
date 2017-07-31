package com.starapps.beender.nav.access;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.starapps.beender.R;
import com.starapps.beender.databinding.FragmentMemoryPermissionBinding;
import com.starapps.beender.nav.NestedFragment;
import com.starapps.beender.utils.ImageUtil;



public class MemoryAccessFragment extends NestedFragment {

    public static final String TAG = "MemoryAccessFragment";
    private static final int PERM_LOCAL = 254;

    private FragmentMemoryPermissionBinding binding;
    private boolean isDenied;

    public static Fragment newInstance() {
        return new MemoryAccessFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMemoryPermissionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageUtil.loadImage(R.drawable.access_photo, binding.image, true, true);

        binding.allowButton.setOnClickListener(v -> askPermission());
        binding.denyButton.setOnClickListener(v -> showExplanation());
    }

    private void askPermission() {
        requestPermissions(new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, PERM_LOCAL);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length == 0) {
            return;
        }
        switch (requestCode) {
            case PERM_LOCAL:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getInterface().moveBack();
                } else {
                    showExplanation();
                }
                break;
        }
    }

    private void showExplanation() {
        if (isDenied) {
            getInterface().moveBack();
            return;
        }
        isDenied = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.messageView.setText(Html.fromHtml(getString(R.string.access_explanation), Html.FROM_HTML_MODE_LEGACY));
        } else {
            binding.messageView.setText(Html.fromHtml(getString(R.string.access_explanation)));
        }
    }
}
