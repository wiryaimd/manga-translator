package com.wiryaimd.mangatranslator.ui.setup.fragment.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.wiryaimd.mangatranslator.R;

import org.jetbrains.annotations.NotNull;

public class InfoDialog extends DialogFragment {

    private TextView tvtitle, tvdesc;
    private Button btnok;

    private boolean isProcess;
    private String title, message;

    public InfoDialog(String title, String message, boolean isProcess) {
        this.title = title;
        this.message = message;
        this.isProcess = isProcess;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        if (getDialog() != null) getDialog().requestWindowFeature(STYLE_NO_TITLE);
        return inflater.inflate(R.layout.dialog_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        tvtitle = view.findViewById(R.id.infodialog_item_title);
        tvdesc = view.findViewById(R.id.infodialog_item_desc);
        btnok = view.findViewById(R.id.infodialog_item_ok);

        tvtitle.setText(title);
        tvdesc.setText(message);

        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isProcess) new ProcessDialog().show(getParentFragmentManager(), "PROCESS_FRAGMENT");
                if (getDialog() != null) getDialog().dismiss();
            }
        });

    }
}
