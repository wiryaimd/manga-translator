package com.wiryaimd.mangatranslator.util.translator;

import android.util.Log;

import com.google.gson.Gson;
import com.wiryaimd.mangatranslator.model.TranslateModel;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MSTranslate {

    private static final String TAG = "MSTranslate";

    private OkHttpClient client;
    private Gson gson;

    private MediaType mediaType;

    public interface Listener{
        void complete(String translated, String source);
        void fail(String msg);
    }

    public MSTranslate(){
        client = new OkHttpClient();
        gson = new Gson();

        mediaType = MediaType.parse("application/json");
    }

    public void translateText(String text, String langFrom, String langTo, String key, String host, MSTranslate.Listener listener){
        RequestBody body = RequestBody.create("[{\"Text\": \"" + text.replace("\\", "") + "\"}]", mediaType);

//        HttpUrl url = new HttpUrl.Builder()
//                .scheme("https")
//                .host("microsoft-translator-text.p.rapidapi.com")
//                .addPathSegment("/translate")
//                .addQueryParameter("api-version", "3.0")
//                .addQueryParameter("to", langTo)
//                .addQueryParameter("from", langFrom)
//                .addQueryParameter("textType", "plain")
//                .build();

        Request request = new Request.Builder().url("https://microsoft-translator-text.p.rapidapi.com/translate?api-version=3.0&from=" + langFrom + "&to=" + langTo + "&textType=plain").post(body)
                .addHeader("Content-type", "application/json")
                .addHeader("x-rapidapi-host", "microsoft-translator-text.p.rapidapi.com")
                .addHeader("x-rapidapi-key", key)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                listener.fail("");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()){
                    Log.d(TAG, "onResponse: msCode: " + response.code() + " " + response.message());
                    listener.fail(response.message());
                    return;
                }

                if (response.body() == null){
                    listener.fail("null");
                    return;
                }

                String result = response.body().string();
                TranslateModel[] translateList = gson.fromJson(result, TranslateModel[].class);

                StringBuilder sb = new StringBuilder();
                for(TranslateModel tlModel : translateList){
                    for (TranslateModel.Translation translation : tlModel.getTranslations()){
                        Log.d(TAG, "onResponse: translation mstl: " + translation.getText());
                        sb.append(translation.getText()).append(" ");
                    }
                }

                listener.complete(sb.toString(), text);

            }
        });
    }

}
