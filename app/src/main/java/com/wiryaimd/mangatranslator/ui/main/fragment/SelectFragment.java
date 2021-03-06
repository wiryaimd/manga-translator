package com.wiryaimd.mangatranslator.ui.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.wiryaimd.mangatranslator.R;
import com.wiryaimd.mangatranslator.ui.main.MainViewModel;
import com.wiryaimd.mangatranslator.ui.main.fragment.dialog.SelectDialog;
import com.wiryaimd.mangatranslator.ui.premium.PremiumActivity;
import com.wiryaimd.mangatranslator.util.PermissionHelper;

import org.jetbrains.annotations.NotNull;

public class SelectFragment extends Fragment {

    private MainViewModel mainViewModel;

    private ImageView imgprem;
    private Button btnselect;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        btnselect = view.findViewById(R.id.selectf_btnselect);
//        imgprem = view.findViewById(R.id.selectf_imgprem);

        btnselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isAllow = PermissionHelper.requestPermission(requireActivity());
                if (isAllow){
                    // SELECT IMAGE DIRECTLY
                    mainViewModel.getOpenFile().openImage();

                    // SELECT IMAGE OR PDF
                    // new SelectDialog().show(getParentFragmentManager(), "SELECT_DIALOG_F");
                }
            }
        });

//        imgprem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                requireActivity().startActivity(new Intent(requireActivity(), PremiumActivity.class));
//            }
//        });

    }
}
