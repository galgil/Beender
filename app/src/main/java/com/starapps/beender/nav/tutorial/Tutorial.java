package com.starapps.beender.nav.tutorial;

import android.view.View;



public class Tutorial {

    private View anchor;
    private String title;
    private String description;
    private String buttonText;
    private int gravity;

    public Tutorial(View anchor, String title, String description, String buttonText, int gravity) {
        this.anchor = anchor;
        this.title = title;
        this.description = description;
        this.buttonText = buttonText;
        this.gravity = gravity;
    }

    public int getGravity() {
        return gravity;
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    public View getAnchor() {
        return anchor;
    }

    public void setAnchor(View anchor) {
        this.anchor = anchor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }
}
