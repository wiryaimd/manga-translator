package com.wiryaimd.mangatranslator.ui.premium;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.material.navigation.NavigationView;
import com.wiryaimd.mangatranslator.R;
import com.wiryaimd.mangatranslator.ui.main.MainActivity;
import com.wiryaimd.mangatranslator.ui.setup.fragment.dialog.InfoDialog;

import org.jetbrains.annotations.NotNull;

public class PremiumActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler{

    private static final String TAG = "PremiumActivity";

    private BillingProcessor billingProcessor;
    private TransactionDetails transactionDetails;

    private Toolbar toolbar;
    private Button buyPremium;

    public static final String SUBS_ID = "subs_month";
    public static final String SUBS_ID_PREMIUM = "mangatranslator_subs";

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_MangaTranslator);
        setContentView(R.layout.activity_premium);

        buyPremium = findViewById(R.id.premium_btnbuy);
        toolbar = findViewById(R.id.setuplang_toolbar);

        setSupportActionBar(toolbar);

        billingProcessor = BillingProcessor.newBillingProcessor(PremiumActivity.this, getString(R.string.ps_license), this);
        billingProcessor.initialize();

    }

    @Override
    public void onBillingInitialized() {
        Log.d(TAG, "onBillingInitialized: ");

        transactionDetails = billingProcessor.getSubscriptionTransactionDetails(SUBS_ID_PREMIUM);

        buyPremium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (billingProcessor.isSubscriptionUpdateSupported()){
                    billingProcessor.subscribe(PremiumActivity.this, SUBS_ID_PREMIUM);
                }else{
                    new InfoDialog("FAILED TO SUBSCRIBE", "Subscription update not supported", false);
                }
            }
        });

        if (transactionDetails != null && transactionDetails.purchaseInfo != null){
            Log.d(TAG, "onBillingInitialized: subscribed");
            buyPremium.setText("Subscribed");
            buyPremium.setBackgroundColor(ContextCompat.getColor(PremiumActivity.this, R.color.color3));
        }else{
            Log.d(TAG, "onBillingInitialized: lets buyy");
        }
    }

    @Override
    public void onProductPurchased(@NonNull @NotNull String productId, @android.support.annotation.Nullable @org.jetbrains.annotations.Nullable TransactionDetails details) {
        Log.d(TAG, "onProductPurchased: purcheddboss: " + productId);
        buyPremium.setText("Subscribed");
        buyPremium.setBackgroundColor(ContextCompat.getColor(PremiumActivity.this, R.color.color3));
    }

    @Override
    public void onPurchaseHistoryRestored() {
        Log.d(TAG, "onPurchaseHistoryRestored: ");
    }

    @Override
    public void onBillingError(int errorCode, @android.support.annotation.Nullable @org.jetbrains.annotations.Nullable Throwable error) {
        Log.d(TAG, "onBillingError: ");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if (!billingProcessor.handleActivityResult(requestCode, resultCode, data)){
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        if (billingProcessor != null){
            billingProcessor.release();
        }
        super.onDestroy();
    }
}
