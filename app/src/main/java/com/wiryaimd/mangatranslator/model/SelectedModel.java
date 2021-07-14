package com.wiryaimd.mangatranslator.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class SelectedModel implements Parcelable {

    private String name;
    private Uri uri;
    private Type type;

    protected SelectedModel(Parcel in) {
        name = in.readString();
        uri = in.readParcelable(Uri.class.getClassLoader());
        type = (Type) in.readValue(Type.class.getClassLoader());
    }

    public static final Creator<SelectedModel> CREATOR = new Creator<SelectedModel>() {
        @Override
        public SelectedModel createFromParcel(Parcel in) {
            return new SelectedModel(in);
        }

        @Override
        public SelectedModel[] newArray(int size) {
            return new SelectedModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeParcelable(uri, 0);
        parcel.writeValue(type);
    }

    public SelectedModel(String name, Uri uri, Type type) {
        this.name = name;
        this.uri = uri;
        this.type = type;
    }

    public enum Type {
        IMAGE, PDF;
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
