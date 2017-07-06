package com.tarunisrani.instahack.listeners;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by tarunisrani on 7/5/17.
 */

public interface NetworkResponseListener {
    void onResponseReceived(JSONObject jsonObject);
    void onErrorReceived(VolleyError error);
}
