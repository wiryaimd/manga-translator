package com.wiryaimd.mangatranslator.util.translator;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GApiTranslate {

    private static final String TAG = "GApiTranslate";

    private static GApiTranslate instance = null;

    private OkHttpClient client;
    private Gson gson;

    public interface Listener{
        void complete(String translated, String source);
        void fail(String msg);
    }

    public static GApiTranslate getInstance(){
        if (instance == null){
            instance = new GApiTranslate();
        }
        return instance;
    }

    public GApiTranslate() {

        client = new OkHttpClient();
        gson = new Gson();

    }

    public void translateText(String text, String langFrom, String langTo, Listener listener){
        Request request = new Request.Builder()
                .url("https://translate.googleapis.com/translate_a/single?client=gtx&sl=" + langFrom + "&tl=" + langTo +"&dt=t&q=" + text)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()){
                    Log.d(TAG, "onResponse: api Code: " + response.code() + " " + response.message());
                    if (response.code() == 429){ // your ip blocked from google translate, use on device translate
                        listener.fail("Your ip blocked from google translate, use on device translate");
                    }else{
                        listener.fail(response.message());
                    }
                    return;
                }
                String result = response.body().string();

                StringBuilder sb = new StringBuilder();
                try {
                    JSONArray r1 = new JSONArray(result);
                    for (int i = 0; i < r1.length(); i++) {
                        if (r1.getJSONArray(i) == null){
                            break;
                        }

                        JSONArray r2 = r1.getJSONArray(i);
                        for (int j = 0; j < r2.length(); j++) {
                            JSONArray r3 = r2.getJSONArray(j);
                            String str = r3.getString(0);
                            Log.d(TAG, "onResponse: str result : " + str);
                            sb.append(str);
                        }
                    }
                }catch (JSONException jsonException){
                    jsonException.printStackTrace();
                }

                Log.d(TAG, "onResponse: real result: " + sb.toString());
                listener.complete(sb.toString(), text);

            }
        });
    }
}
