package com.wiryaimd.mangatranslator.ui.main.fragment.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.wiryaimd.mangatranslator.R;
import com.wiryaimd.mangatranslator.ui.premium.PremiumActivity;

import org.jetbrains.annotations.NotNull;

public class MenuDialog extends DialogFragment {

    private static final String TAG = "MenuDialog";

    private Button btnUpgrade, btnReport;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        if (getDialog() != null){
            getDialog().requestWindowFeature(STYLE_NO_TITLE);
        }
        return inflater.inflate(R.layout.dialog_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        btnUpgrade = view.findViewById(R.id.menu_upgrade);
        btnReport = view.findViewById(R.id.menu_reportbug);

        btnUpgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireActivity(), PremiumActivity.class));
                if (getDialog() != null){
                    getDialog().dismiss();
                }
            }
        });
    }
}
