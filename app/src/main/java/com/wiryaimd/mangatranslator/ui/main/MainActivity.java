package com.wiryaimd.mangatranslator.ui.main;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wiryaimd.mangatranslator.BaseApplication;
import com.wiryaimd.mangatranslator.R;
import com.wiryaimd.mangatranslator.model.SelectedModel;
import com.wiryaimd.mangatranslator.ui.main.fragment.SelectFragment;
import com.wiryaimd.mangatranslator.ui.main.fragment.dialog.MenuDialog;
import com.wiryaimd.mangatranslator.ui.premium.PremiumActivity;
import com.wiryaimd.mangatranslator.ui.setup.SetupActivity;
import com.wiryaimd.mangatranslator.ui.main.fragment.dialog.SelectDialog;
import com.wiryaimd.mangatranslator.ui.setup.fragment.dialog.InfoDialog;
import com.wiryaimd.mangatranslator.util.Const;
import com.wiryaimd.mangatranslator.util.PermissionHelper;
import com.wiryaimd.mangatranslator.util.translator.GApiTranslate;
import com.wiryaimd.mangatranslator.util.vision.GVision;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private MainViewModel mainViewModel;

    private DatabaseReference dbref;

    private TextView tvinfo;

    private Toolbar toolbar;

    private ActivityResultLauncher<String> launcherImg = registerForActivityResult(new ActivityResultContracts.GetMultipleContents(), new ActivityResultCallback<List<Uri>>() {
        @Override
        public void onActivityResult(List<Uri> result) {

            if(result.size() == 0) {
                return;
            }
            
            if (result.size() > 10){
                Toast.makeText(MainActivity.this, "Maximum image 10", Toast.LENGTH_LONG).show();
                return;
            }

            ArrayList<SelectedModel> selectedList = new ArrayList<>();
            HashMap<String, Uri> resultMap = new HashMap<>();

            for (Uri uri : result){
                Log.d(TAG, "onActivityResult: uri result: " + uri.toString());

                // get file name brohhh
                if (!uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
                    String nameFile = new File(uri.getPath()).getName();
                    Log.d(TAG, "onActivityResult: namefile: " + nameFile);

                    resultMap.put(nameFile, uri);
                }else {
                    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                    cursor.moveToFirst();
                    int indexCursor = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);

                    String nameContent = cursor.getString(indexCursor);
                    Log.d(TAG, "onActivityResult: name content: " + nameContent);

                    resultMap.put(nameContent, uri);
                }
            }

            // sort hashmap
            List<String> sortList = new ArrayList<>(resultMap.keySet());
            Collections.sort(sortList);

            for (String key : sortList){
                Log.d(TAG, "onActivityResult: sorted key: " + key);
                selectedList.add(new SelectedModel(key, resultMap.get(key), SelectedModel.Type.IMAGE));
            }

            Intent intent = new Intent(MainActivity.this, SetupActivity.class);
            intent.putParcelableArrayListExtra(Const.SELECTED_LIST, selectedList);
            startActivity(intent);
        }
    });

    private ActivityResultLauncher<String> launcherPdf = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {

            if (result == null){
                return;
            }

            ArrayList<SelectedModel> selectedList = new ArrayList<>();

            Cursor cursor = getContentResolver().query(result, null, null, null, null);
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            cursor.moveToFirst();
            selectedList.add(new SelectedModel(cursor.getColumnName(nameIndex), result, SelectedModel.Type.PDF));

            Intent intent = new Intent(MainActivity.this, SetupActivity.class);
            intent.putExtra(Const.SELECTED_LIST, selectedList);
            startActivity(intent);
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_MangaTranslator);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.main_toolbar);
        tvinfo = findViewById(R.id.main_tvinfo);
        tvinfo.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        tvinfo.setMarqueeRepeatLimit(-1);
        tvinfo.setSingleLine(true);
        tvinfo.setSelected(true);

        setSupportActionBar(toolbar);
        mainViewModel = new ViewModelProvider(MainActivity.this).get(MainViewModel.class);

        BaseApplication baseApplication = mainViewModel.getApplication();
        baseApplication.initApplovin(MainActivity.this);

        FirebaseApp.initializeApp(MainActivity.this);

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

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new SelectFragment());
        ft.commit();

        if (!checkConnection()){
            new InfoDialog("Connection Info", "Please check your connection before start translate", false).show(getSupportFragmentManager(), "CONNECTION_CHECK");
        }

//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(MainActivity.this, PremiumActivity.class));
//            }
//        });

        dbref = FirebaseDatabase.getInstance().getReference().child("mangaTranslator");

        dbref.child("info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String info = snapshot.getValue(String.class);
                tvinfo.setText(info);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (PermissionHelper.PERM_CODE == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mainViewModel.getOpenFile().openImage();
//                new SelectDialog().show(getSupportFragmentManager(), "SELECT_DIALOG");
            } else {
                Log.d(TAG, "onActivityResult: Permission result DENIED brohh");
            }
        }
    }

    public boolean checkConnection() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
        }catch (Exception e){
            return false;
        }
    }
}