package com.wiryaimd.mangatranslator.util.translator.draw;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.wiryaimd.mangatranslator.model.merge.MergeBlockModel;

// this class do nothing
public class NonLatinDraw {

    private Paint paintBg, paintText, paintStroke;
    private float textLength, avgWidth, avgHeight, mid;

    private MergeBlockModel textBlock;

    public NonLatinDraw(){

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

}
