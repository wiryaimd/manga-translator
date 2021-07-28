package com.wiryaimd.mangatranslator.ui.setup.fragment.dialog;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.load.resource.bitmap.ParcelFileDescriptorBitmapDecoder;
import com.wiryaimd.mangatranslator.R;
import com.wiryaimd.mangatranslator.ui.setup.SetupViewModel;
import com.wiryaimd.mangatranslator.util.RealPath;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LoadPDFDialog extends DialogFragment {

    private static final String TAG = "LoadPDFDialog";

    private SetupViewModel setupViewModel;

    private Uri uri;
    private List<Integer> ignoredIndex;

    public LoadPDFDialog(Uri uri, List<Integer> ignoredIndex) {
        this.uri = uri;
        this.ignoredIndex = ignoredIndex;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_infoloading, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        setupViewModel = new ViewModelProvider(requireActivity()).get(SetupViewModel.class);

        setupViewModel.getPdfListLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<Bitmap>>() {
            @Override
            public void onChanged(ArrayList<Bitmap> bitmaps) {
                if (bitmaps.size() != 0) {
                    new ProcessDialog().show(getParentFragmentManager(), "PROCESS_FRAGMENT_SETUP");
                }else{
                    Toast.makeText(setupViewModel.getApplication(), "Your pdf is empty", Toast.LENGTH_SHORT).show();
                }
                if (getDialog() != null) getDialog().dismiss();
            }
        });

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                ArrayList<Bitmap> bitmapList = new ArrayList<>();

                try {
                    File file = RealPath.from(setupViewModel.getApplication(), uri);

                    PdfRenderer pdfRenderer = new PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY));
                    Bitmap bitmap;

                    Log.d(TAG, "onActivityResult: count: " + pdfRenderer.getPageCount());
                    floop: for (int i = 0; i < pdfRenderer.getPageCount(); i++) {
                        for (int j = 0; j < ignoredIndex.size(); j++) {
                            if (i == (ignoredIndex.get(j) - 1)){
                                continue floop;
                            }
                        }
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

                setupViewModel.getPdfListLiveData().postValue(bitmapList);
            }
        });
    }
}
