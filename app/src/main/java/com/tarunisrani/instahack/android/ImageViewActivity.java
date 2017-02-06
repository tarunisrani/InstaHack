package com.tarunisrani.instahack.android;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tarunisrani.instahack.R;

import java.io.File;

public class ImageViewActivity extends AppCompatActivity {

    private String mFileName = null;
    private boolean mIsVideo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_view_layout);

        Intent intent = getIntent();
        if(intent!=null){
            mFileName = intent.getStringExtra("IMAGE");
            mIsVideo = intent.getBooleanExtra("ISVIDEO", false);
        }

        if(mIsVideo){
            VideoView videoView = (VideoView) findViewById(R.id.videoView);
            videoView.setVisibility(View.VISIBLE);
            if(mFileName !=null){
                showVideo(videoView);
            }
        }else{
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setVisibility(View.VISIBLE);
            if(mFileName !=null){
                showImage(imageView);
            }
        }


    }

    private void showImage(ImageView imageView){
        File myFilesDir = Environment.getExternalStorageDirectory().getAbsoluteFile();
        File file = new File(myFilesDir, mFileName);
        Log.e("FilePath", file.getAbsolutePath());
        Picasso.with(this).load("file://" + file.getAbsolutePath()).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                Toast.makeText(ImageViewActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError() {
                Toast.makeText(ImageViewActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showVideo(VideoView videoView){
        File myFilesDir = Environment.getExternalStorageDirectory().getAbsoluteFile();
        File file = new File(myFilesDir, mFileName);
        Log.e("FilePath", file.getAbsolutePath());
        videoView.setMediaController(new MediaController(this));
        videoView.setVideoPath(file.getAbsolutePath());
        videoView.start();

    }

}


