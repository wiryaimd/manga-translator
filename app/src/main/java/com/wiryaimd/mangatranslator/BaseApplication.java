package com.wiryaimd.mangatranslator;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.wiryaimd.mangatranslator.ui.main.MainActivity;
import com.wiryaimd.mangatranslator.util.Const;
import com.wiryaimd.mangatranslator.util.LanguagesData;
import com.wiryaimd.mangatranslator.util.translator.GApiTranslate;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;

public class BaseApplication extends Application {

    private static final String TAG = "BaseApplication";

    private long timeNow;
    private long dateSubscribe = 0, diff;

    private boolean isSubscribe;
    private int countTL, countTLMS;

    private int countAds;

    private String tempDay, currDay;

    private SharedPreferences sharedPref;

    public static final String PREF_KEY = "MangaTL_PREF";

    private MaxInterstitialAd interstitialAd;
    private InterstitialAd interstitialAdAdmob;

    private MaxAdListener maxAdListener;
    private InterstitialAdLoadCallback admobAdListener;

    private int retryAttempt = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        sharedPref = getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Calendar calendar = Calendar.getInstance();
        String datestr = dateFormat.format(calendar.getTime());
        Log.d(TAG, "onCreate: format date: " + datestr);

        if (sharedPref.getString(Const.TEMPDAY, null) == null){
            Log.d(TAG, "onCreate: tempday null");
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(Const.TEMPDAY, datestr);
            editor.apply();
        }else {
            String tempDate = sharedPref.getString(Const.TEMPDAY, datestr);
            if (!datestr.equalsIgnoreCase(tempDate)){
                Log.d(TAG, "onCreate: not same day");
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(Const.TEMPDAY, datestr);
                editor.putInt(Const.COUNT_TL, 0);
                editor.putInt(Const.COUNT_MSTL, 0);
                editor.apply();
            }
        }

        isSubscribe = sharedPref.getBoolean(Const.IS_SUBSCRIBE, false);
        countTL = sharedPref.getInt(Const.COUNT_TL, 0);
        countTLMS = sharedPref.getInt(Const.COUNT_MSTL, 0);
        countAds = 0;

        Log.d(TAG, "onCreate: base isSubscribe: " + isSubscribe);
        Log.d(TAG, "onCreate: base countTL: " + countTL);
        Log.d(TAG, "onCreate: base countTLMS: " + countTLMS);

        if (isSubscribe){
            LanguagesData.setPremium();
            dateSubscribe = sharedPref.getLong(Const.SUBSCRIBE_DATE, 0);
            dateSubscribe += (30L * 1000L * 60L * 60L * 24L);
        }
        Log.d(TAG, "onCreate: base dateSubs: " + dateSubscribe);

        timeNow = System.currentTimeMillis();
        diff = dateSubscribe - timeNow;

        Log.d(TAG, "run: diff: " + diff);
        if (isSubscribe && diff < 0) {
            Log.d(TAG, "run: expires");
            saveSubscribe(false);
        }

        AppLovinSdk.getInstance(BaseApplication.this).setMediationProvider("max");

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });

        maxAdListener = new MaxAdListener() {
            @Override
            public void onAdLoaded(MaxAd ad) {
                retryAttempt = 0;
                Log.d(TAG, "onAdLoaded: ");
            }

            @Override
            public void onAdDisplayed(MaxAd ad) {
                Log.d(TAG, "onAdDisplayed: ");
            }

            @Override
            public void onAdHidden(MaxAd ad) {
                Log.d(TAG, "onAdHidden: ");
                interstitialAd.loadAd();
                countAds += 1;
            }

            @Override
            public void onAdClicked(MaxAd ad) {
                Log.d(TAG, "onAdClicked: ");
            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
//                retryAttempt++;
//                long delayMillis = TimeUnit.SECONDS.toMillis( (long) Math.pow( 2, Math.min( 6, retryAttempt ) ) );

//                Timer timer = new Timer();
//                timer.scheduleAtFixedRate(new TimerTask() {
//                    @Override
//                    public void run() {
//                        Log.d(TAG, "run: delay load ads");
//                        interstitialAd.loadAd();
//                        timer.cancel();
//                    }
//                }, delayMillis, delayMillis);
                initAdmob();
                Log.d(TAG, "onAdLoadFailed: ");
            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                Log.d(TAG, "onAdDisplayFailed: ");
                interstitialAd.loadAd();
            }
        };

        admobAdListener = new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull @NotNull InterstitialAd interstitialAd) {
                Log.d(TAG, "onAdLoaded: admob ad load");
                interstitialAdAdmob = interstitialAd;
            }

            @Override
            public void onAdFailedToLoad(@NonNull @NotNull LoadAdError loadAdError) {
                Log.d(TAG, "onAdFailedToLoad: admod load err: " + loadAdError.getMessage());
                interstitialAdAdmob = null;
                interstitialAd.loadAd();

            }
        };
    }

    public void initApplovin(Activity activity){
        AppLovinSdk.initializeSdk(BaseApplication.this, new AppLovinSdk.SdkInitializationListener() {
            @Override
            public void onSdkInitialized(AppLovinSdkConfiguration config) {
                Log.d(TAG, "onSdkInitialized: init success");
                interstitialAd = new MaxInterstitialAd(getString(R.string.ads_id), activity);
                interstitialAd.loadAd();
                interstitialAd.setListener(maxAdListener);
            }
        });
    }

    public void initAdmob(){
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(BaseApplication.this, getString(R.string.admob_ads1), adRequest, admobAdListener);
    }

    public MaxInterstitialAd getInterstitialAd() {
        return interstitialAd;
    }

    public InterstitialAd getInterstitialAdAdmob() {
        return interstitialAdAdmob;
    }

    public long getDiff() {
        timeNow = System.currentTimeMillis();
        diff = dateSubscribe - timeNow;
        return diff;
    }

    public void saveSubscribe(boolean status){
        isSubscribe = status;
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(Const.IS_SUBSCRIBE, isSubscribe);
        editor.apply();
    }

    public void saveTl(int count){
        SharedPreferences.Editor editor = sharedPref.edit();

        countTL += count;
        Log.d(TAG, "saveTl: countTL: " + countTL);

        editor.putInt(Const.COUNT_TL, countTL);
        editor.apply();
    }

    public void saveTlMS(int count){
        SharedPreferences.Editor editor = sharedPref.edit();

        countTLMS += count;
        Log.d(TAG, "saveTlMS: countTLMS: " + countTLMS);

        editor.putInt(Const.COUNT_MSTL, countTLMS);
        editor.apply();
    }

    public long generateTime(){
        this.dateSubscribe = System.currentTimeMillis();
        this.dateSubscribe += (30L * 1000L * 60L * 60L * 24L);
        Log.d(TAG, "generateTime: dateSubscribe");
        return this.dateSubscribe;
    }

    public SharedPreferences getSharedPref() {
        return sharedPref;
    }

    public long getDateSubscribe() {
        return dateSubscribe;
    }

    public int getCountTL() {
        return countTL;
    }

    public int getCountTLMS() {
        return countTLMS;
    }

    public boolean isSubscribe() {
        return isSubscribe;
    }

    public long getTimeNow() {
        return timeNow;
    }

    public int getCountAds() {
        return countAds;
    }
}
