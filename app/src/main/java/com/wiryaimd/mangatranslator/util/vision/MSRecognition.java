package com.wiryaimd.mangatranslator.util.vision;

import android.graphics.Rect;
import android.util.Log;

import com.google.gson.Gson;
import com.wiryaimd.mangatranslator.api.ApiEndpoint;
import com.wiryaimd.mangatranslator.api.model.DetectModel;
import com.wiryaimd.mangatranslator.model.merge.MergeLineModel;
import com.wiryaimd.mangatranslator.util.Const;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

public class MSRecognition {

    private static final String TAG = "MtRepository";

    private static MSRecognition instance = null;

    private OkHttpClient client;
    private MediaType mediaType;

    private Gson gson;

    public interface Listener{
        void success(Response response);
        List<MergeLineModel> mergeNormal(List<MergeLineModel> mergeList, MergeLineModel mergeLineModel);
    }

    public static MSRecognition getInstance(){
        if (instance == null){
            instance = new MSRecognition();
        }
        return instance;
    }

    public MSRecognition(){
        client = new OkHttpClient();
        mediaType = MediaType.parse("application/json");

        gson = new Gson();
    }

    public void requestDetectModel(String img, String options, Listener listener){
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
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                if (!response.isSuccessful()){
                    Log.d(TAG, "onResponse: ngapasich: " + response.code() + " " + response.message());
                    return;
                }

                if (response.body() == null){
                    return;
                }

                try {
                    String str = response.body().string(); // json string
                    Log.d(TAG, "success: json: " + str);

                    // convert json to object class
                    DetectModel detectModel = toDetectModel(str);

                    List<MergeLineModel> mergeLineList = new ArrayList<>();
                    for (DetectModel.Regions region : detectModel.getRegions()){
                        Log.d(TAG, "success: region");
                        for (DetectModel.Lines line : region.getLines()){
                            Log.d(TAG, "success: line");

                            StringBuilder tempLine = new StringBuilder();
                            String[] pos = line.getBoundingBox().split(",");
                            int left = Integer.parseInt(pos[0]), top = Integer.parseInt(pos[1]), right = Integer.parseInt(pos[2]), bottom = Integer.parseInt(pos[3]);
                            Rect rect = new Rect(left, top, right, bottom);

                            for(DetectModel.Words word : line.getWords()){
                                Log.d(TAG, "success: ms word: " + word.getText());
                                tempLine.append(word);
                            }

                            mergeLineList.add(new MergeLineModel(tempLine.toString(), rect));
                        }
                    }

                    if (detectModel.getLang().equalsIgnoreCase("ja")){
                        mergeJapan();
                    }else{

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                listener.success(response);
            }
        });

    }

    public void mergeJapan(){

    }

    public DetectModel toDetectModel(String json){
        return gson.fromJson(json, DetectModel.class);
    }
}
