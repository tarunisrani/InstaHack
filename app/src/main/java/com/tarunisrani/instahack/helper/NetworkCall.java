package com.tarunisrani.instahack.helper;

/**
 * Created by tarunisrani on 1/31/17.
 */
public class NetworkCall {

    public void getJSONDetails(int code, String url, NetworkCallListener listener){
        new PageParser(code, url, listener).execute();
    }

    public void downloadFile(int code, String url, String userName, String fileName, NetworkCallListener listener){
        new DownloadFile(code, url, userName, fileName, listener).execute();
    }

}



