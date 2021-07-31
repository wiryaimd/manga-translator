package com.wiryaimd.mangatranslator.util.translator;

import android.util.Log;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.translate.AmazonTranslateAsyncClient;
import com.amazonaws.services.translate.AmazonTranslateClient;
import com.amazonaws.services.translate.model.TranslateTextRequest;
import com.amazonaws.services.translate.model.TranslateTextResult;
import com.wiryaimd.mangatranslator.util.Const;

import java.util.concurrent.Executors;

public class AWSTranslate {

    private static final String TAG = "AWSTranslate";

    private static AWSTranslate instance = null;

    private AmazonTranslateAsyncClient translateAsyncClient;

    public interface Listener{
        void complete(String translated, String source);
    }

    public static AWSTranslate getInstance(){
        if (instance == null){
            instance = new AWSTranslate();
        }
        return instance;
    }

    public AWSTranslate() {

        AWSCredentials credentials = new AWSCredentials() {
            @Override
            public String getAWSAccessKeyId() {
                return Const.AWS_ACCESS_ID;
            }

            @Override
            public String getAWSSecretKey() {
                return Const.AWS_SECRET_KEY;
            }
        };

        translateAsyncClient = new AmazonTranslateAsyncClient(credentials);

    }

    public void translateText(String text, String source, String target, Listener listener){
        String res = text.replaceAll("\\n", " ").replaceAll("-", " ").replaceAll("\\.", " ");

        TranslateTextRequest request = new TranslateTextRequest()
                .withText(res.toLowerCase())
                .withSourceLanguageCode(source)
                .withTargetLanguageCode(target);

        translateAsyncClient.translateTextAsync(request, new AsyncHandler<TranslateTextRequest, TranslateTextResult>() {
            @Override
            public void onError(Exception exception) {
                Log.d(TAG, "onError: TRANSLATE FAIL BROHH: " + exception.getMessage());
            }

            @Override
            public void onSuccess(TranslateTextRequest request, TranslateTextResult translateTextResult) {
                Log.d(TAG, "onSuccess: original: " + request.getText());
                Log.d(TAG, "onSuccess: translated: " + translateTextResult.getTranslatedText());
                listener.complete(translateTextResult.getTranslatedText(), res);
            }
        });
    }
}
