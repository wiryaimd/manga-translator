package com.wiryaimd.mangatranslator.util.vision;

import android.graphics.Rect;
import android.util.Log;

import com.google.gson.Gson;
import com.wiryaimd.mangatranslator.api.ApiEndpoint;
import com.wiryaimd.mangatranslator.api.model.DetectModel;
import com.wiryaimd.mangatranslator.model.merge.MergeBlockModel;
import com.wiryaimd.mangatranslator.model.merge.MergeLineModel;
import com.wiryaimd.mangatranslator.util.Const;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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
        void success(Iterator<MergeBlockModel> block);
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
                            right = right + left;
                            bottom = bottom + top;
                            Log.d(TAG, "onResponse: left: " + left + " top: " + top + " bottom: " + bottom + " right: " + right);
                            Rect rect = new Rect(left, top, right, bottom);

                            for(DetectModel.Words word : line.getWords()){
                                Log.d(TAG, "success: ms word: " + word.getText());
                                tempLine.append(word.getText());
                            }

                            mergeLineList.add(new MergeLineModel(tempLine.toString(), rect));
                        }
                    }

                    List<List<MergeLineModel>> mergeBlock = new ArrayList<>();
                    Log.d(TAG, "onResponse: lang detect: " + detectModel.getLang());
                    if (detectModel.getLang().equalsIgnoreCase("ja")){
                        for (int i = 0; i < mergeLineList.size();) {
                            List<MergeLineModel> result = mergeJapan(mergeLineList, mergeLineList.get(i));
                            mergeBlock.add(result);
                            mergeLineList.removeAll(result);
                        }
                    }else{
                        for (int i = 0; i < mergeLineList.size();) {
                            List<MergeLineModel> result = listener.mergeNormal(mergeLineList, mergeLineList.get(i));
                            mergeBlock.add(result);
                            mergeLineList.removeAll(result);
                        }
                    }

                    List<MergeBlockModel> blockList = new ArrayList<>();
                    for(List<MergeLineModel> block : mergeBlock){
                        Log.d(TAG, "onComplete: block crot");
                        StringBuilder sb = new StringBuilder();
                        int left = Integer.MAX_VALUE, top = Integer.MAX_VALUE, bottom = 0, right = 0;
                        for (MergeLineModel line : block){
                            if (line.getRect().left < left){
                                left = line.getRect().left;
                            }
                            if (line.getRect().top < top){
                                top = line.getRect().top;
                            }
                            if (line.getRect().bottom > bottom){
                                bottom = line.getRect().bottom;
                            }
                            if (line.getRect().right > right){
                                right = line.getRect().right;
                            }
                            Log.d(TAG, "onComplete: text second: " + line.getText());
                            sb.append(line.getText()).append(" ");
                        }
                        blockList.add(new MergeBlockModel(sb.toString(), new Rect(left, top, right, bottom), block));
                        Log.d(TAG, "onComplete: ");
                    }

                    listener.success(blockList.iterator());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public List<MergeLineModel> mergeJapan(List<MergeLineModel> mergeList, MergeLineModel mergeLineModel){

        List<MergeLineModel> blockList = new ArrayList<>();
        MergeLineModel mergeHead = mergeLineModel;
        MergeLineModel mergeHead2 = mergeLineModel;

        blockList.add(mergeHead);
        for (int i = 0; i < mergeList.size(); i++) {
            float spaceHeightL = (mergeHead.getRect().right - mergeHead.getRect().left);

            boolean isAvailableBottom = false;
            sLoop: for (int j = 0; j < blockList.size(); j++) {
                if (blockList.get(j) != mergeList.get(i)){
                    isAvailableBottom = true;
                }else{
                    isAvailableBottom = false;
                    break sLoop;
                }
            }

            if (isAvailableBottom) {
                float res = mergeHead.getRect().left - mergeList.get(i).getRect().right;
                float mid = (float) mergeHead.getRect().centerY() / 2;
                if (res > 0 - spaceHeightL &&
                        res <= spaceHeightL &&
                        mergeList.get(i).getRect().top < (mergeHead.getRect().top + mid) &&
                        mergeList.get(i).getRect().bottom > mergeHead.getRect().top) {
                    Log.d(TAG, "mergeJapan: left available");
                    blockList.add(mergeList.get(i));
                    mergeHead = mergeList.get(i);
                    i = 0;
                }
            }
        }

        for (int i = 0; i < mergeList.size(); i++) {
            float spaceHeightR = mergeHead2.getRect().right - mergeHead2.getRect().left;
            boolean isAvailableTop = false;

            tLoop: for (int j = 0; j < blockList.size(); j++) {
                if (blockList.get(j) != mergeList.get(i)){
                    isAvailableTop = true;
                }else{
                    isAvailableTop = false;
                    break tLoop;
                }
            }

            if (isAvailableTop) {
                float res = mergeList.get(i).getRect().left - mergeHead2.getRect().right;
                float mid = (float) mergeHead2.getRect().centerY() / 2;
                if (res > 0 - spaceHeightR &&
                        res <= spaceHeightR &&
                        mergeList.get(i).getRect().top < (mergeHead2.getRect().top + mid) &&
                        mergeList.get(i).getRect().bottom > mergeHead2.getRect().top) {
                    Log.d(TAG, "mergeJapan: right available");
                    blockList.add(0, mergeList.get(i));
                    mergeHead2 = mergeList.get(i);
                    i = 0;
                    Log.d(TAG, "merge: available right: " + mergeList.get(i).getText());
                }
            }
        }

        return blockList;

    }

    public DetectModel toDetectModel(String json){
        return gson.fromJson(json, DetectModel.class);
    }
}
