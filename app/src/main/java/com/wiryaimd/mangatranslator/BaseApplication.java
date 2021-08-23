package com.wiryaimd.mangatranslator;

import android.app.Application;
import android.util.Log;

import com.wiryaimd.mangatranslator.util.translator.GApiTranslate;

public class BaseApplication extends Application {

    private static final String TAG = "BaseApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate: start translate");

    }


}
