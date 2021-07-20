package com.wiryaimd.mangatranslator.util.translator;

import android.graphics.Paint;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.google.mlkit.vision.text.Text;
import com.wiryaimd.mangatranslator.util.LanguagesData;
import com.wiryaimd.mangatranslator.util.translator.draw.LatinDraw;

import org.jetbrains.annotations.NotNull;

public class GTranslate {

    private static final String TAG = "GTranslate";

    private static GTranslate instance = null;
    private Translator translator;

    public interface Listener{
        void complete(String translated, String source);
    }

    public static GTranslate getInstance() {
        if (instance == null){
            instance = new GTranslate();
        }
        return instance;
    }

    public GTranslate() {

    }

    public void init(String from, String to){
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(from)
                .setTargetLanguage(to)
                .build();

        translator = Translation.getClient(options);
    }

    public void translate(String source, Listener listener){
        String result = source.replaceAll("\\n", " ").replaceAll("\\.", " ");
        translator.translate(result.toLowerCase()).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(@NonNull @NotNull String s) {
                listener.complete(s, result);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d(TAG, "onFailure: fail translate: " + e.getMessage());
            }
        });
    }

    public void close(){
        translator.close();
    }
}
