package com.tarunisrani.instahack.android;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tarunisrani.instahack.R;
import com.tarunisrani.instahack.adapter.ImageListAdapter;
import com.tarunisrani.instahack.helper.NetworkCall;
import com.tarunisrani.instahack.helper.NetworkCallListener;
import com.tarunisrani.instahack.listeners.ImageListClickListener;
import com.tarunisrani.instahack.utils.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, NetworkCallListener, ImageListClickListener {

    private final int REQUEST_SAVE_IMAGE = 100;
    private final int CALLBACK_PARSE_LINK = 1;
    private final int CALLBACK_DOWNLOAD_FILE = 2;

//    private ImageView instahack_image_field;
    private EditText instahack_link_field;
    private ProgressBar instahack_progressbar;

    private ImageView instahack_download_button;
    private ImageView instahack_share_button;

    private String mFileURL = null;
    private String mImageName = null;
    private String mUserName = null;
    private boolean isVideo = false;
    private int no_of_files_to_download = 0;
    private int no_of_files_downloaded = 0;

    private JSONArray final_image_list;

    private boolean fileDownloaded = false;
    private boolean fileShareInQueue = false;

    private ImageListAdapter imageListAdapter;
    private RecyclerView instahack_recycler_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        performPermissionCheckOperation();

        imageListAdapter = new ImageListAdapter(this);

        instahack_link_field = (EditText) findViewById(R.id.instahack_link_field);
        ImageView instahack_search_button = (ImageView) findViewById(R.id.instahack_search_button);
//        Button hack_install_button = (Button) findViewById(R.id.instahack_install_button);
        instahack_download_button = (ImageView) findViewById(R.id.instahack_download_button);
        instahack_share_button = (ImageView) findViewById(R.id.instahack_share_button);
//        instahack_image_field = (ImageView) findViewById(instahack_image_field);
        instahack_progressbar = (ProgressBar) findViewById(R.id.instahack_progressbar);


        instahack_recycler_view = (RecyclerView) findViewById(R.id.instahack_recycler_view);

        instahack_recycler_view.setAdapter(imageListAdapter);
        imageListAdapter.notifyDataSetChanged();
        imageListAdapter.setmListener(this);

        instahack_recycler_view.setHasFixedSize(true);
        LinearLayoutManager linearLayout =new LinearLayoutManager(this);
        linearLayout.setOrientation(LinearLayoutManager.VERTICAL);
        instahack_recycler_view.setLayoutManager(linearLayout);


        instahack_search_button.setOnClickListener(this);
//        hack_install_button.setOnClickListener(this);
        instahack_download_button.setOnClickListener(this);
        instahack_share_button.setOnClickListener(this);
//        instahack_image_field.setOnClickListener(this);
    }

    private void performPermissionCheckOperation(){
        AppUtils.isStoragePermissionGranted(this, REQUEST_SAVE_IMAGE);
    }

    private void performSearchOperation(){
        String url = instahack_link_field.getText().toString();
        if(!url.isEmpty()) {
            String lastCharacter = url.substring(url.length()-1, url.length());
            Log.e("Last character", lastCharacter);
            if(!lastCharacter.equalsIgnoreCase("/")){
                url += "/";
            }
            showAlertInProfilePageScenario(url);
        }
    }

    private void performNetworkOperation(String url, boolean parseAllPages){
        instahack_recycler_view.removeAllViews();
        imageListAdapter.clear();
        imageListAdapter.notifyDataSetChanged();
        instahack_progressbar.setVisibility(View.VISIBLE);
//        instahack_image_field.setVisibility(View.GONE);
        instahack_download_button.setVisibility(View.GONE);
        instahack_share_button.setVisibility(View.GONE);
        fileDownloaded = fileShareInQueue = false;
        no_of_files_downloaded = no_of_files_to_download = 0;
        new NetworkCall().getJSONDetails(CALLBACK_PARSE_LINK, url, parseAllPages, this);
    }

    private void showAlertInProfilePageScenario(final String url){
        if(!url.contains("/p/")){
            String msg = "White loading profile page, heavy network operations are required";
            DialogInterface.OnClickListener single = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    performNetworkOperation(url, false);
                }
            };
            DialogInterface.OnClickListener all = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    performNetworkOperation(url, true);
                }
            };

            AppUtils.showCustomAlertDialog(this, "Confirmation", msg, true, all, single, "Single page", "All pages");
        }else{
            performNetworkOperation(url, false);
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
        if(final_image_list == null){
            performSearchOperation();
        } else{
            performImageSavePermissionCheck();
        }
    }

    private void downloadFile(){
        if(final_image_list!=null) {
            instahack_progressbar.setVisibility(View.VISIBLE);
            no_of_files_to_download = final_image_list.length();
            for(int index = 0; index<no_of_files_to_download;index++){
                try {
                    String file_url = "";
                    String image_name = "";
                    JSONObject list_element = final_image_list.getJSONObject(index);
                    isVideo = list_element.getBoolean("is_video");
                    String image_url = list_element.getString("imagelink");
                    if (isVideo) {
                        file_url = list_element.getString("video_url");
                    } else {
                        file_url = list_element.getString("imagelink");
                    }

                    image_name = list_element.getString("filename");
                    Log.e("image_url", image_url);

                    new NetworkCall().downloadFile(CALLBACK_DOWNLOAD_FILE, file_url, mUserName, image_name, this);

                } catch (JSONException exp){
                    exp.printStackTrace();
                }

            }
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

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);

        int size = final_image_list.length()>10?10:final_image_list.length();
        ArrayList<Uri> imageUriArray = new ArrayList<>();
        for(int index = 0; index<size;index++){
            try {
                String image_name = "";
                JSONObject list_element = final_image_list.getJSONObject(index);
                image_name = list_element.getString("filename");
                if(list_element!=null){
                    boolean isVideo = list_element.getBoolean("is_video");
                    if (isVideo) {
                        mFileURL = list_element.getString("video_url");
                        ++size;
                        continue;
                    }

                    File myFilesDir = Environment.getExternalStorageDirectory().getAbsoluteFile();
                    File instaHackDir = new File(myFilesDir, "InstaHack");
                    File userDir = new File(instaHackDir, mUserName);
                    File file = new File(userDir, image_name);

                    Uri uri = Uri.parse(file.getAbsolutePath());

                    imageUriArray.add(uri);


                }



            } catch (JSONException exp){
                exp.printStackTrace();
            }
        }
        intent.putExtra(Intent.EXTRA_STREAM,imageUriArray);
        intent.setType("image/jpeg");

        intent.setPackage("com.whatsapp");
        startActivity(intent);
    }

    private void showImage(JSONObject jsonObject){

        if(jsonObject!=null) {
            instahack_progressbar.setVisibility(View.GONE);
//            instahack_image_field.setVisibility(View.VISIBLE);
            instahack_download_button.setVisibility(View.VISIBLE);
            instahack_share_button.setVisibility(View.VISIBLE);
            try {
                String username = jsonObject.getString("username");
                JSONArray list = jsonObject.getJSONArray("list");

                Log.e("username", username);
                mUserName = username;
                final_image_list = list;
                for(int index = 0; index<list.length();index++){
                    JSONObject list_element = list.optJSONObject(index);
                    if(list_element!=null){
                        isVideo = list_element.getBoolean("is_video");
                        String image_url = list_element.getString("imagelink");
                        String thumbnail_link = list_element.getString("thumbnail_link");
                        if (isVideo) {
                            mFileURL = list_element.getString("video_url");
                        } else {
                            mFileURL = list_element.getString("imagelink");
                        }

                        imageListAdapter.addUrl(list_element);

                        mImageName = list_element.getString("filename");
                        Log.e("thumbnail_link", thumbnail_link);
                    }

                }

                Log.e("ImageListAdapter", "Size of image list: "+imageListAdapter.getItemCount());

                imageListAdapter.notifyDataSetChanged();

            } catch (JSONException exp) {
                exp.printStackTrace();
                mFileURL = mImageName = mUserName = null;
                isVideo = false;
            }
        }
    }

    private void performDownloadCompletedOperation(){
        ++no_of_files_downloaded;
        if(no_of_files_downloaded == no_of_files_to_download){
            fileDownloaded = true;
            instahack_progressbar.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, "Download Completed", Toast.LENGTH_SHORT).show();
            if(fileShareInQueue){
                shareImage();
            }
        }
    }

    private void openImageScreen(int position){

        try {
            JSONObject jsonObject = final_image_list.getJSONObject(position);
            Intent intent = new Intent(this, ImageViewActivity.class);
            intent.putExtra("IMAGE_LINK", jsonObject.getString("imagelink"));
            intent.putExtra("FILE_NAME", jsonObject.getString("filename"));
            intent.putExtra("IS_VIDEO", jsonObject.getBoolean("is_video"));
            intent.putExtra("VIDEO_URL", jsonObject.getString("video_url"));
            intent.putExtra("USER_NAME", mUserName);

            startActivity(intent);
        }catch (JSONException exp){
            exp.printStackTrace();
        }
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
            /*case instahack_image_field:
                openImageScreen(0);
                break;*/
        }
    }

    @Override
    public void onResponse(int code, JSONObject jsonObject) {
        switch (code){
            case CALLBACK_PARSE_LINK:
                showImage(jsonObject);
                imageListAdapter.notifyDataSetChanged();
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

    @Override
    public void onItemClick(int position) {
        openImageScreen(position);
    }
}


