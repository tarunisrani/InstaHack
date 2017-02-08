package com.tarunisrani.instahack.utils;

import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MyWebViewClient extends WebViewClient {

    private AppCompatActivity activity;
    private String finalURL;

    public MyWebViewClient(AppCompatActivity activity){
        this.activity = activity;
//        this.finalURL = final_url;
    }


    @Override
    public void onPageFinished(WebView view, String url) {
        view.loadUrl("javascript:window.HTMLOUT.showHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
    }
}