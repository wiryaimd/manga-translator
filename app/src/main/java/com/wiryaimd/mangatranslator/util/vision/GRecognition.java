package com.wiryaimd.mangatranslator.util.vision;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
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
import com.wiryaimd.mangatranslator.model.MergeModel;
import com.wiryaimd.mangatranslator.model.SelectedModel;
import com.wiryaimd.mangatranslator.ui.result.ResultActivity;
import com.wiryaimd.mangatranslator.util.Const;

import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class GRecognition {

    private static final String TAG = "GRecognition";

    private static GRecognition instance = null;

    private TextRecognizer textRecognizer;
    private List<List<MergeModel>> mergeBlock;
    private List<MergeModel> mergeList;

    private int count;

    private Paint tempPaint;

    public interface Listener{
        void completeDetect(Iterator<Text.TextBlock> block, Canvas canvas);
    }

    public static GRecognition getInstance(){
        if (instance == null){
            instance = new GRecognition();
        }
        return instance;
    }

    public GRecognition() {
        count = 0;

        tempPaint = new Paint();
        tempPaint.setTypeface(Typeface.DEFAULT_BOLD);
        tempPaint.setColor(Color.BLACK);
        
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
    }

    public void detect(Bitmap bitmap, Listener listener){
        Canvas canvas = new Canvas(bitmap);

        InputImage inputImage = InputImage.fromBitmap(bitmap, 0);

        Task<Text> task = textRecognizer.process(inputImage).addOnCompleteListener(new OnCompleteListener<Text>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Text> task) {
                mergeBlock = new ArrayList<>();
                mergeList = new ArrayList<>();

                Log.d(TAG, "onComplete: start cord");
                for (Text.TextBlock block : task.getResult().getTextBlocks()){
                    for (Text.Line line : block.getLines()){
                        if (line.getBoundingBox() != null) {
                            Log.d(TAG, "onComplete: Text: " + line.getText());
                            String msg = "left: " + line.getBoundingBox().left + "\n" + 
                                    "top: " + line.getBoundingBox().top + "\n" +
                                    "bottom: " + line.getBoundingBox().bottom + "\n" +
                                    "right: " + line.getBoundingBox().right + "\n";
                            Log.d(TAG, "onComplete: line cord: " + msg);
                            mergeList.add(new MergeModel(line.getText(), line.getBoundingBox()));
                        }
                    }
                }

                merge();
                
                for(List<MergeModel> merges : mergeBlock){
                    Log.d(TAG, "onComplete: block crot");
                    for (MergeModel model : merges){
                        Log.d(TAG, "onComplete: text: " + model.getText());
                    }
                    Log.d(TAG, "onComplete: ");
                }

                Iterator<Text.TextBlock> block = task.getResult().getTextBlocks().iterator();
                listener.completeDetect(block, canvas);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {

            }
        });
    }

    public void merge(){

        MergeModel mergeModel = mergeList.get(count);

        List<MergeModel> blockList = new ArrayList<>();
        MergeModel mergeHead = mergeModel;
        MergeModel mergeHead2 = mergeModel;

        blockList.add(mergeHead);
        for (int i = 0; i < mergeList.size(); i++) {
            float spaceHeightB = mergeHead.getRect().bottom - mergeHead.getRect().top;

            boolean isAvailableBottom = false;
            sLoop: for (int j = 0; j < blockList.size(); j++) {
//                if (blockList.get(j).getRect().centerX() == mergeHead.getRect().centerX() &&
//                        blockList.get(j).getRect().centerY() == mergeHead.getRect().centerY()){
                if (blockList.get(j) != mergeList.get(i)){
                    isAvailableBottom = true;
                }else{
                    isAvailableBottom = false;
                    break sLoop;
                }
            }

            if (isAvailableBottom) {
                Log.d(TAG, "merge: available bottom: " + mergeList.get(i).getText());
                float res = mergeList.get(i).getRect().top - mergeHead.getRect().bottom;
                float mid = mergeHead.getRect().centerX();
                if (res > 0 &&
                        res <= spaceHeightB &&
                        mergeList.get(i).getRect().right > mid &&
                        mergeList.get(i).getRect().left < mid) {
                    blockList.add(mergeList.get(i));
                    mergeHead = mergeList.get(i);
                    i = 0;
                }
            }
        }

        for (int i = 0; i < mergeList.size(); i++) {
            float spaceHeightT = mergeHead2.getRect().bottom - mergeHead2.getRect().top;
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
                Log.d(TAG, "merge: available top: " + mergeList.get(i).getText());
                float res = mergeHead2.getRect().top - mergeList.get(i).getRect().bottom;
                float mid = mergeHead2.getRect().centerX();
                if (res > 0 &&
                        res <= spaceHeightT &&
                        mergeList.get(i).getRect().right > mid &&
                        mergeList.get(i).getRect().left < mid) {
                    blockList.add(0, mergeList.get(i));
                    mergeHead2 = mergeList.get(i);
                    i = 0;
                }
            }
        }

        mergeBlock.add(blockList);
        mergeList.removeAll(blockList);
        if (mergeList.size() > 0){
            count += 1;
            merge();
        }
    }

}
