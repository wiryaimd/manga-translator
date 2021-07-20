package com.wiryaimd.mangatranslator.util.storage;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.wiryaimd.mangatranslator.util.Const;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CStorage {

    private static final String TAG = "CStorage";

    private static CStorage instance = null;
    
    private Context context;

    interface Listener{
        void success(String requestId, String fileName);
    }

    public static CStorage getInstance(Context context) {
        if (instance == null){
            instance = new CStorage(context);
        }
        return instance;
    }

    public CStorage(Context context) {
        this.context = context;
        Log.d(TAG, "CStorage: cekk bosss storage");
    }
    
    public void init(){
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", Const.CLOUDINARY_NAME);
        config.put("api_key", Const.CLOUDINARY_KEY);
        config.put("api_secret", Const.CLOUDINARY_SECRET);
        MediaManager.init(context, config);
    }

    public void uploadImg(Bitmap bitmap, Listener listener){
        String fileName = "mngTranslator-" + UUID.randomUUID().toString();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);

        MediaManager.get().upload(stream.toByteArray())
                .unsigned(Const.CLOUDINARY_UNSIGNED)
                .option("public_id", fileName)
                .option("connect_timeout", Const.CLOUDINARY_CONNECT_TIMEOUT)
                .option("read_timeout", Const.CLOUDINARY_READ_TIMEOUT)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Log.d(TAG, "onStart: START IMG");
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {

                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        listener.success(requestId, fileName);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Log.d(TAG, "onError: ERROR IMG: " + error.getDescription());
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {

                    }
                }).dispatch();
    }
}
