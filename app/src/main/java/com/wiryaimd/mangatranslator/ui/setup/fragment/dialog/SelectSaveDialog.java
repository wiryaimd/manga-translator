package com.wiryaimd.mangatranslator.ui.setup.fragment.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.wiryaimd.mangatranslator.R;
import com.wiryaimd.mangatranslator.model.SelectedModel;
import com.wiryaimd.mangatranslator.ui.setup.SetupViewModel;

import org.jetbrains.annotations.NotNull;

public class SelectSaveDialog extends DialogFragment {

    private SetupViewModel setupViewModel;

    private LinearLayout linearImage, linearPdf;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        if (getDialog() != null){
            getDialog().requestWindowFeature(STYLE_NO_TITLE);
        }
        return inflater.inflate(R.layout.dialog_select, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        setupViewModel = new ViewModelProvider(requireActivity()).get(SetupViewModel.class);

        linearImage = view.findViewById(R.id.select_linear_image);
        linearPdf = view.findViewById(R.id.select_linear_pdf);

        linearImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    new SaveDialog(SelectedModel.Type.IMAGE).show(getParentFragmentManager(), "SAVE_DIALOG");
                }catch (RuntimeException e){
                    Toast.makeText(setupViewModel.getApplication(), "Try Again To Save", Toast.LENGTH_LONG).show();
                }
                if (getDialog() != null){
                    getDialog().dismiss();
                }
            }
        });

        linearPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    new SaveDialog(SelectedModel.Type.PDF).show(getParentFragmentManager(), "SAVE_DIALOG");
                }catch (RuntimeException e){
                    Toast.makeText(setupViewModel.getApplication(), "Try Again To Save", Toast.LENGTH_LONG).show();
                }
                if (getDialog() != null){
                    getDialog().dismiss();
                }
            }
        });


    }
}
