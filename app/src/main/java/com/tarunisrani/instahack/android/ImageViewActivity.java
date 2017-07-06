package com.tarunisrani.instahack.android;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.tarunisrani.instahack.R;
import com.tarunisrani.instahack.helper.MySingleton;
import com.tarunisrani.instahack.helper.NetworkCall;
import com.tarunisrani.instahack.helper.NetworkCallListener;
import com.tarunisrani.instahack.utils.AppUtils;
import com.tarunisrani.instahack.utils.String_Constants;

import org.json.JSONObject;

import java.io.File;

public class ImageViewActivity extends AppCompatActivity implements NetworkCallListener, View.OnClickListener {

    private final int CALLBACK_DOWNLOAD_IMAGE = 1;
    private final int CALLBACK_DOWNLOAD_VIDEO = 2;
    private final int REQUEST_SAVE_IMAGE = 100;




    private String mFileName = null;
    private String mImageLink = null;
    private String mType = null;
    private String mUserName = null;
//    private boolean mIsVideo = false;


    private VideoView videoView;
    private NetworkImageView networkimageView;
    private ImageView imageView;
    private ProgressBar video_progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_view_layout);

        Intent intent = getIntent();
        if(intent!=null){
            mImageLink = intent.getStringExtra("IMAGE_LINK");
            mFileName = intent.getStringExtra("FILE_NAME");
            mType = intent.getStringExtra("TYPE");
            mUserName = intent.getStringExtra("USER_NAME");
//            mIsVideo = intent.getBooleanExtra("IS_VIDEO", false);
        }

        videoView = (VideoView) findViewById(R.id.videoView);
        networkimageView = (NetworkImageView) findViewById(R.id.networkimageView);
        imageView = (ImageView) findViewById(R.id.imageView);
        video_progressbar = (ProgressBar) findViewById(R.id.video_progressbar);

        ImageView download_button = (ImageView) findViewById(R.id.instahack_individual_image_download_button);
        ImageView share_button = (ImageView) findViewById(R.id.instahack_individual_image_share_button);

        if(mType.equalsIgnoreCase(String_Constants.TYPE_VIDEO)){
//            VideoView videoView = (VideoView) findViewById(R.id.videoView);
            videoView.setVisibility(View.VISIBLE);
            if(mFileName !=null){
                showVideo(videoView);
            }
        }else{
//            ImageView networkimageView = (ImageView) findViewById(R.id.networkimageView);
//            networkimageView.setVisibility(View.VISIBLE);
            if(mFileName !=null){
                showImage();
            }
        }

        download_button.setOnClickListener(this);
        share_button.setOnClickListener(this);

    }

    private void showImage(){
        Log.e("ImageActivity", "Loading image");
        File myFilesDir = Environment.getExternalStorageDirectory().getAbsoluteFile();
        File instaHackDir = new File(myFilesDir, String_Constants.Instahack_Dir_Name);
        File userDir = new File(instaHackDir, mUserName);
        File file = new File(userDir, mFileName);

        if(file.exists()) {

            Log.e("ImageActivity", "Loading file from local");

            networkimageView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            final Bitmap imageBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            imageView.setImageBitmap(imageBitmap);

        }else{

            Log.e("ImageActivity", "Loading file from network");
            networkimageView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            ImageLoader imageLoader = MySingleton.getInstance(this)
                    .getImageLoader();
            imageLoader.get(mImageLink, ImageLoader.getImageListener(networkimageView,
                    android.R.drawable.ic_menu_gallery, android.R.drawable
                            .ic_dialog_alert));
            networkimageView.setImageUrl(mImageLink, imageLoader);

        }
    }

    private void showVideo(VideoView videoView){
        video_progressbar.setVisibility(View.VISIBLE);
        Log.e("ImageActivity", "Loading video");
        File myFilesDir = Environment.getExternalStorageDirectory().getAbsoluteFile();
        File instaHackDir = new File(myFilesDir, String_Constants.Instahack_Dir_Name);
        File userDir = new File(instaHackDir, mUserName);
        File file = new File(userDir, mFileName);
        if(file.exists()) {
            video_progressbar.setVisibility(View.GONE);
            Log.e("FilePath", file.getAbsolutePath());
            videoView.setMediaController(new MediaController(this));
            videoView.setVideoPath(file.getAbsolutePath());
            videoView.start();
        }else{
            new NetworkCall().downloadFile(CALLBACK_DOWNLOAD_VIDEO, mImageLink, mUserName, mFileName, this);
        }

    }

    private void performDownloadOperation(){
        video_progressbar.setVisibility(View.VISIBLE);
        new NetworkCall().downloadFile(CALLBACK_DOWNLOAD_IMAGE, mImageLink, mUserName, mFileName, this);
    }

    private void performPermissionCheckOperation(){
        if(AppUtils.isStoragePermissionGranted(this, REQUEST_SAVE_IMAGE)){
            performDownloadOperation();
        }
    }

    private void performShareOperation(){

    }


    @Override
    public void onResponse(int code, JSONObject imageUrl) {
        if(code == CALLBACK_DOWNLOAD_VIDEO){
            showVideo(videoView);
        }else{
            video_progressbar.setVisibility(View.GONE);
            Toast.makeText(ImageViewActivity.this, "Download Completed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.instahack_individual_image_download_button:
                performPermissionCheckOperation();
                break;
            case R.id.instahack_individual_image_share_button:
                performShareOperation();
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


