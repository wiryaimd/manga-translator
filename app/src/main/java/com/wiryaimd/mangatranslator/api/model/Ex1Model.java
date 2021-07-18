package com.wiryaimd.mangatranslator.api.model;

import com.google.gson.annotations.SerializedName;

public class Ex1Model {

    private int postId;

    @SerializedName("email")
    private String email;

    public Ex1Model(int postId, String email) {
        this.postId = postId;
        this.email = email;
    }

    public int getPostId() {
        return postId;
    }

    public String getEmail() {
        return email;
    }
}
