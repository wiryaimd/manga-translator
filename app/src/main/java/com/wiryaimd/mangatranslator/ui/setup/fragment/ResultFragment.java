package com.wiryaimd.mangatranslator.ui.setup.fragment;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wiryaimd.mangatranslator.BaseApplication;
import com.wiryaimd.mangatranslator.R;
import com.wiryaimd.mangatranslator.ui.main.MainActivity;
import com.wiryaimd.mangatranslator.ui.setup.fragment.adapter.ResultAdapter;
import com.wiryaimd.mangatranslator.ui.setup.SetupViewModel;
import com.wiryaimd.mangatranslator.ui.setup.fragment.dialog.SelectSaveDialog;
import com.wiryaimd.mangatranslator.util.Const;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ResultFragment extends Fragment {

    private static final String TAG = "ResultActivity";

    private SetupViewModel setupViewModel;

    private ViewPager2 viewPager;
    private TextView tvinfo;

    private ExtendedFloatingActionButton fabSave;

    private ArrayList<Bitmap> bitmapList;

    private ImageView imgprev, imgnext;

    private Timer timer;
    private int count = 1;

    private int countPage = 0;

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
        viewPager = view.findViewById(R.id.result_viewpager);
        imgprev = view.findViewById(R.id.result_prev);
        imgnext = view.findViewById(R.id.result_next);

        bitmapList = setupViewModel.getBitmapListLiveData().getValue();
        if (bitmapList == null || bitmapList.size() == 0){
            requireActivity().finish();
            Toast.makeText(setupViewModel.getApplication(), "Cannot load bitmap data, select another image", Toast.LENGTH_LONG).show();
            return;
        }

        BaseApplication baseApplication = setupViewModel.getApplication();

        if (setupViewModel.getTeLiveData().getValue() == SetupViewModel.TranslateEngine.USING_MS) {
            baseApplication.saveTlMS(bitmapList.size());
        }else{
            baseApplication.saveTl(bitmapList.size());
        }

        viewPager.setUserInputEnabled(false);

        ResultAdapter adapter = new ResultAdapter(setupViewModel.getApplication());
        viewPager.setAdapter(adapter);
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

                    if (!baseApplication.isSubscribe()) {
                        if (baseApplication.getInterstitialAd().isReady() && baseApplication.getCountAds() == 0) {
                            Log.d(TAG, "onChanged: ads ready: ");
                            baseApplication.getInterstitialAd().showAd();
                        }else{
                            if (baseApplication.getInterstitialAdAdmob() != null){
                                baseApplication.getInterstitialAdAdmob().show(requireActivity());
                            }
                        }
                    }
                }
            }
        });

        setupViewModel.getCountPage().setValue(0);

        setupViewModel.getCountPage().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer == bitmapList.size() - 1){
                    imgnext.setVisibility(View.GONE);
                }else{
                    imgnext.setVisibility(View.VISIBLE);
                }

                if (integer == 0){
                    imgprev.setVisibility(View.GONE);
                }else{
                    imgprev.setVisibility(View.VISIBLE);
                }

                viewPager.setCurrentItem(integer);
            }
        });

        imgprev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countPage -= 1;
                setupViewModel.getCountPage().setValue(countPage);
            }
        });

        imgnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countPage += 1;
                setupViewModel.getCountPage().setValue(countPage);
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
