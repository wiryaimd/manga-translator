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

public class SaveDialog extends DialogFragment {

    private static final String TAG = "SaveDialog";

    private SetupViewModel setupViewModel;
    
    private ArrayList<Bitmap> bitmapList;

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
            try {
                saveImage(0);
            } catch (IOException e) {
                Toast.makeText(setupViewModel.getApplication(), "Cannot save bitmap, idk why", Toast.LENGTH_SHORT).show();
            }
        }else{
            savePdf();
        }
    }

    public void savePdf(){
        int maxWidth = 0, maxHeight = 0;

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);

        for (int i = 0; i < bitmapList.size(); i++) {
            if (bitmapList.get(i).getWidth() > maxWidth){
                maxWidth = bitmapList.get(i).getWidth();
            }
            if (bitmapList.get(i).getHeight() > maxHeight){
                maxHeight = bitmapList.get(i).getHeight();
            }
        }

        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(maxWidth, maxHeight, bitmapList.size()).create();

        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        canvas.drawPaint(paint);

        saveBitmap(pdfDocument, page, canvas, 0, maxHeight);
    }

    public void saveBitmap(PdfDocument pdfDocument, PdfDocument.Page page, Canvas canvas, int index, int maxHeight){
        int currentHeight = 0;
        for (int i = 0; i < index; i++) {
            currentHeight += maxHeight;
        }
        canvas.drawBitmap(bitmapList.get(index), 0, currentHeight, null);

        if ((index + 1) < bitmapList.size()){
            saveBitmap(pdfDocument, page, canvas, (index + 1), maxHeight);
        }else{

            pdfDocument.finishPage(page);

            File dir = new File(Environment.getExternalStorageDirectory().toString() + "/MangaTranslator");
            dir.mkdirs();
            String filename;

            filename = "mngTranslator-PDF-" + UUID.randomUUID().toString() + "(" + index + ")"  + ".pdf";

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

            Toast.makeText(setupViewModel.getApplication(), "PDF Saved!", Toast.LENGTH_SHORT).show();
            if (getDialog() != null) getDialog().dismiss();
        }
    }
    
    public void saveImage(int index) throws IOException {
        File dir = new File(Environment.getExternalStorageDirectory().toString() + "/MangaTranslator");
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
            Toast.makeText(setupViewModel.getApplication(), "Image Saved!", Toast.LENGTH_SHORT).show();
            if (getDialog() != null) getDialog().dismiss();
        }
        
    }
}
