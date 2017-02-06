package com.tarunisrani.instahack.helper;

import org.json.JSONObject;

public interface NetworkCallListener {
    void onResponse(int code, JSONObject imageUrl);
}