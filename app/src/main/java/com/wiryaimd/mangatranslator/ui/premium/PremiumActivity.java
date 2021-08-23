package com.wiryaimd.mangatranslator.ui.premium;

import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.wiryaimd.mangatranslator.R;
public class PremiumActivity extends AppCompatActivity{

    private static final String TAG = "PremiumActivity";

    private Toolbar toolbar;
    private Button buyPremium;
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_MangaTranslator);
        setContentView(R.layout.activity_premium);

        buyPremium = findViewById(R.id.premium_btnbuy);
        toolbar = findViewById(R.id.setuplang_toolbar);

        setSupportActionBar(toolbar);

    }
}
