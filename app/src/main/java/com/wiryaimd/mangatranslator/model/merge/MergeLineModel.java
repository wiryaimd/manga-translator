package com.wiryaimd.mangatranslator.model.merge;

import android.graphics.Rect;

public class MergeLineModel {

    private String text;
    private Rect rect;

    public MergeLineModel(String text, Rect rect) {
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
