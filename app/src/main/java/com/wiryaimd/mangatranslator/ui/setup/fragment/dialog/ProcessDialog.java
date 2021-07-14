package com.wiryaimd.mangatranslator.ui.setup.fragment.dialog;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognizerOptions;
import com.wiryaimd.mangatranslator.R;
import com.wiryaimd.mangatranslator.model.SelectedModel;
import com.wiryaimd.mangatranslator.ui.setup.SetupViewModel;
import com.wiryaimd.mangatranslator.util.LanguagesData;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;

public class ProcessDialog extends DialogFragment {

    private static final String TAG = "ProcessDialog";

    private SetupViewModel setupViewModel;

    private TextView tvinfo;
    private ProgressBar loading;

    private List<SelectedModel> selectedList;
    private int flagFrom, flagTo;

    private Translator translator;
    private TextRecognizer textRecognizer;

    private int countTranslate;

    private Bitmap bitmap;
    private Canvas canvas;
    private Paint paintBg, paintText, paintStroke;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        if (getDialog() != null){
            getDialog().requestWindowFeature(STYLE_NO_TITLE);
        }
        return inflater.inflate(R.layout.dialog_process, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        tvinfo = view.findViewById(R.id.process_info);
        loading = view.findViewById(R.id.process_loading);
        
        countTranslate = 0;

        setupViewModel = new ViewModelProvider(requireActivity()).get(SetupViewModel.class);
        if (setupViewModel.getFlagFromLiveData().getValue() == null || 
                setupViewModel.getFlagToLiveData().getValue() == null || 
                setupViewModel.getSelectedModelLiveData().getValue() == null){
            return;
        }

        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        flagFrom = setupViewModel.getFlagFromLiveData().getValue();
        flagTo = setupViewModel.getFlagToLiveData().getValue();
        selectedList = setupViewModel.getSelectedModelLiveData().getValue();

        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(LanguagesData.flag_id_from[flagFrom])
                .setTargetLanguage(LanguagesData.flag_id_to[flagTo])
                .build();
        
        translator = Translation.getClient(options);

        paintBg = new Paint();
        paintBg.setColor(Color.WHITE);

        Typeface typeface = Typeface.createFromAsset(setupViewModel.getApplication().getAssets(), "sfbold.ttf");
        paintText = new Paint();
        paintText.setTypeface(Typeface.DEFAULT_BOLD);
        paintText.setColor(Color.BLACK);

        paintStroke = new Paint();
        paintStroke.setTypeface(Typeface.DEFAULT_BOLD);
        paintStroke.setStyle(Paint.Style.STROKE);
        paintStroke.setColor(Color.WHITE);
        
        if (selectedList.size() == 0){
            return;
        }
        tvinfo.setText(("Processing image " + countTranslate + "/" + selectedList.size()));
        detectText();
    }

