package com.wiryaimd.mangatranslator.ui.main;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wiryaimd.mangatranslator.R;
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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private MainViewModel mainViewModel;

    private ActivityResultLauncher<String> launcherImg = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            Log.d(TAG, "onActivityResult: cekkk");
            // get bitmap
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            Glide.with(MainActivity.this).load(result).into(demoimg);
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

    private ImageView imgselect, demoimg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgselect = findViewById(R.id.main_select);
        demoimg = findViewById(R.id.main_demoimg);

        mainViewModel = new ViewModelProvider(MainActivity.this).get(MainViewModel.class);
        MainViewModel.OpenFile openFile = new MainViewModel.OpenFile() {
            @Override
            public void openImage() {
                launcherImg.launch("image/*");
            }

            @Override
            public void openPdf() {
                launcherPdf.launch("application/pdf");
            }
        };
        mainViewModel.setOpenFile(openFile);

        imgselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isAllow = PermissionHelper.requestPermission(MainActivity.this);
                if (isAllow){
                    new SelectDialog().show(getSupportFragmentManager(), "SELECT_DIALOG_MAIN");
                }
            }
        });

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