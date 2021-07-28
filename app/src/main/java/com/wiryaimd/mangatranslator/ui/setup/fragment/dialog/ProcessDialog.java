package com.wiryaimd.mangatranslator.ui.setup.fragment.dialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.mlkit.nl.translate.TranslateLanguage;
import com.wiryaimd.mangatranslator.R;
import com.wiryaimd.mangatranslator.model.SelectedModel;
import com.wiryaimd.mangatranslator.model.merge.MergeBlockModel;
import com.wiryaimd.mangatranslator.model.merge.MergeLineModel;
import com.wiryaimd.mangatranslator.ui.setup.fragment.ResultFragment;
import com.wiryaimd.mangatranslator.ui.setup.SetupViewModel;
import com.wiryaimd.mangatranslator.util.Const;
import com.wiryaimd.mangatranslator.util.LanguagesData;
import com.wiryaimd.mangatranslator.util.RealPath;
import com.wiryaimd.mangatranslator.util.storage.CStorage;
import com.wiryaimd.mangatranslator.util.translator.AWSTranslate;
import com.wiryaimd.mangatranslator.util.translator.GTranslate;
import com.wiryaimd.mangatranslator.util.translator.draw.LatinDraw;
import com.wiryaimd.mangatranslator.util.vision.GRecognition;
import com.wiryaimd.mangatranslator.util.vision.MSRecognition;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;

public class ProcessDialog extends DialogFragment {

    private static final String TAG = "ProcessDialog";

    private SetupViewModel setupViewModel;

    private TextView tvinfo;
    private ProgressBar loading;

    private List<SelectedModel> selectedList;
    private List<Bitmap> bitmapList;
    private ArrayList<Bitmap> resultList;

    private int flagFrom, flagTo;

    private int countTranslate;

    private SetupViewModel.TranslateEngine translateEngine;

    private GRecognition gRecognition;
    private MSRecognition msRecognition;

    private GTranslate gTranslate;
    private AWSTranslate awsTranslate;

    private CStorage storage;

    private Bitmap bitmap;

    private String lang;

