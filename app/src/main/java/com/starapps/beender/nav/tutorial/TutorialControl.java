package com.starapps.beender.nav.tutorial;

import android.content.Context;
import android.view.LayoutInflater;

import com.starapps.beender.databinding.PopupTutorialBinding;

import java.util.ArrayList;
import java.util.List;

import io.github.douglasjunior.androidSimpleTooltip.OverlayView;
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;



public class TutorialControl implements SimpleTooltip.OnDismissListener {

    private Context mContext;
    private SimpleTooltip mPopup;
    private List<Tutorial> tutorials = new ArrayList<>();
    private int mCurrentPosition = 0;

    public TutorialControl(Context mContext, List<Tutorial> tutorials) {
        this.mContext = mContext;
        this.tutorials = tutorials;
        if (!this.tutorials.isEmpty()) {
            this.showPopup(this.tutorials.get(mCurrentPosition));
        }
    }

    private void showPopup(Tutorial tutorial) {
        PopupTutorialBinding binding = PopupTutorialBinding.inflate(LayoutInflater.from(mContext));

        binding.popupName.setText(tutorial.getTitle());
        binding.description.setText(tutorial.getDescription());
        binding.okButton.setText(tutorial.getButtonText());

        binding.okButton.setOnClickListener(v1 -> moveForward());
        mPopup = new SimpleTooltip.Builder(mContext)
                .anchorView(tutorial.getAnchor())
                .contentView(binding.getRoot(), 0)
                .gravity(tutorial.getGravity())
                .animated(false)
                .padding(0f)
                .margin(0f)
                .transparentOverlay(false)
                .highlightShape(OverlayView.HIGHLIGHT_SHAPE_RECTANGULAR)
                .overlayOffset(0)
                .showArrow(false)
                .dismissOnInsideTouch(false)
                .dismissOnOutsideTouch(false)
                .onDismissListener(this)
                .build();
        mPopup.show();
    }

    private void moveForward() {
        hidePopup();
        if (mCurrentPosition == tutorials.size() - 1) {
            return;
        }
        mCurrentPosition++;
        showPopup(tutorials.get(mCurrentPosition));
    }

    public boolean hidePopup() {
        if (mPopup != null && mPopup.isShowing()) {
            mPopup.dismiss();
            return false;
        }
        return true;
    }

    @Override
    public void onDismiss(SimpleTooltip tooltip) {

    }

    public boolean isShowing() {
        return mPopup != null && mPopup.isShowing();
    }
}
