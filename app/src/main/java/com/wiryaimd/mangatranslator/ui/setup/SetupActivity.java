package com.wiryaimd.mangatranslator.ui.setup;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.translate.TranslateRemoteModel;
import com.wiryaimd.mangatranslator.R;
import com.wiryaimd.mangatranslator.model.InfoModel;
import com.wiryaimd.mangatranslator.model.SelectedModel;
import com.wiryaimd.mangatranslator.ui.MainViewModel;
import com.wiryaimd.mangatranslator.ui.setup.adapter.InfoAdapter;
import com.wiryaimd.mangatranslator.ui.setup.adapter.SelectAdapter;
import com.wiryaimd.mangatranslator.util.LanguagesData;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SetupActivity extends AppCompatActivity {

    private static final String TAG = "SetupActivity";

    private MainViewModel mainViewModel;

    private Button btnrequire;
    private ImageView imgalert;

    private Spinner spinFrom, spinTo;

    private ViewPager viewPager;
    private RecyclerView recyclerView;

    private List<SelectedModel> selectedList = new ArrayList<>();
    private List<String> downloadedList = new ArrayList<>();

    private RemoteModelManager remoteModelManager;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setuplang);

        remoteModelManager = RemoteModelManager.getInstance();

        btnrequire = findViewById(R.id.setuplang_downloadlanguages);
        imgalert = findViewById(R.id.setuplang_imgalert);
        spinFrom = findViewById(R.id.setuplang_spinfrom);
        spinTo = findViewById(R.id.setuplang_spinto);
        recyclerView = findViewById(R.id.setuplang_recyclerview);
        viewPager = findViewById(R.id.setuplang_viewpager);

        // oke ternyatod data yang tersimpan pada viewmodel berbeda ya tod tiap activity nya tod kentod
        mainViewModel = new ViewModelProvider(SetupActivity.this).get(MainViewModel.class);

        List<InfoModel> infoList = new ArrayList<>();
        infoList.add(new InfoModel("For non-latin languages", "Currently, for non-latin comic language need to select the text manually, if you want automatic translate you can translate the latin comic e.g Manga in english, france etc"));
        infoList.add(new InfoModel("Download Models", "Before translate Manga/Manhwa/Manhua you need to download model languages which languages selected for translating process"));
        InfoAdapter infoAdapter = new InfoAdapter(SetupActivity.this, infoList);

        viewPager.setAdapter(infoAdapter);
        viewPager.setPadding(24, 0, 160, 0);

        SelectAdapter selectAdapter = new SelectAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(mainViewModel.getApplication()));
        recyclerView.setAdapter(selectAdapter);

        ArrayAdapter<String> spinnerFromAdapter = new ArrayAdapter<>(SetupActivity.this, R.layout.item_spinner, R.id.spinner_language, LanguagesData.flag_from);
        spinFrom.setAdapter(spinnerFromAdapter);
        ArrayAdapter<String> spinnerToAdapter = new ArrayAdapter<>(SetupActivity.this, R.layout.item_spinner, R.id.spinner_language, LanguagesData.flag_to);
        spinTo.setAdapter(spinnerToAdapter);

        mainViewModel.getSelectedModelLiveData().observe(SetupActivity.this, new Observer<List<SelectedModel>>() {
            @Override
            public void onChanged(List<SelectedModel> selectedModels) {
                Log.d(TAG, "onChanged: observer selected");
                selectAdapter.setSelectedList(selectedList);
            }
        });

        mainViewModel.getDownloadedModelsLiveData().observe(SetupActivity.this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                Log.d(TAG, "onChanged: observe downloaded models");
                downloadedList = strings;
            }
        });

        mainViewModel.getFlagFromLiveData().observe(SetupActivity.this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                Log.d(TAG, "onChanged: observe flag from");
                checkRequireDownload(integer, 0);
                if (!downloadedFrom || !downloadedTo){
                    btnrequire.setVisibility(View.VISIBLE);
                    imgalert.setVisibility(View.VISIBLE);
                }else{
                    Log.d(TAG, "onChanged: crot");
                    btnrequire.setVisibility(View.GONE);
                    imgalert.setVisibility(View.GONE);
                }
            }
        });

        mainViewModel.getFlagToLiveData().observe(SetupActivity.this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                Log.d(TAG, "onChanged: observe flag to");
                checkRequireDownload(integer, 1);
                if (!downloadedFrom || !downloadedTo){
                    btnrequire.setVisibility(View.VISIBLE);
                    imgalert.setVisibility(View.VISIBLE);
                }else{
                    Log.d(TAG, "onChanged: crot");
                    btnrequire.setVisibility(View.GONE);
                    imgalert.setVisibility(View.GONE);
                }
            }
        });

        spinFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mainViewModel.getFlagFromLiveData().setValue(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mainViewModel.getFlagToLiveData().setValue(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private boolean downloadedFrom = true;
    private boolean downloadedTo = true;

    public void checkRequireDownload(int pos, int type){
        Log.d(TAG, "checkRequireDownload: pos " + pos);
        if (pos == 0 && type == 0){
            downloadedFrom = true;
            return;
        }else if(pos == 0 && type == 1){
            downloadedTo = true;
            return;
        }

        String data;
        if (type == 0) {
            data = LanguagesData.flag_code_from[pos];
        } else{
            data = LanguagesData.flag_code[pos];
        }
        Log.d(TAG, "checkRequireDownload: need: " + data);
        Log.d(TAG, "checkRequireDownload: size: " + downloadedList.size());
        for (String lang : downloadedList) {
            Log.d(TAG, "checkRequireDownload: d: " + lang);
            if (data.equalsIgnoreCase(lang)) {
                if (type == 0) {
                    downloadedFrom = true;
                } else {
                    downloadedTo = true;
                }
                return;
            }
        }

        if (type == 0) {
            downloadedFrom = false;
        }else {
            downloadedTo = false;
        }
    }
}