    public static Bitmap getBitmap(ContentResolver cr, Uri url) throws FileNotFoundException, IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        InputStream input = cr.openInputStream(url);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);
        input.close();
        return bitmap;
    }

    public void detectText(){
        try {
//            Bitmap rawbitmap = MediaStore.Images.Media.getBitmap(setupViewModel.getApplication().getContentResolver(), selectedList.get(countTranslate).getUri());
//            Log.d(TAG, "detectText: widht: " + rawbitmap.getWidth() + " height: " + rawbitmap.getHeight());
            bitmap = getBitmap(setupViewModel.getApplication().getContentResolver(), selectedList.get(countTranslate).getUri());
            Log.d(TAG, "detectText: w: " + bitmap.getWidth());
            Log.d(TAG, "detectText: h: " + bitmap.getHeight());
//            rawbitmap.recycle();

            canvas = new Canvas(bitmap);

            InputImage inputImage = InputImage.fromBitmap(bitmap, 0);

            Task<Text> task = textRecognizer.process(inputImage).addOnCompleteListener(new OnCompleteListener<Text>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Text> task) {
                    Iterator<Text.TextBlock> block = task.getResult().getTextBlocks().iterator();
                    if (block.hasNext()) {
                        processText(block);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private float textLength, avgWidth, avgHeight, mid;

    public void processText(Iterator<Text.TextBlock> block){
        Text.TextBlock textBlock = block.next();

        if (textBlock.getBoundingBox() == null){
            if (block.hasNext()) {
                processText(block);
            }else{
                saveBitmap();
            }
            return;
        }

        Log.d(TAG, "processText: process: " + textBlock.getText());
        avgWidth = 0; avgHeight = 0; int countSize = 0;
        for (Text.Line line : textBlock.getLines()){
            if (line.getBoundingBox() != null){
                avgWidth += (line.getBoundingBox().right - line.getBoundingBox().left);
                avgHeight += (line.getBoundingBox().bottom - line.getBoundingBox().top);
            }else{
                countSize += 1;
            }
            canvas.drawRect(line.getBoundingBox(), paintBg);
        }
        avgHeight = avgHeight / (textBlock.getLines().size() - countSize);
        avgWidth = avgWidth / (textBlock.getLines().size() -  countSize);

        mid = (float)(textBlock.getBoundingBox().left + ((textBlock.getBoundingBox().right - textBlock.getBoundingBox().left) / 2));
//        int midHeight = (textBlock.getBoundingBox().top + ((textBlock.getBoundingBox().bottom - textBlock.getBoundingBox().top) / 2));
//        int pixel = bitmap.getPixel((int)mid, bitmap.getHeight() - midHeight);
//        int resultColor = Color.rgb(Color.red(pixel), Color.green(pixel), Color.blue(pixel));
//        paintBg.setColor(resultColor);

        textLength = (avgWidth / avgHeight);
        textLength = textLength + (float)(textLength * 0.20);
        Log.d(TAG, "processText: avgWidth: " + avgWidth);
        Log.d(TAG, "processText: avgHeight: " + avgHeight);
        Log.d(TAG, "processText: textLength: " + textLength);

        translator.translate(textBlock.getText()).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(@NonNull @NotNull String s) {
                StringBuilder sb = new StringBuilder();
                String[] res = s.split("\\s+|\\n");

                if (s.length() > textBlock.getText().length()){
                    paintText.setTextSize(avgHeight);
                    paintStroke.setTextSize(avgHeight);
                }else{
                    paintText.setTextSize((float)(avgHeight + (avgHeight * 0.20)));
                    paintStroke.setTextSize((float)(avgHeight + (avgHeight * 0.20)));
                }
                float avgStroke = (float)(avgHeight * 0.04);
                paintStroke.setStrokeWidth(avgStroke);

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
                    canvas.drawText(draw.toUpperCase(), textMid, textY, paintStroke);
                    i += avgHeight;
                }
                if (block.hasNext()) {
                    processText(block);
                }else{
                    saveBitmap();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d(TAG, "onFailure: fail translate: " + e.getMessage());
            }
        });
    }

    public void saveBitmap(){
        File dir = new File(Environment.getExternalStorageDirectory().toString());
        dir.mkdirs();
        String filename = "mngTranslator-" + selectedList.get(countTranslate).getName() + "(" + countTranslate + ")" + UUID.randomUUID().toString() + ".jpg";
        File file = new File(dir, filename);
        if (file.exists()){
            file.delete();
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();
            bitmap.recycle();

            Log.d(TAG, "saveBitmap: boom bieac hahahah ihihi ahyuuu");
            
            updateData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void updateData(){
        tvinfo.setText(("Processing image " + (countTranslate + 1) + "/" + selectedList.size()));
        countTranslate += 1;
        
        if (countTranslate < selectedList.size()){
            detectText();
        }else{
            getDialog().dismiss();
        }
    }

    @Override
    public void onDismiss(@NonNull @NotNull DialogInterface dialog) {
        super.onDismiss(dialog);
        
        if (translator != null) translator.close();
        Log.d(TAG, "onDismiss: cek close");
    }
}
