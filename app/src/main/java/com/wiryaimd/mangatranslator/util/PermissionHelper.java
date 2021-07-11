package com.wiryaimd.mangatranslator.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

public class PermissionHelper {

    public static final int PERM_CODE = 10;

    public static boolean requestPermission(Activity activity){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                    activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                String[] perm = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                activity.requestPermissions(perm, PERM_CODE);
                return false;
            }else{
                return true;
            }
        }else{
            return true;
        }
    }

}
