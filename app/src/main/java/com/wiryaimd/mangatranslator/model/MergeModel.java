package com.wiryaimd.mangatranslator.model;

import android.graphics.Rect;

public class MergeModel {

    private String text;
    private Rect rect;

    public MergeModel(String text, Rect rect) {
        this.text = text;
        this.rect = rect;
    }

    public String getText() {
        return text;
    }

    public Rect getRect() {
        return rect;
    }
}
