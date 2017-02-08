package com.tarunisrani.instahack.utils;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;

public class MyJavaScriptInterface {

    private Context ctx;
    Handler handlerForJavascriptInterface = new Handler();

    public MyJavaScriptInterface(Context ctx) {
        this.ctx = ctx;
    }

    @JavascriptInterface
    public void showHTML(final String html) {
        /*Pattern pattern = Pattern.compile("Load more", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);
        String displayHTML = null;
        while(matcher.find()){
            displayHTML = matcher.group();
        }
        Log.e("Match", displayHTML);*/

        handlerForJavascriptInterface.post(new Runnable()
        {
            @Override
            public void run()
            {
                Log.e("HTML", html);
//                Toast toast = Toast.makeText(ctx, "Page has been loaded in webview. html content :"+html, Toast.LENGTH_LONG);
//                toast.show();
            }
        }
        );

    }

}