package com.wiryaimd.mangatranslator.ui.setup.fragment.dialog;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
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
import com.google.firebase.FirebaseApp;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognizerOptions;
import com.wiryaimd.mangatranslator.R;
import com.wiryaimd.mangatranslator.api.model.DetectModel;
import com.wiryaimd.mangatranslator.model.ResultModel;
import com.wiryaimd.mangatranslator.model.SelectedModel;
import com.wiryaimd.mangatranslator.model.merge.MergeBlockModel;
import com.wiryaimd.mangatranslator.model.merge.MergeLineModel;
import com.wiryaimd.mangatranslator.ui.result.ResultActivity;
import com.wiryaimd.mangatranslator.ui.setup.SetupActivity;
import com.wiryaimd.mangatranslator.ui.setup.SetupViewModel;
import com.wiryaimd.mangatranslator.util.Const;
import com.wiryaimd.mangatranslator.util.LanguagesData;
import com.wiryaimd.mangatranslator.util.RealPath;
import com.wiryaimd.mangatranslator.util.storage.CStorage;
import com.wiryaimd.mangatranslator.util.translator.GTranslate;
import com.wiryaimd.mangatranslator.util.translator.draw.LatinDraw;
import com.wiryaimd.mangatranslator.util.vision.GRecognition;
import com.wiryaimd.mangatranslator.util.vision.MSRecognition;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
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

import okhttp3.Response;

public class ProcessDialog extends DialogFragment {

    private static final String TAG = "ProcessDialog";

    private SetupViewModel setupViewModel;

    private TextView tvinfo;
    private ProgressBar loading;

    private List<SelectedModel> selectedList;
    private List<Bitmap> bitmapList;
    private ArrayList<ResultModel> resultList;

    private int flagFrom, flagTo;

    private int countTranslate;

    private GRecognition gRecognition;
    private MSRecognition msRecognition;

    private GTranslate gTranslate;
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
                setupViewModel.getSelectedModelLiveData().getValue() == null){
            return;
        }
        storage = new CStorage();
        resultList = new ArrayList<>();
        bitmapList = new ArrayList<>();

        flagFrom = setupViewModel.getFlagFromLiveData().getValue();
        flagTo = setupViewModel.getFlagToLiveData().getValue();
        selectedList = setupViewModel.getSelectedModelLiveData().getValue();

        lang = LanguagesData.flag_code_from[flagFrom];
        
        if (selectedList.size() == 0){
            return;
        }

        if (selectedList.get(0).getType() == SelectedModel.Type.PDF) {
            bitmapList = loadBitmapList();
            tvinfo.setText(("Processing image " + (countTranslate + 1) + "/" + bitmapList.size()));
        }else{
            tvinfo.setText(("Processing image " + (countTranslate + 1) + "/" + selectedList.size()));
        }

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
                gTranslate.translate(latinDraw.getTextBlock().getText(), new GTranslate.Listener() {
                    @Override
                    public void complete(String translated, String source) {
                        // draw translated
                        latinDraw.drawTranslated(translated, source, canvas, false);

                        if (block.hasNext()) {
                            completeDetect(block, canvas);
                        }else{
                            addBitmap();
                        }
                    }
                });
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
                        gTranslate.translate(latinDraw.getTextBlock().getText(), new GTranslate.Listener() {
                            @Override
                            public void complete(String translated, String source) {
                                // draw translated
                                latinDraw.drawTranslated(translated, source, canvas, false);

                                if (block.hasNext()) {
                                    success(block);
                                }else{
                                    addBitmap();
                                }
                            }
                        });
                    }

                    @Override
                    public List<MergeLineModel> mergeNormal(List<MergeLineModel> mergeList, MergeLineModel mergeLineModel) {
                        return setupViewModel.getGRecognition().merge(mergeList, mergeLineModel, lang);
                    }
                });
            }
        });
    }

    public List<Bitmap> loadBitmapList(){
        List<Bitmap> bitmapList = new ArrayList<>();
        try {
            File file = RealPath.from(setupViewModel.getApplication(), selectedList.get(0).getUri());
            PdfRenderer pdfRenderer = new PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY));
            Bitmap bitmap;
            Log.d(TAG, "onActivityResult: count: " + pdfRenderer.getPageCount());
            for (int i = 0; i < pdfRenderer.getPageCount(); i++) {
                PdfRenderer.Page page = pdfRenderer.openPage(i);
                int width = getResources().getDisplayMetrics().densityDpi / 72 * page.getWidth();
                int height = getResources().getDisplayMetrics().densityDpi / 72 * page.getHeight();
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                canvas.drawColor(Color.WHITE);
                canvas.drawBitmap(bitmap, 0, 0, null);
                Rect r = new Rect(0, 0, width, height);
                page.render(bitmap, r, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT);
                bitmapList.add(bitmap);
                page.close();
            }
            pdfRenderer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "loadBitmapList: uwu crot");
        return bitmapList;
    }

    public void addBitmap(){
//        if (bitmapList.size() != 0) {
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
//            resultList.add(new ResultModel(stream.toByteArray()));
//
//            try {
//                stream.flush();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            updateData();
//            return;
//        }

        File dir = new File(Environment.getExternalStorageDirectory().toString());
        dir.mkdirs();
        String filename;

        if (bitmapList.size() == 0) {
            filename = "mngTranslator-" + selectedList.get(countTranslate).getName() + "(" + countTranslate + ")" + UUID.randomUUID().toString() + ".jpg";
        }else{
            filename = "mngTranslator-" + "pdf" + "(" + countTranslate + ")" + UUID.randomUUID().toString() + ".jpg";
        }

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
        countTranslate += 1;

        Intent intent = new Intent(setupViewModel.getApplication(), ResultActivity.class);
        intent.putParcelableArrayListExtra(Const.BITMAP_RESULT_LIST, resultList);

        if (bitmapList.size() != 0){
            if (countTranslate < bitmapList.size()){
                if (isLatin){
                    detectText();
                }else{
                    detectNLatin();
                }
            }else{
//                startActivity(intent);
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
