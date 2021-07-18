package com.wiryaimd.mangatranslator.api.model;

import com.google.gson.annotations.SerializedName;

public class DetectModel {

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("body")
    private String text;

    public DetectModel( String title, String text) {
        this.title = title;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }
}
