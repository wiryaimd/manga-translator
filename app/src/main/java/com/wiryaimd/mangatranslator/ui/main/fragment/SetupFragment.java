package com.wiryaimd.mangatranslator.ui.main.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.wiryaimd.mangatranslator.R;
import com.wiryaimd.mangatranslator.model.InfoModel;
import com.wiryaimd.mangatranslator.model.SelectedModel;
import com.wiryaimd.mangatranslator.ui.main.MainViewModel;
import com.wiryaimd.mangatranslator.ui.main.fragment.adapter.InfoAdapter;
import com.wiryaimd.mangatranslator.ui.main.fragment.adapter.SelectAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SetupFragment extends Fragment {

    private MainViewModel mainViewModel;

    private ViewPager viewPager;
    private RecyclerView recyclerView;

    private List<SelectedModel> selectedList;

    public SetupFragment(List<SelectedModel> selectedList) {
        this.selectedList = selectedList;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setuplang, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        recyclerView = view.findViewById(R.id.setuplang_recyclerview);
        viewPager = view.findViewById(R.id.setuplang_viewpager);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        List<InfoModel> infoList = new ArrayList<>();
        infoList.add(new InfoModel("For non-latin languages", "Currently, for non-latin comic language need to select the text manually, if you want automatic translate you can translate the latin comic e.g Manga in english, france etc"));
        infoList.add(new InfoModel("Download Models", "Before translate Manga/Manhwa/Manhua you need to download model languages which languages selected for translating process"));
        InfoAdapter infoAdapter = new InfoAdapter(requireContext(), infoList);

        viewPager.setAdapter(infoAdapter);
        viewPager.setPadding(24, 0, 160, 0);

        SelectAdapter selectAdapter = new SelectAdapter(selectedList);
        recyclerView.setLayoutManager(new LinearLayoutManager(mainViewModel.getApplication()));
        recyclerView.setAdapter(selectAdapter);

    }
}
