package com.wiryaimd.mangatranslator;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.wiryaimd.mangatranslator.util.Const;
import com.wiryaimd.mangatranslator.util.LanguagesData;
import com.wiryaimd.mangatranslator.util.translator.GApiTranslate;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;

public class BaseApplication extends Application {

    private static final String TAG = "BaseApplication";

    private long timeNow;
    private long dateSubscribe = 0, diff;

    private boolean isSubscribe;
    private int countTL, countTLMS;

    private SharedPreferences sharedPref;

    public static final String PREF_KEY = "MangaTL_PREF";

    @Override
    public void onCreate() {
        super.onCreate();

        sharedPref = getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);

        isSubscribe = sharedPref.getBoolean(Const.IS_SUBSCRIBE, false);
        countTL = sharedPref.getInt(Const.COUNT_TL, 0);
        countTLMS = sharedPref.getInt(Const.COUNT_MSTL, 0);

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


}