    private boolean isLatin = true;

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
                setupViewModel.getSelectedModelLiveData().getValue() == null ||
                setupViewModel.getTeLiveData().getValue() == null){
            if (getDialog() != null) {
                getDialog().dismiss();
                Toast.makeText(setupViewModel.getApplication(), "Failed to start translating image/pdf", Toast.LENGTH_LONG).show();
            }
            return;
        }
        storage = new CStorage();
        resultList = new ArrayList<>();
        bitmapList = new ArrayList<>();

        flagFrom = setupViewModel.getFlagFromLiveData().getValue();
        flagTo = setupViewModel.getFlagToLiveData().getValue();
        selectedList = setupViewModel.getSelectedModelLiveData().getValue();
        translateEngine = setupViewModel.getTeLiveData().getValue();

        Log.d(TAG, "onViewCreated: translate Engine: " + translateEngine.name());

        lang = LanguagesData.flag_code_from[flagFrom];
        
        if (selectedList.size() == 0){
            return;
        }

        if (selectedList.get(0).getType() == SelectedModel.Type.PDF) {
            bitmapList = setupViewModel.getPdfListLiveData().getValue();
            if (bitmapList == null || bitmapList.size() == 0){
                Log.d(TAG, "onViewCreated: cant read pdf file");
                Toast.makeText(setupViewModel.getApplication(), "Cannot read pdf file", Toast.LENGTH_SHORT).show();
                if (getDialog() != null) getDialog().dismiss();
                return;
            }
            tvinfo.setText(("Processing image " + (countTranslate + 1) + "/" + bitmapList.size()));
        }else{
            tvinfo.setText(("Processing image " + (countTranslate + 1) + "/" + selectedList.size()));
        }

        awsTranslate = setupViewModel.getAwsTranslate();
        gTranslate = setupViewModel.getGTranslate();
        gTranslate.init(LanguagesData.flag_id_from[flagFrom], LanguagesData.flag_id_to[flagTo]);

        if (LanguagesData.flag_id_from[flagFrom].equalsIgnoreCase(TranslateLanguage.ENGLISH) ||
                LanguagesData.flag_id_from[flagFrom].equalsIgnoreCase(TranslateLanguage.INDONESIAN)){
            isLatin = true;
        }else{
            isLatin = false;
        }

        if (isLatin) {
            gRecognition = setupViewModel.getGRecognition();

            detectText();

        }else{
            // dooo recog mikocok
            msRecognition = setupViewModel.getMsRecognition();

            Log.d(TAG, "onViewCreated: nlatin");
            detectNLatin();
        }
    }

    public void checkBitmap(){
        if (selectedList.get(0).getType() == SelectedModel.Type.IMAGE) {
            bitmap = loadBitmap(selectedList.get(countTranslate).getUri());
        }else{
            bitmap = bitmapList.get(countTranslate);
        }
    }

    public void detectText(){
        LatinDraw latinDraw = new LatinDraw();
        checkBitmap();

        // detect text
        gRecognition.detect(bitmap, new GRecognition.Listener() {
            @Override
            public void completeDetect(Iterator<MergeBlockModel> block, Canvas canvas) {
                // draw bg & get position
                boolean isFinish = latinDraw.update(block, canvas, lang);
                if (isFinish){
                    addBitmap();
                }

                // translate text
                if (translateEngine == SetupViewModel.TranslateEngine.ON_DEVICE) {
                    gTranslate.translate(latinDraw.getTextBlock().getText(), new GTranslate.Listener() {
                        @Override
                        public void complete(String translated, String source) {
                            // draw translated
                            Log.d(TAG, "complete: latin device translate");
                            latinDraw.drawTranslated(translated, source, canvas, false);

                            if (block.hasNext()) {
                                completeDetect(block, canvas);
                            } else {
                                addBitmap();
                            }
                        }
                    });
                }else{
                    awsTranslate.translateText(latinDraw.getTextBlock().getText(), LanguagesData.flag_id_from[flagFrom], LanguagesData.flag_id_to[flagTo], new AWSTranslate.Listener() {
                        @Override
                        public void complete(String translated, String source) {
                            Log.d(TAG, "complete: latin api translate");

                            latinDraw.drawTranslated(translated, source, canvas, false);

                            if (block.hasNext()) {
                                completeDetect(block, canvas);
                            } else {
                                addBitmap();
                            }
                        }
                    });
                }
            }
        });
    }

    public void detectNLatin(){
        LatinDraw latinDraw = new LatinDraw();

        checkBitmap();
        if (lang.equalsIgnoreCase("zh")){
            lang = "zh-Hant";
        }
        String options = "detectOrientation=true&language=" + lang;

        Canvas canvas = new Canvas(bitmap);

        storage.uploadImg(bitmap, new CStorage.Listener() {
            @Override
            public void success(String url) {
                Log.d(TAG, "success: url img: " + url);
                msRecognition.requestDetectModel(url, options, new MSRecognition.Listener() {
                    @Override
                    public void success(Iterator<MergeBlockModel> block) {
                        boolean isFinish = latinDraw.update(block, canvas, lang);
                        if (isFinish){
                            addBitmap();
                        }

                        // translate text
                        if (translateEngine == SetupViewModel.TranslateEngine.ON_DEVICE) {
                            gTranslate.translate(latinDraw.getTextBlock().getText(), new GTranslate.Listener() {
                                @Override
                                public void complete(String translated, String source) {
                                    Log.d(TAG, "complete: non-latin device translate");

                                    // draw translated
                                    latinDraw.drawTranslated(translated, source, canvas, false);

                                    if (block.hasNext()) {
                                        success(block);
                                    } else {
                                        addBitmap();
                                    }
                                }
                            });
                        }else{
                            awsTranslate.translateText(latinDraw.getTextBlock().getText(), LanguagesData.flag_id_from[flagFrom], LanguagesData.flag_id_to[flagTo], new AWSTranslate.Listener() {
                                @Override
                                public void complete(String translated, String source) {
                                    Log.d(TAG, "complete: non-latin api translate");

                                    latinDraw.drawTranslated(translated, source, canvas, false);

                                    if (block.hasNext()) {
                                        success(block);
                                    } else {
                                        addBitmap();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public List<MergeLineModel> mergeNormal(List<MergeLineModel> mergeList, MergeLineModel mergeLineModel) {
                        return setupViewModel.getGRecognition().merge(mergeList, mergeLineModel);
                    }
                });
            }
        });
    }

    public void addBitmap(){
        resultList.add(bitmap);
//        bitmap.recycle();
        Log.d(TAG, "addBitmap: boom done");
        updateData();
    }

    public void updateData(){
        countTranslate += 1;

        if (bitmapList.size() != 0){
            if (countTranslate < bitmapList.size()){
                if (isLatin){
                    detectText();
                }else{
                    detectNLatin();
                }
            }else{
                setupViewModel.getBitmapListLiveData().setValue(resultList);
                FragmentTransaction ft = getParentFragmentManager().beginTransaction().replace(R.id.setuplang_mainframe, new ResultFragment());
                ft.commit();
                if (getDialog() != null) getDialog().dismiss();
            }
            tvinfo.setText(("Processing image " + (countTranslate + 1) + "/" + bitmapList.size()));
        }else {
            if (countTranslate < selectedList.size()) {
                if (isLatin){
                    detectText();
                }else{
                    detectNLatin();
                }
            } else {
                setupViewModel.getBitmapListLiveData().setValue(resultList);
                FragmentTransaction ft = getParentFragmentManager().beginTransaction().replace(R.id.setuplang_mainframe, new ResultFragment());
                ft.commit();
                if (getDialog() != null) getDialog().dismiss();
            }
            tvinfo.setText(("Processing image " + (countTranslate + 1) + "/" + selectedList.size()));
        }
    }

    public Bitmap loadBitmap(Uri uri){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        try {
            InputStream input = setupViewModel.getApplication().getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);
            input.close();

            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onDismiss(@NonNull @NotNull DialogInterface dialog) {
        super.onDismiss(dialog);

        if (gTranslate != null) gTranslate.close();
    }
}
