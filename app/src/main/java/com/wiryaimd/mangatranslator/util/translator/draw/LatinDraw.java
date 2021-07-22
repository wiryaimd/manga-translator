package com.wiryaimd.mangatranslator.util.translator.draw;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;

import com.google.mlkit.vision.text.Text;
import com.wiryaimd.mangatranslator.model.merge.MergeBlockModel;
import com.wiryaimd.mangatranslator.model.merge.MergeLineModel;

import java.util.Iterator;

public class LatinDraw {

    private static final String TAG = "LatinDraw";

    private Paint paintBg, paintText, paintStroke;
    private float textLength, avgWidth, avgHeight, mid;

    private MergeBlockModel textBlock;

    public LatinDraw(){

        paintBg = new Paint();
        paintBg.setColor(Color.WHITE);

        paintText = new Paint();
        paintText.setTypeface(Typeface.DEFAULT_BOLD);
        paintText.setColor(Color.BLACK);

        paintStroke = new Paint();
        paintStroke.setTypeface(Typeface.DEFAULT_BOLD);
        paintStroke.setStyle(Paint.Style.STROKE);
        paintStroke.setColor(Color.WHITE);

    }

    public boolean update(Iterator<MergeBlockModel> block, Canvas canvas) {
        this.textBlock = block.next();
        Log.d(TAG, "update: textBlock: " + textBlock.getText());

        if (textBlock.getBoundingBox() == null){
            if (block.hasNext()) {
                update(block, canvas);
            }else{
                return true;
            }
        }

        avgWidth = 0; avgHeight = 0; int countSize = 0;
        for (MergeLineModel line : textBlock.getLineList()){
            if (line.getRect() != null){
                avgWidth += (line.getRect().right - line.getRect().left);
                avgHeight += (line.getRect().bottom - line.getRect().top);
            }else{
                countSize += 1;
            }
        }
        canvas.drawRect(textBlock.getBoundingBox(), paintBg);
        avgHeight = avgHeight / (textBlock.getLineList().size() - countSize);
        avgWidth = avgWidth / (textBlock.getLineList().size() -  countSize);

        mid = (float)(textBlock.getBoundingBox().left + ((textBlock.getBoundingBox().right - textBlock.getBoundingBox().left) / 2));

        textLength = (avgWidth / avgHeight);
        textLength = textLength + (float)(textLength * 0.20);

        return false;
    }

    public void drawTranslated(String translated, String original, Canvas canvas){
        if (textBlock.getBoundingBox() == null){
            return;
        }
//        if (translated.length() > original.length()){
            paintText.setTextSize(avgHeight);
            paintStroke.setTextSize(avgHeight);
//        }else{
//            paintText.setTextSize((float)(avgHeight + (avgHeight * 0.20)));
//            paintStroke.setTextSize((float)(avgHeight + (avgHeight * 0.20)));
//        }

        float avgStroke = (float)(avgHeight * 0.02);
        paintStroke.setStrokeWidth(avgStroke);

        StringBuilder sb = new StringBuilder();
        String[] res = translated.split("\\s+|\\n");

        int countLength = 0;
        for (String str : res){
            sb.append(str).append(" ");
            if ((sb.length() - countLength) > textLength){
                sb.append("\n");
                countLength = sb.length();
            }
        }

        int i = 0;
        for (String draw : sb.toString().split("\\n")) {
            Log.d(TAG, "onSuccess: measure: " + paintText.measureText(draw));
            float textMid = mid - (paintText.measureText(draw) / 2);
            float textY = textBlock.getBoundingBox().top + avgHeight + i;
            canvas.drawText(draw.toUpperCase(), textMid, textY, paintText);
//            canvas.drawText(draw.toUpperCase(), textMid, textY, paintStroke);
            i += avgHeight;
        }
        Log.d(TAG, "drawTranslated: drawed brohh");
    }

    public MergeBlockModel getTextBlock() {
        return textBlock;
    }

    public Paint getPaintBg() {
        return paintBg;
    }

    public Paint getPaintText() {
        return paintText;
    }

    public Paint getPaintStroke() {
        return paintStroke;
    }
}
