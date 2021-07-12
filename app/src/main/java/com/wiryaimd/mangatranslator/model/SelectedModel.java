package com.wiryaimd.mangatranslator.model;

import android.net.Uri;

public class SelectedModel {

    private String name;
    private Uri uri;
    private Type type;

    public enum Type {
        IMAGE, PDF;
    }

    public SelectedModel(String name, Uri uri, Type type) {
        this.name = name;
        this.uri = uri;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Uri getUri() {
        return uri;
    }

    public Type getType() {
        return type;
    }
}
