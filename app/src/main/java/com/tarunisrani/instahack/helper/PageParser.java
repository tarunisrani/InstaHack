package com.tarunisrani.instahack.helper;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PageParser extends AsyncTask<String, String, JSONObject> {

    private int mCode = -1;
    private NetworkCallListener mListener;
    private String mUrl;

    public PageParser(int code, String url, NetworkCallListener listener){
        this.mCode = code;
        this.mListener = listener;
        this.mUrl = url;
    }

    @Override
    protected JSONObject doInBackground(String... params) {

        try {
            return checkForImageDetails();
        } catch (Exception exp) {
            exp.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(JSONObject appVersion) {
        if(mListener!=null){
            mListener.onResponse(mCode, appVersion);
        }
    }


    private JSONObject checkForImageDetails() throws Exception {
        JSONObject image_link = null;
            Document doc = Jsoup.connect(mUrl).get();
            Elements links = doc.select("script[type]");
            for (Element link : links) {
                if(link.attributes().size() == 1 && link.data().contains("window._sharedData")) {
                    String jsonString = link.data().substring(21).replaceAll("\\;", "");
                    System.out.println("text : " + jsonString);
                    JSONObject jsonObject = new JSONObject(jsonString);
                    image_link = processJSON(jsonObject);
                }
            }
        return image_link;
    }

    private JSONObject processJSON(JSONObject jsonObject) throws JSONException {
        String link = "";
        String code = "";
        String video_url = "";
        boolean is_video = false;
        JSONObject entry_data = jsonObject.getJSONObject("entry_data");
        String key = entry_data.keys().next();
        if(key.equalsIgnoreCase("ProfilePage")){
            JSONArray ProfilePage = entry_data.getJSONArray("ProfilePage");
        }else if(key.equalsIgnoreCase("PostPage")){
            JSONArray PostPage = entry_data.getJSONArray("PostPage");
            JSONObject media = getMedia(PostPage.getJSONObject(0));
            is_video = isVideo(media);
            if(is_video){
                video_url = getVideoUrl(media);
            }
            link = getDisplaySrc(media);
            code = getCode(media);

            System.out.println("Link : " + link);
//            JSONObject owner = getOwner(media);
        }

        JSONObject fileDetail = new JSONObject();
        fileDetail.put("imagelink", link);
        fileDetail.put("filename", code+(is_video?".mp4":".jpg"));
        fileDetail.put("is_video", is_video);
        fileDetail.put("video_url", video_url);

        return fileDetail;
    }

    private JSONObject getMedia(JSONObject jsonObject) throws JSONException{
        return jsonObject.getJSONObject("media");
    }

    private JSONObject getOwner(JSONObject jsonObject) throws JSONException{
        return jsonObject.getJSONObject("owner");
    }

    private String getDisplaySrc(JSONObject jsonObject) throws JSONException{
        return jsonObject.getString("display_src");
    }

    private String getVideoUrl(JSONObject jsonObject) throws JSONException{
        return jsonObject.getString("video_url");
    }

    private String getCode(JSONObject jsonObject) throws JSONException{
        return jsonObject.getString("code");
    }

    private boolean isVideo(JSONObject jsonObject) throws JSONException{
        return jsonObject.getBoolean("is_video");
    }
}