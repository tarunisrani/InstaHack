package com.tarunisrani.instahack.android;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.tarunisrani.instahack.R;
import com.tarunisrani.instahack.helper.MySingleton;
import com.tarunisrani.instahack.helper.NetworkCall;
import com.tarunisrani.instahack.helper.NetworkCallListener;

import org.json.JSONObject;

import java.io.File;

public class ImageViewActivity extends AppCompatActivity implements NetworkCallListener {

    private final int CALLBACK_DOWNLOAD_FILE = 2;

    private String mFileName = null;
    private String mImageLink = null;
    private String mVideoUrl = null;
    private String mUserName = null;
    private boolean mIsVideo = false;


    private VideoView videoView;
    private NetworkImageView networkimageView;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_view_layout);

        Intent intent = getIntent();
        if(intent!=null){
            mImageLink = intent.getStringExtra("IMAGE_LINK");
            mFileName = intent.getStringExtra("FILE_NAME");
            mVideoUrl = intent.getStringExtra("VIDEO_URL");
            mUserName = intent.getStringExtra("USER_NAME");
            mIsVideo = intent.getBooleanExtra("IS_VIDEO", false);
        }

        videoView = (VideoView) findViewById(R.id.videoView);
        networkimageView = (NetworkImageView) findViewById(R.id.networkimageView);
        imageView = (ImageView) findViewById(R.id.imageView);
        if(mIsVideo){
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
    }

    private void showImage(){
        Log.e("ImageActivity", "Loading image");
        File myFilesDir = Environment.getExternalStorageDirectory().getAbsoluteFile();
        File instaHackDir = new File(myFilesDir, "InstaHack");
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
        Log.e("ImageActivity", "Loading video");
        File myFilesDir = Environment.getExternalStorageDirectory().getAbsoluteFile();
        File instaHackDir = new File(myFilesDir, "InstaHack");
        File userDir = new File(instaHackDir, mUserName);
        File file = new File(userDir, mFileName);
        if(file.exists()) {
            Log.e("FilePath", file.getAbsolutePath());
            videoView.setMediaController(new MediaController(this));
            videoView.setVideoPath(file.getAbsolutePath());
            videoView.start();
        }else{
            new NetworkCall().downloadFile(CALLBACK_DOWNLOAD_FILE, mVideoUrl, mUserName, mFileName, this);
        }

    }

    @Override
    public void onResponse(int code, JSONObject imageUrl) {
        if(code == CALLBACK_DOWNLOAD_FILE){
            showVideo(videoView);
        }
    }
}


