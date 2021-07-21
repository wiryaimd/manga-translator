package com.wiryaimd.mangatranslator.model.merge;

import android.graphics.Rect;

import java.util.List;

public class MergeBlockModel {

    private String text;
    private Rect boundingBox;
    private List<MergeLineModel> lineList;

    public MergeBlockModel(String text, Rect boundingBox, List<MergeLineModel> lineList) {
        this.text = text;
        this.boundingBox = boundingBox;
        this.lineList = lineList;
    }

    public String getText() {
        return text;
    }

    public Rect getBoundingBox() {
        return boundingBox;
    }

    public List<MergeLineModel> getLineList() {
        return lineList;
    }
}
