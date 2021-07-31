package com.wiryaimd.mangatranslator.ui.setup.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wiryaimd.mangatranslator.R;
import com.wiryaimd.mangatranslator.ui.main.MainActivity;
import com.wiryaimd.mangatranslator.ui.setup.fragment.adapter.ResultAdapter;
import com.wiryaimd.mangatranslator.ui.setup.SetupViewModel;
import com.wiryaimd.mangatranslator.ui.setup.fragment.dialog.SelectSaveDialog;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ResultFragment extends Fragment {

    private static final String TAG = "ResultActivity";

    private SetupViewModel setupViewModel;

    private RecyclerView recyclerView;
    private TextView tvinfo;

    private ExtendedFloatingActionButton fabSave;

    private ArrayList<Bitmap> bitmapList;

    private Timer timer;
    private int count = 5;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        setupViewModel = new ViewModelProvider(requireActivity()).get(SetupViewModel.class);

        tvinfo = view.findViewById(R.id.result_tvinfo);
        fabSave = view.findViewById(R.id.result_fabsave);
        recyclerView = view.findViewById(R.id.result_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(setupViewModel.getApplication()));

        bitmapList = setupViewModel.getBitmapListLiveData().getValue();
        if (bitmapList == null || bitmapList.size() == 0){
            requireActivity().finish();
            Toast.makeText(setupViewModel.getApplication(), "Cannot load bitmap data", Toast.LENGTH_LONG).show();
            return;
        }

        ResultAdapter adapter = new ResultAdapter(setupViewModel.getApplication());
        recyclerView.setAdapter(adapter);
        adapter.setBitmapList(bitmapList);

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                count -= 1;
                Log.d(TAG, "run: cek ads: " + count);
                setupViewModel.getAdsCount().postValue(count);
            }
        }, 1000, 1000);

        setupViewModel.getAdsCount().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer > 0){
                    tvinfo.setText(("Ads will shown in " + integer));
                }else{
                    count = 5;
                    tvinfo.setVisibility(View.GONE);
                    timer.cancel();

                    if (MainActivity.interstitialAd.isReady()){
                        MainActivity.interstitialAd.showAd();
                    }
                }
            }
        });

        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SelectSaveDialog().show(getParentFragmentManager(), "SELECT_SAVE_DIALOG");
            }
        });

    }
}
