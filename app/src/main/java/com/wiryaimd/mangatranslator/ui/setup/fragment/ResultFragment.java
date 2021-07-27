package com.wiryaimd.mangatranslator.ui.setup.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wiryaimd.mangatranslator.R;
import com.wiryaimd.mangatranslator.ui.setup.fragment.adapter.ResultAdapter;
import com.wiryaimd.mangatranslator.ui.setup.SetupViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ResultFragment extends Fragment {

    private static final String TAG = "ResultActivity";

    private SetupViewModel setupViewModel;

    private RecyclerView recyclerView;

    private ArrayList<Bitmap> bitmapList;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        setupViewModel = new ViewModelProvider(requireActivity()).get(SetupViewModel.class);

        recyclerView = view.findViewById(R.id.result_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(setupViewModel.getApplication()));

        bitmapList = setupViewModel.getBitmapListLiveData().getValue();
        if (bitmapList == null || bitmapList.size() == 0){
            requireActivity().finish();
            Toast.makeText(setupViewModel.getApplication(), "Cannot load bitmap data", Toast.LENGTH_LONG).show();
            return;
        }

        Log.d(TAG, "onCreate: boom biaahahahhah aye");

        ResultAdapter adapter = new ResultAdapter(setupViewModel.getApplication());
        recyclerView.setAdapter(adapter);
        adapter.setBitmapList(bitmapList);

    }
}
