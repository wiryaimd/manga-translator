package com.wiryaimd.mangatranslator.util.vision;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.wiryaimd.mangatranslator.model.merge.MergeBlockModel;
import com.wiryaimd.mangatranslator.model.merge.MergeLineModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GRecognition {

    private static final String TAG = "GRecognition";

    private static GRecognition instance = null;

    private TextRecognizer textRecognizer;

    public interface Listener{
        void completeDetect(Iterator<MergeBlockModel> block, Canvas canvas);
    }

    public static GRecognition getInstance(){
        if (instance == null){
            instance = new GRecognition();
        }
        return instance;
    }

    public GRecognition() {
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
    }

    public void detect(Bitmap bitmap, Listener listener){
        Canvas canvas = new Canvas(bitmap);

        InputImage inputImage = InputImage.fromBitmap(bitmap, 0);

        Task<Text> task = textRecognizer.process(inputImage).addOnCompleteListener(new OnCompleteListener<Text>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Text> task) {
                List<MergeLineModel> mergeList = new ArrayList<>();
                Log.d(TAG, "onComplete: start cord");
                for (Text.TextBlock block : task.getResult().getTextBlocks()){
                    for (Text.Line line : block.getLines()){
                        if (line.getBoundingBox() != null) {
                            mergeList.add(new MergeLineModel(line.getText(), line.getBoundingBox()));
                        }
                    }
                }

                List<List<MergeLineModel>> mergeBlock = new ArrayList<>();
                for (int i = 0; i < mergeList.size();) {
                    List<MergeLineModel> result = merge(mergeList, mergeList.get(i));
                    mergeBlock.add(result);
                    mergeList.removeAll(result);
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
                        Log.d(TAG, "onComplete: text: " + line.getText());
                        sb.append(line.getText()).append(" ");
                    }
                    blockList.add(new MergeBlockModel(sb.toString(), new Rect(left, top, right, bottom), block));
                    Log.d(TAG, "onComplete: ");
                }

//                Iterator<Text.TextBlock> block = task.getResult().getTextBlocks().iterator();
                Iterator<MergeBlockModel> block = blockList.iterator();
                listener.completeDetect(block, canvas);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {

            }
        });
    }

    public List<MergeLineModel> merge(List<MergeLineModel> mergeList, MergeLineModel mergeLineModel){

        List<MergeLineModel> blockList = new ArrayList<>();
        MergeLineModel mergeHead = mergeLineModel;
        MergeLineModel mergeHead2 = mergeLineModel;

        blockList.add(mergeHead);
        for (int i = 0; i < mergeList.size(); i++) {
            float spaceHeightB = (mergeHead.getRect().bottom - mergeHead.getRect().top);

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
                float res = mergeList.get(i).getRect().top - mergeHead.getRect().bottom;
                float mid = mergeHead.getRect().centerX();
                if (res > 0 - spaceHeightB &&
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
                float res = mergeHead2.getRect().top - mergeList.get(i).getRect().bottom;
                float mid = mergeHead2.getRect().centerX();
                if (res > 0 - spaceHeightT &&
                        res <= spaceHeightT &&
                        mergeList.get(i).getRect().right > mid &&
                        mergeList.get(i).getRect().left < mid) {
                    blockList.add(0, mergeList.get(i));
                    mergeHead2 = mergeList.get(i);
                    i = 0;
                }
            }
        }

        return blockList;
    }

}
