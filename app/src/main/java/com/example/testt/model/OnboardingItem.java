package com.example.testt.model;

import com.example.testt.R;

import com.example.testt.activity.*;
import com.example.testt.fragment.*;
import com.example.testt.adapter.*;
import com.example.testt.model.*;
import com.example.testt.helper.*;

public class OnboardingItem {
    private int iconResId;
    private int titleResId;
    private int descriptionResId;

    public OnboardingItem(int iconResId, int titleResId, int descriptionResId) {
        this.iconResId = iconResId;
        this.titleResId = titleResId;
        this.descriptionResId = descriptionResId;
    }

    public int getIconResId() {
        return iconResId;
    }

    public int getTitleResId() {
        return titleResId;
    }

    public int getDescriptionResId() {
        return descriptionResId;
    }
}
