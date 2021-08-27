package com.wiryaimd.mangatranslator.ui.premium;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.wiryaimd.mangatranslator.BaseApplication;
import com.wiryaimd.mangatranslator.R;
import com.wiryaimd.mangatranslator.util.Const;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

public class PremiumActivity extends AppCompatActivity{

    private static final String TAG = "PremiumActivity";

    private Toolbar toolbar;
    private Button buyPremium;

    private TextView tvexpires;

    private PremiumViewModel premiumViewModel;
    private BaseApplication baseApplication;

    public static final String CLIENT_ID = "ARbxuNTRkNyKE7QE5x51-sTCvRt0NJyAIE9wwG_iYMLZI0iUhDhcaIlFcN-gHGSvLJdXDGjx5Abj637c";

    private PayPalConfiguration payPalConfiguration = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION)
            .clientId(CLIENT_ID);

    private ActivityResultLauncher<Intent> premiumLaunch = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null){
                PaymentConfirmation confirm = result.getData().getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null){
                    Log.d(TAG, "onActivityResult: check masuk payment");
                    try {
                        String response = confirm.toJSONObject().toString(4);
                        JSONObject jsonObject = new JSONObject(response);

                        String state = jsonObject.getJSONObject("response").getString("state");
                        if (state.equalsIgnoreCase("approved")) {
                            SharedPreferences sharedPreferences = baseApplication.getSharedPref();
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putLong(Const.SUBSCRIBE_DATE, baseApplication.generateTime());

                            baseApplication.saveSubscribe(true);

                            editor.apply();

                            tvexpires.setVisibility(View.VISIBLE);
                            buyPremium.setBackgroundColor(ContextCompat.getColor(PremiumActivity.this, R.color.color3));
                            buyPremium.setText("Thank you for Subscribe!");
                            Toast.makeText(baseApplication, "Thank you for subscribe this app!", Toast.LENGTH_LONG).show();
                        }

                        Log.d(TAG, "onActivityResult: response payment: " + response);
                    }catch (JSONException e){
                        Log.d(TAG, "onActivityResult: fail");
                    }
                }
            }else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.");
            }
            else if (result.getResultCode() == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }
    });

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_MangaTranslator);
        setContentView(R.layout.activity_premium);

        tvexpires = findViewById(R.id.premium_expires);
        buyPremium = findViewById(R.id.premium_btnbuy);
        toolbar = findViewById(R.id.setuplang_toolbar);

        setSupportActionBar(toolbar);

        premiumViewModel = new ViewModelProvider(PremiumActivity.this).get(PremiumViewModel.class);
        baseApplication = premiumViewModel.getApplication();

        if (baseApplication.isSubscribe()){
            int hoursTime = (int) ((baseApplication.getDiff() / (1000 * 60 * 60)) % 24);
            int dayTime = (int) ((baseApplication.getDiff() / (1000 * 60 * 60 * 24)) % 30);
            tvexpires.setVisibility(View.VISIBLE);
            tvexpires.setText(("Expires in " + dayTime + " days " + hoursTime + " hours"));

            buyPremium.setBackgroundColor(ContextCompat.getColor(PremiumActivity.this, R.color.color3));
            buyPremium.setText("Thank you for Subscribe!");
        }

        Intent intent = new Intent(PremiumActivity.this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, payPalConfiguration);
        startService(intent);

        buyPremium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (baseApplication.isSubscribe()){
                    Toast.makeText(baseApplication, "You already subscribe", Toast.LENGTH_SHORT).show();
                }else {
                    processPayment();
                }
            }
        });

    }

    public void processPayment(){
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal("1"), "USD", "MangaTranslator Premium", PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(PremiumActivity.this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, payPalConfiguration);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);

        premiumLaunch.launch(intent);
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(PremiumActivity.this, PayPalService.class));
        super.onDestroy();
    }
}
