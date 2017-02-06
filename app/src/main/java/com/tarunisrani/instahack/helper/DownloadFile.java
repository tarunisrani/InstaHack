package com.tarunisrani.instahack.helper;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DownloadFile extends AsyncTask<Integer, String, JSONObject> {

    private int mCode = -1;
    private String mUrl;
    private String mFileName = "test.jpg";
    private NetworkCallListener mListener;

    public DownloadFile(int code, String url, String fileName,  NetworkCallListener listener) {
        this.mCode = code;
        this.mUrl = url;
        this.mFileName = fileName;
        this.mListener = listener;
    }

    @Override
    protected JSONObject doInBackground(Integer... params) {
//        String filename = "image.jpg";
        File myFilesDir = Environment.getExternalStorageDirectory().getAbsoluteFile();
        File file = new File(myFilesDir, mFileName);

        Log.e("DownloadFile", "Started downloading: " + mUrl);

        int count;

        URL url = null;
        try {
            url = new URL(mUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        /*HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {

            con.setDoOutput(true);


            InputStream is = con.getInputStream();
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fos = new FileOutputStream(file);
            byte data[] = new byte[4096];
            int count;
            while ((count = is.read(data)) != -1) {
                fos.write(data, 0, count);
            }
            is.close();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }*/

        int lenghtOfFile = 0;
        try{
            URLConnection conection = url.openConnection();
            conection.connect();
            // getting file length
            lenghtOfFile = conection.getContentLength();

            // input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            // Output stream to write file
//            OutputStream output = new FileOutputStream("/sdcard/downloadedfile.jpg");
            OutputStream output = new FileOutputStream(file);

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress(""+(int)((total*100)/lenghtOfFile));

                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();
        } catch (Exception exp){
            exp.printStackTrace();
        }

        Log.e("DownloadFile", "Completed");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("filename", mFileName);
            jsonObject.put("filesize", lenghtOfFile);
        }catch (JSONException exp){
            exp.printStackTrace();
        }

        return jsonObject;
    }

    @Override
    protected void onPostExecute(JSONObject onTaskCompleted) {
        if(mListener!=null){
            mListener.onResponse(mCode, onTaskCompleted);
        }
    }

}