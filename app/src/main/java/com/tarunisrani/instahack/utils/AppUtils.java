package com.tarunisrani.instahack.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by tarunisrani on 2/3/17.
 */
public class AppUtils {
    public static boolean isStoragePermissionGranted(Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("FILE UPLOAD", "Permission is granted");
                return true;
            } else {

                Log.v("FILE UPLOAD","Permission is revoked");
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                return false;
            }
        }
        else {
            Log.v("FILE UPLOAD", "Permission is granted");
            return true;
        }
    }
}
