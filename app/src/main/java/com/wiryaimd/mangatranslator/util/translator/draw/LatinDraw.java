package com.wiryaimd.mangatranslator.util.translator.draw;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.opengl.Matrix;
import android.util.Log;
import android.widget.Toast;

import com.google.mlkit.vision.text.Text;
import com.wiryaimd.mangatranslator.model.merge.MergeBlockModel;
import com.wiryaimd.mangatranslator.model.merge.MergeLineModel;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LatinDraw {

    private static final String TAG = "LatinDraw";

    private Paint paintBg, paintText, paintStroke;
    private float textLength, avgWidth, avgHeight, mid;
    private float widthJapan;

    private MergeBlockModel textBlock;

    private float bitmapH;

    public LatinDraw(int bitmapH){
        Log.d(TAG, "LatinDraw: bitmapH: " + bitmapH);
        this.bitmapH = (float)(bitmapH * 0.15);

        Log.d(TAG, "LatinDraw: bitmap height resized: " + this.bitmapH);

        paintBg = new Paint();

        // bisa kustom box color juga yekan biar uwu
        paintBg.setColor(Color.WHITE);

        paintText = new Paint();
        paintText.setTypeface(Typeface.DEFAULT_BOLD);
        paintText.setColor(Color.BLACK);
        paintText.setAntiAlias(true);

        paintStroke = new Paint();
        paintStroke.setTypeface(Typeface.DEFAULT_BOLD);
        paintStroke.setStyle(Paint.Style.STROKE);
        paintStroke.setColor(Color.WHITE);
        paintStroke.setAntiAlias(true);

    }

    public boolean update(Iterator<MergeBlockModel> block, Canvas canvas, String lang) {
        try {
            this.textBlock = block.next();
        }catch (NoSuchElementException e){
            return true;
        }
        Log.d(TAG, "update: textBlock: " + textBlock.getText());

        if (textBlock.getBoundingBox() == null){
            if (block.hasNext()) {
                update(block, canvas, lang);
            }else{
                return true;
            }
        }

        avgWidth = 0; avgHeight = 0; int countSize = 0;
        for (MergeLineModel line : textBlock.getLineList()){
            if (line.getRect() != null){
                avgWidth += (line.getRect().right - line.getRect().left);
                avgHeight += (line.getRect().bottom - line.getRect().top);

                if(lang.equalsIgnoreCase("ja")){
//                    avgWidthJapan = line.getRect().right - line.getRect().left;
                    canvas.drawRect(line.getRect(), paintBg);
                }

            }else{
                countSize += 1;
            }
        }
        
        if (!lang.equalsIgnoreCase("ja")) {
            canvas.drawRect(textBlock.getBoundingBox(), paintBg);
        }
        avgHeight = avgHeight / (textBlock.getLineList().size() - countSize);
        avgWidth = avgWidth / (textBlock.getLineList().size() -  countSize);

        mid = textBlock.getBoundingBox().centerX();

        if (lang.equalsIgnoreCase("ja")){
            //widthJapan = textBlock.getBoundingBox().right - textBlock.getBoundingBox().left;
            // widthJapan = widthJapan + (float)(widthJapan * 0.30);
            textLength = (avgHeight / avgWidth);
            //textLength = textLength + (float)(textLength * 0.20);
        }else{
            textLength = (avgWidth / avgHeight);
            textLength = textLength + (float)(textLength * 0.20);
        }

        if (lang.equalsIgnoreCase("ko") || lang.equalsIgnoreCase("zh-Hant")) {
            avgHeight = avgHeight - (float) (avgHeight * 0.50);
        }

        return false;
    }

    private float resize; //avgWidthJapan;

    public void drawTranslated(String translated, String original, Canvas canvas, boolean isJapan){
        Log.d(TAG, "drawTranslated: translated: " + translated);
        if (textBlock.getBoundingBox() == null){
            return;
        }
//        if (translated.length() > original.length()){
        if (isJapan){
            resize = (float)(avgWidth - (avgWidth * 0.20));
            Log.d(TAG, "drawTranslated: resize size: " + resize);
            Log.d(TAG, "drawTranslated: avg widthja: " + avgWidth);
            if (resize < bitmapH) {
                paintText.setTextSize(resize);
                paintStroke.setTextSize(resize);
                paintStroke.setStrokeWidth((float) (resize * 0.04));
            }
        }else {
            Log.d(TAG, "drawTranslated: avgheight: " + avgHeight);
            if (avgHeight < bitmapH) {
                paintText.setTextSize(avgHeight);
                paintStroke.setTextSize(avgHeight);
                paintStroke.setStrokeWidth((float) (avgHeight * 0.04));
            }
        }
//        }else{
//            paintText.setTextSize((float)(avgHeight + (avgHeight * 0.20)));
//            paintStroke.setTextSize((float)(avgHeight + (avgHeight * 0.20)));
//        }

//        float avgStroke = (float)(avgHeight * 0.02);

        StringBuilder sb = new StringBuilder();
        String[] res = translated.split("\\s+|\\n");

        int countLength = 0;
        for (String str : res){
//            if (!isJapan) {
                sb.append(str).append(" ");
                if ((sb.length() - countLength) > textLength) {
                    sb.append("\n");
                    countLength = sb.length();
                }
//            }else{
//                sb.append(str).append(" ");
//                if ((sb.length() - countLength) > textLength) {
//                    sb.append("\n");
//                    countLength = sb.length();
//                }
//            }
        }

        String[] drawList = sb.toString().split("\\n");
        float heightMid = textBlock.getBoundingBox().centerY() - ((avgHeight * drawList.length) / 2);
        float widthMidY = textBlock.getBoundingBox().centerY() - ((avgWidth * drawList.length) / 2);

        for (String draw : drawList) {
            Log.d(TAG, "onSuccess: measure: " + paintText.measureText(draw));
            float textMid = mid - (paintText.measureText(draw) / 2);
            if (isJapan){
//                float textY = widthJapan / avgHeight;
                widthMidY += avgWidth;
                canvas.drawText(draw.toUpperCase(), textMid, widthMidY, paintText);
                canvas.drawText(draw.toUpperCase(), textMid, widthMidY, paintStroke);
            }else{
                heightMid += avgHeight;
                canvas.drawText(draw.toUpperCase(), textMid, heightMid, paintText);
                canvas.drawText(draw.toUpperCase(), textMid, heightMid, paintStroke);
            }
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
