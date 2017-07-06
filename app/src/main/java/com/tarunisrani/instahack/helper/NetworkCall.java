package com.tarunisrani.instahack.helper;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.tarunisrani.instahack.listeners.NetworkResponseListener;

import org.json.JSONObject;

/**
 * Created by tarunisrani on 1/31/17.
 */
public class NetworkCall {

    public void getFileList(Context context, String url, String next_cursor, final NetworkResponseListener listener){

        String final_url = "http://funstuff.co.in/instahack/api/filelist.php?url="+url+"&next_cursor="+next_cursor;

        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, final_url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onResponseReceived(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onErrorReceived(error);
            }
        });
//        request.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);

        queue.add(request);
    }

    public void getJSONDetails(int code, String url, String end_cursor, NetworkCallListener listener){
        new PageParser(code, url, end_cursor, listener).execute();
    }

    public void downloadFile(int code, String url, String userName, String fileName, NetworkCallListener listener){
        new DownloadFile(code, url, userName, fileName, listener).execute();
    }

}



