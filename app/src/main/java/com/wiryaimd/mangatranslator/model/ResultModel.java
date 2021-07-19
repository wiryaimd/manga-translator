package com.wiryaimd.mangatranslator.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ResultModel implements Parcelable {

    private byte[] result;

    public ResultModel(byte[] result) {
        this.result = result;
    }

    protected ResultModel(Parcel in) {
        result = in.createByteArray();
    }

    public static final Creator<ResultModel> CREATOR = new Creator<ResultModel>() {
        @Override
        public ResultModel createFromParcel(Parcel in) {
            return new ResultModel(in);
        }

        @Override
        public ResultModel[] newArray(int size) {
            return new ResultModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByteArray(result);
    }
}
