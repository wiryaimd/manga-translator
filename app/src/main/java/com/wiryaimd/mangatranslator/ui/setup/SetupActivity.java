package com.wiryaimd.mangatranslator.ui.setup;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wiryaimd.mangatranslator.R;
import com.wiryaimd.mangatranslator.model.SelectedModel;
import com.wiryaimd.mangatranslator.ui.setup.fragment.ProcessFragment;
import com.wiryaimd.mangatranslator.ui.setup.fragment.dialog.InfoDialog;
import com.wiryaimd.mangatranslator.util.Const;

import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.Executors;

public class SetupActivity extends AppCompatActivity {

    private static final String TAG = "SetupActivity";

    private SetupViewModel setupViewModel;
    private ArrayList<SelectedModel> selectedList = new ArrayList<>();

    private Toolbar toolbar;

    private DatabaseReference dbref;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_MangaTranslator);
        setContentView(R.layout.activity_setuplang);

        toolbar = findViewById(R.id.setuplang_toolbar);
        setSupportActionBar(toolbar);

        setupViewModel = new ViewModelProvider(SetupActivity.this).get(SetupViewModel.class);

        dbref = FirebaseDatabase.getInstance().getReference().child("mangaTranslator");

        dbref.child("availableAws").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Boolean isAvailable = snapshot.getValue(Boolean.class);

                Log.d(TAG, "onDataChange: availableAws: " + isAvailable);

                setupViewModel.setAvailableAws(isAvailable);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        dbref.child("availableMicrosoft").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Boolean isAvailable = snapshot.getValue(Boolean.class);

                Log.d(TAG, "onDataChange: availableMicrosoft: " + isAvailable);

                setupViewModel.setAvailableMicrosoft(isAvailable);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        dbref.child("rapid_key").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String key = snapshot.getValue(String.class);
                Log.d(TAG, "onDataChange: rapid_key from rd: " + key);
                setupViewModel.setRapidKey(key);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        dbref.child("rapid_host").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String host = snapshot.getValue(String.class);
                Log.d(TAG, "onDataChange: rapid_host from rd: " + host);
                setupViewModel.setRapidHost(host);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        if (getIntent() != null) {
            selectedList = getIntent().getParcelableArrayListExtra(Const.SELECTED_LIST);
            setupViewModel.getSelectedModelLiveData().setValue(selectedList);
        } else {
            Toast.makeText(SetupActivity.this, "Cannot load data, please try again", Toast.LENGTH_SHORT).show();
            finish();
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().replace(R.id.setuplang_mainframe, new ProcessFragment());
        ft.commit();

    }
}
