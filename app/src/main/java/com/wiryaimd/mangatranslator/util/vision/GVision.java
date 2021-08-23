package com.wiryaimd.mangatranslator.util.vision;

import android.util.Log;

import com.google.gson.Gson;
import com.wiryaimd.mangatranslator.model.merge.MergeBlockModel;
import com.wiryaimd.mangatranslator.model.merge.MergeLineModel;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GVision {

    private static final String TAG = "GVision";

    private static GVision instance = null;

    private OkHttpClient client;
    private MediaType mediaType;

    private Gson gson;

    public interface Listener{
        void success(Iterator<MergeBlockModel> block);
    }

    public static GVision getInstance(){
        if (instance == null){
            instance = new GVision();
        }
        return instance;
    }

    public GVision(){
        client = new OkHttpClient();
        mediaType = MediaType.parse("application/json");

        gson = new Gson();
        requestDetectModel();
    }

    public void requestDetectModel(){

        OkHttpClient client = new OkHttpClient();

        String rawimg = "https://kumacdn.club/wp-content/uploads/S/Shonen%20no%20Abyss/Chapter%2061/003.jpg";

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create("{ \"source\": \"https://kumacdn.club/wp-content/uploads/S/Shonen%20no%20Abyss/Chapter%2061/003.jpg\", \"sourceType\": \"url\" }", mediaType);
        Request request = new Request.Builder()
                .url("https://google-ai-vision.p.rapidapi.com/cloudVision/imageToText")
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("x-rapidapi-host", "google-ai-vision.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "57389d3dd0msh2ba24f872d271adp1daa1fjsn7bd4eb658b63")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()){
                    Log.d(TAG, "onResponse: msCode: " + response.code() + " " + response.message());
                    return;
                }

                if (response.body() == null){
                    return;
                }

                String str = response.body().string(); // json string
                Log.d(TAG, "onResponse: gvision: " + str);
            }
        });

    }

}
