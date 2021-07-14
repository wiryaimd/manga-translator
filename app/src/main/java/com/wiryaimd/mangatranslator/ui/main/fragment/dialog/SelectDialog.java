package com.wiryaimd.mangatranslator.ui.main.fragment.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.wiryaimd.mangatranslator.R;
import com.wiryaimd.mangatranslator.ui.main.MainViewModel;

import org.jetbrains.annotations.NotNull;

public class SelectDialog extends DialogFragment {

    private MainViewModel mainViewModel;

    private LinearLayout selectImg, selectPdf;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        if (getDialog() != null){
            getDialog().requestWindowFeature(STYLE_NO_TITLE);
            getDialog().getWindow().setDimAmount(0);
            getDialog().getWindow().setGravity(Gravity.BOTTOM);
        }
        return inflater.inflate(R.layout.dialog_select, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        selectImg = view.findViewById(R.id.select_linear_image);
        selectPdf = view.findViewById(R.id.select_linear_pdf);

        selectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainViewModel.getOpenFile().openImage();
                if (getDialog() != null) getDialog().dismiss();
            }
        });

        selectPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainViewModel.getOpenFile().openPdf();
                if (getDialog() != null) getDialog().dismiss();
            }
        });

    }
}
