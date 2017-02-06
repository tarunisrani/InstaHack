package com.tarunisrani.instahack.android;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tarunisrani.instahack.R;
import com.tarunisrani.instahack.helper.NetworkCall;
import com.tarunisrani.instahack.helper.NetworkCallListener;
import com.tarunisrani.instahack.utils.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NetworkCallListener {

    private final int REQUEST_SAVE_IMAGE = 100;
    private final int CALLBACK_PARSE_LINK = 1;
    private final int CALLBACK_DOWNLOAD_FILE = 2;

    private ImageView instahack_image_field;
    private EditText instahack_link_field;
    private ProgressBar instahack_progressbar;

    private ImageView instahack_download_button;
    private ImageView instahack_share_button;

    private String mFileURL = null;
    private String mImageName = null;
    private boolean isVideo = false;

    private boolean fileDownloaded = false;
    private boolean fileShareInQueue = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        performPermissionCheckOperation();


        instahack_link_field = (EditText) findViewById(R.id.instahack_link_field);
        ImageView instahack_search_button = (ImageView) findViewById(R.id.instahack_search_button);
//        Button hack_install_button = (Button) findViewById(R.id.instahack_install_button);
        instahack_download_button = (ImageView) findViewById(R.id.instahack_download_button);
        instahack_share_button = (ImageView) findViewById(R.id.instahack_share_button);
        instahack_image_field = (ImageView) findViewById(R.id.instahack_image_field);
        instahack_progressbar = (ProgressBar) findViewById(R.id.instahack_progressbar);


        instahack_search_button.setOnClickListener(this);
//        hack_install_button.setOnClickListener(this);
        instahack_download_button.setOnClickListener(this);
        instahack_share_button.setOnClickListener(this);
        instahack_image_field.setOnClickListener(this);
    }

    private void performPermissionCheckOperation(){
        AppUtils.isStoragePermissionGranted(this, REQUEST_SAVE_IMAGE);
    }

    private void performSearchOperation(){
        String url = instahack_link_field.getText().toString();
        if(!url.isEmpty()) {
            instahack_progressbar.setVisibility(View.VISIBLE);
            instahack_image_field.setVisibility(View.GONE);
            instahack_download_button.setVisibility(View.GONE);
            instahack_share_button.setVisibility(View.GONE);
            fileDownloaded = fileShareInQueue = false;
            new NetworkCall().getJSONDetails(CALLBACK_PARSE_LINK, url, this);
        }
    }

    private void performAppUpdate(){
        Intent promptInstall = new Intent(Intent.ACTION_VIEW);

//                .setDataAndType(Uri.parse("file:///sdcard/Download/app-debug.apk"),
//                        "application/vnd.android.package-archive");

        promptInstall.setDataAndType(Uri.parse("file:///sdcard/Download/app-debug.apk"), "application/vnd.android.package-archive");
        promptInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(promptInstall);
    }

    private void performDownloadOperation(){
        if(mFileURL == null){
            performSearchOperation();
        } else{
            performImageSavePermissionCheck();
        }
    }

    private void downloadFile(){
        if(mImageName!=null) {
            instahack_progressbar.setVisibility(View.VISIBLE);
            new NetworkCall().downloadFile(CALLBACK_DOWNLOAD_FILE, mFileURL, mImageName, this);
        }
    }

    private void performImageSavePermissionCheck(){
        downloadFile();
    }

    private void performShareOperation(){
        if(fileDownloaded){
            shareImage();
        }else{
            fileShareInQueue = true;
            downloadFile();
        }
    }

    private void shareImage(){
        File myFilesDir = Environment.getExternalStorageDirectory().getAbsoluteFile();
//        String fileName = "file:///sdcard/"+mImageName;
        File file = new File(myFilesDir, mImageName);
//        String fileName = myFilesDir+mImageName;
        Uri uri = Uri.parse(file.getAbsolutePath());
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
//        intent.putExtra(Intent.EXTRA_TEXT, "Test Whatsapp Message");
//        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        intent.setType("image/jpeg");
        intent.setPackage("com.whatsapp");
        startActivity(intent);
    }

    private void showImage(JSONObject jsonObject){

        try {
            isVideo = jsonObject.getBoolean("is_video");
            String image_url = jsonObject.getString("imagelink");
            if(isVideo){
                mFileURL = jsonObject.getString("video_url");
            }else{
                mFileURL = jsonObject.getString("imagelink");
            }

            mImageName = jsonObject.getString("filename");
            Picasso.with(this).load(image_url).into(instahack_image_field, new Callback() {
                @Override
                public void onSuccess() {
                    instahack_progressbar.setVisibility(View.GONE);
                    instahack_image_field.setVisibility(View.VISIBLE);
                    instahack_download_button.setVisibility(View.VISIBLE);
                    instahack_share_button.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError() {
                    mFileURL = mImageName = null;
                    isVideo = false;
                }
            });
        } catch (JSONException exp){
            exp.printStackTrace();
            mFileURL = mImageName = null;
            isVideo = false;
        }
    }

    private void performDownloadCompletedOperation(){
        fileDownloaded = true;
        instahack_progressbar.setVisibility(View.GONE);
        Toast.makeText(MainActivity.this, "Download Completed", Toast.LENGTH_SHORT).show();
        if(fileShareInQueue){
            shareImage();
        }
    }

    private void openImageScreen(){
        Intent intent = new Intent(this, ImageViewActivity.class);
        intent.putExtra("IMAGE", mImageName);
        intent.putExtra("ISVIDEO", isVideo);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.instahack_search_button:
                performSearchOperation();
//                performAppUpdate();
                break;
            case R.id.instahack_download_button:
                performDownloadOperation();
                break;
            case R.id.instahack_share_button:
                performShareOperation();
                break;
            case R.id.instahack_image_field:
                openImageScreen();
                break;
        }
    }

    @Override
    public void onResponse(int code, JSONObject jsonObject) {
        switch (code){
            case CALLBACK_PARSE_LINK:
                showImage(jsonObject);
                break;
            case CALLBACK_DOWNLOAD_FILE:
                performDownloadCompletedOperation();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == REQUEST_SAVE_IMAGE) {
            Log.v("Image save", "Permision granted");
            performDownloadOperation();
        }
    }
}


