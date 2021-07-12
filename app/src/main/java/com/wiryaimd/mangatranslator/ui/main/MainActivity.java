package com.wiryaimd.mangatranslator.ui.main;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.TranslateRemoteModel;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.wiryaimd.mangatranslator.R;
import com.wiryaimd.mangatranslator.model.SelectedModel;
import com.wiryaimd.mangatranslator.ui.main.fragment.SelectFragment;
import com.wiryaimd.mangatranslator.ui.main.fragment.SetupFragment;
import com.wiryaimd.mangatranslator.ui.main.fragment.dialog.SelectDialog;
import com.wiryaimd.mangatranslator.util.PermissionHelper;
import com.wiryaimd.mangatranslator.util.RealPath;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private MainViewModel mainViewModel;

    private ActivityResultLauncher<String> launcherImg = registerForActivityResult(new ActivityResultContracts.GetMultipleContents(), new ActivityResultCallback<List<Uri>>() {
        @Override
        public void onActivityResult(List<Uri> result) {
            List<SelectedModel> selectedList = new ArrayList<>();
            for (Uri uri : result){
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                cursor.moveToFirst();
                selectedList.add(new SelectedModel(cursor.getColumnName(nameIndex), uri, SelectedModel.Type.IMAGE));
            }
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new SetupFragment(selectedList));
            ft.commit();

            //                // get bitmap
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    });

    private ActivityResultLauncher<String> launcherPdf = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            List<Bitmap> bitmapList = new ArrayList<>();
            try {
                File file = RealPath.from(MainActivity.this, result);
                PdfRenderer pdfRenderer = new PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY));
                Bitmap bitmap;
                Log.d(TAG, "onActivityResult: count: " + pdfRenderer.getPageCount());
                for (int i = 0; i < pdfRenderer.getPageCount(); i++) {
                    PdfRenderer.Page page = pdfRenderer.openPage(i);
                    int width = getResources().getDisplayMetrics().densityDpi / 72 * page.getWidth();
                    int height = getResources().getDisplayMetrics().densityDpi / 72 * page.getHeight();
                    bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//                    Canvas canvas = new Canvas(bitmap);
//                    canvas.drawColor(Color.WHITE);
//                    canvas.drawBitmap(bitmap, 0, 0, null);
//                    Rect r = new Rect(0, 0, width, height);
//                    page.render(bitmap, r, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
//                    bitmapList.add(bitmap);
                    page.close();
                }
                pdfRenderer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
//            Glide.with(MainActivity.this).load(bitmapList.get(3)).into(demoimg);
            Log.d(TAG, "onActivityResult: asuuuu");
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TranslateRemoteModel translateRemoteModel = new TranslateRemoteModel.Builder(TranslateLanguage.INDONESIAN).build();

        RemoteModelManager remoteModelManager = RemoteModelManager.getInstance();

        Log.d(TAG, "onCreate: cekk");

        remoteModelManager.getDownloadedModels(TranslateRemoteModel.class).addOnSuccessListener(new OnSuccessListener<Set<TranslateRemoteModel>>() {
            @Override
            public void onSuccess(@NonNull @NotNull Set<TranslateRemoteModel> translateRemoteModels) {
                for (TranslateRemoteModel model : translateRemoteModels) {
                    Log.d(TAG, "onSuccess: hemm: " + model.getLanguage());
                    Log.d(TAG, "onSuccess: mod name: " + model.getModelName());
                }
            }
        });

        TranslatorOptions options = new TranslatorOptions.Builder().setSourceLanguage(TranslateLanguage.JAPANESE).setTargetLanguage(TranslateLanguage.INDONESIAN).build();
        Translator translator = Translation.getClient(options);

        DownloadConditions downloadConditions = new DownloadConditions.Builder().build();
        Log.d(TAG, "onCreate: downloading");
        // download brohh

        translator.downloadModelIfNeeded(downloadConditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(@NonNull @NotNull Void unused) {
                remoteModelManager.getDownloadedModels(TranslateRemoteModel.class).addOnSuccessListener(new OnSuccessListener<Set<TranslateRemoteModel>>() {
                    @Override
                    public void onSuccess(@NonNull @NotNull Set<TranslateRemoteModel> translateRemoteModels) {
                        for (TranslateRemoteModel model : translateRemoteModels) {
                            Log.d(TAG, "onSuccess: hemm: " + model.getLanguage());
                            Log.d(TAG, "onSuccess: mod name: " + model.getModelName());
                        }
                    }
                });
            }
        });

//        remoteModelManager.download(translateRemoteModel, downloadConditions).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(@NonNull @NotNull Void unused) {
//
//            }
//        });

        mainViewModel = new ViewModelProvider(MainActivity.this).get(MainViewModel.class);
        MainViewModel.OpenFile openFile = new MainViewModel.OpenFile() {
            @Override
            public void openImage() {
//                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                intent.setType("image/*");
//                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                launcherImg.launch("image/*");
            }

            @Override
            public void openPdf() {
                launcherPdf.launch("application/pdf");
            }
        };
        mainViewModel.setOpenFile(openFile);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new SelectFragment());
        ft.commit();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (PermissionHelper.PERM_CODE == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new SelectDialog().show(getSupportFragmentManager(), "SELECT_DIALOG");
            } else {
                Log.d(TAG, "onActivityResult: Permission result DENIED brohh");
            }
        }
    }
}