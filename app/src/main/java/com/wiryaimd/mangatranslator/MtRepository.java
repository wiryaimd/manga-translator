package com.wiryaimd.mangatranslator;

import android.util.Log;

import com.google.gson.Gson;
import com.wiryaimd.mangatranslator.api.ApiEndpoint;
import com.wiryaimd.mangatranslator.api.model.DetectModel;
import com.wiryaimd.mangatranslator.util.Const;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MtRepository {

    private static final String TAG = "MtRepository";

    private ApiEndpoint apiEndpoint;

    private OkHttpClient client;
    private MediaType mediaType;

    private Gson gson;

    public MtRepository(){
        client = new OkHttpClient();
        mediaType = MediaType.parse("application/json");

        gson = new Gson();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Const.BASE_URL_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiEndpoint = retrofit.create(ApiEndpoint.class);
    }

    public ApiEndpoint getApiEndpoint() {
        return apiEndpoint;
    }

    public void requestTranslate(){

    }

    public void requestDetectModel(String img, String options){
        RequestBody body = RequestBody.create("{\r\"url\": \"" + img + "\"\r }", mediaType);

        Request request = new Request.Builder()
                .url("https://microsoft-computer-vision3.p.rapidapi.com/ocr?" + options)
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("x-rapidapi-key", Const.RAPID_API_KEY)
                .addHeader("x-rapidapi-host", Const.RAPID_API_HOST)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()){
                    Log.d(TAG, "onResponse: ngapasich: " + response.code() + " " + response.message());
                    return;
                }

                if (response.body() == null){
                    return;
                }

                ResponseBody responseBody = response.body();
                String str = responseBody.string();
                Log.d(TAG, "onResponse: str: " + str);

                Log.d(TAG, "onResponse: response: " + response.body().toString());
            }
        });

    }

    public DetectModel toDetectModel(String json){
        return gson.fromJson(json, DetectModel.class);
    }
}
