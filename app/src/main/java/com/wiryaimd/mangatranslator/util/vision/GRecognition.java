package com.wiryaimd.mangatranslator.util.vision;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognizerOptions;
import com.wiryaimd.mangatranslator.model.SelectedModel;
import com.wiryaimd.mangatranslator.ui.result.ResultActivity;
import com.wiryaimd.mangatranslator.util.Const;

import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class GRecognition {

    private static GRecognition instance = null;

    private TextRecognizer textRecognizer;

    private Context context;

    public interface Listener{
        void completeDetect(Iterator<Text.TextBlock> block, Canvas canvas);
    }

    public static GRecognition getInstance(Context context){
        if (instance == null){
            instance = new GRecognition(context);
        }
        return instance;
    }

    public GRecognition(Context context) {
        this.context = context;

        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
    }

    public void detect(Bitmap bitmap, Listener listener){
        Canvas canvas = new Canvas(bitmap);

        InputImage inputImage = InputImage.fromBitmap(bitmap, 0);

        Task<Text> task = textRecognizer.process(inputImage).addOnCompleteListener(new OnCompleteListener<Text>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Text> task) {
                Iterator<Text.TextBlock> block = task.getResult().getTextBlocks().iterator();
                listener.completeDetect(block, canvas);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {

            }
        });
    }

    public Bitmap loadBitmap(Uri uri){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        try {
            InputStream input = context.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);
            input.close();

            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
