package com.wiryaimd.mangatranslator.ui.setup.fragment.dialog;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.wiryaimd.mangatranslator.R;
import com.wiryaimd.mangatranslator.model.SelectedModel;
import com.wiryaimd.mangatranslator.ui.setup.SetupViewModel;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.Executors;

public class SaveDialog extends DialogFragment {

    private static final String TAG = "SaveDialog";

    private SetupViewModel setupViewModel;
    
    private ArrayList<Bitmap> bitmapList;

    public static final int CODE_SUCCESS = 10;
    public static final int CODE_ERROR = 1;

    private SelectedModel.Type type;

    public SaveDialog(SelectedModel.Type type) {
        this.type = type;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        if (getDialog() != null){
            getDialog().requestWindowFeature(STYLE_NO_TITLE);
        }
        return inflater.inflate(R.layout.dialog_save, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        
        setupViewModel = new ViewModelProvider(requireActivity()).get(SetupViewModel.class);

        bitmapList = setupViewModel.getBitmapListLiveData().getValue();
        if (bitmapList == null || bitmapList.size() == 0){
            Toast.makeText(setupViewModel.getApplication(), "Cannot load bitmap, please try again", Toast.LENGTH_SHORT).show();
            if (getDialog() != null) getDialog().dismiss();
            return;
        }
        
        if (type == SelectedModel.Type.IMAGE){
            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        saveImage(0);
                    } catch (IOException e) {
                        setupViewModel.getSaveCodeLiveData().postValue(1);
                    }
                }
            });
        }else{
            savePdf();
        }

        setupViewModel.getSaveCodeLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer == CODE_SUCCESS){
                    setupViewModel.getSaveCodeLiveData().setValue(0);

                    Toast.makeText(setupViewModel.getApplication(), "Saved! sdcard/MangaTranslator", Toast.LENGTH_SHORT).show();
                    if (getDialog() != null) getDialog().dismiss();
                }else if(integer == CODE_ERROR){
                    Toast.makeText(setupViewModel.getApplication(), "Cannot save image, idk why", Toast.LENGTH_SHORT).show();
                    if (getDialog() != null) getDialog().dismiss();
                }
            }
        });
    }

    public void savePdf(){
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                PdfDocument pdfDocument = new PdfDocument();
                saveBitmap(pdfDocument, 0);
            }
        });
    }

    public void saveBitmap(PdfDocument pdfDocument, int index){

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmapList.get(index).getWidth(), bitmapList.get(index).getHeight(), 1).create();

        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        canvas.drawBitmap(bitmapList.get(index), 0, 0, null);
        pdfDocument.finishPage(page);

//        bitmapList.get(index).recycle();

        if ((index + 1) < bitmapList.size()){
            saveBitmap(pdfDocument, (index + 1));
        }else{
            File dir = new File(Environment.getExternalStorageDirectory().toString() + "/MangaTranslator/pdf");
            dir.mkdirs();
            String filename;

            filename = "mngTranslator-PDF-" + UUID.randomUUID().toString() + ".pdf";

            File file = new File(dir, filename);
            if (file.exists()){
                file.delete();
            }

            try {
                FileOutputStream fos = new FileOutputStream(file);
                pdfDocument.writeTo(fos);
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            pdfDocument.close();

            setupViewModel.getSaveCodeLiveData().postValue(CODE_SUCCESS);
        }
    }
    
    public void saveImage(int index) throws IOException {
        File dir = new File(Environment.getExternalStorageDirectory().toString());
        dir.mkdirs();
        String filename;

        filename = "mngTranslator-IMG-" + UUID.randomUUID().toString() + "(" + index + ")"  + ".jpg";

        File file = new File(dir, filename);
        if (file.exists()){
            file.delete();
        }

        FileOutputStream fos = new FileOutputStream(file);
        bitmapList.get(index).compress(Bitmap.CompressFormat.JPEG, 90, fos);
        fos.flush();
        fos.close();

//        bitmapList.get(index).recycle();

        Log.d(TAG, "saveBitmap: boom bieac hahahah ihihi ahyuuu + " + index);

        if ((index + 1) < bitmapList.size()){
            saveImage((index + 1));
        }else{
            setupViewModel.getSaveCodeLiveData().postValue(CODE_SUCCESS);
        }
        
    }
}
