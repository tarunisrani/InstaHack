package com.tarunisrani.instahack.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

    public static void showCustomAlertDialog(Activity activity, String title, String msg, boolean showCancel, DialogInterface.OnClickListener positivelistener, DialogInterface.OnClickListener negativelistener, String negText, String posText){
        if(activity != null && !activity.isFinishing()){
            AlertDialog.Builder alert = new AlertDialog.Builder(activity);
            alert.setTitle(title);
            alert.setMessage(msg);
            alert.setPositiveButton(posText, positivelistener);
            if (showCancel) {
                alert.setNegativeButton(negText, negativelistener);
            }
            alert.show();
        }
    }
}
