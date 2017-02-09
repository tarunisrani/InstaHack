package com.tarunisrani.instahack.helper;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class PageParser extends AsyncTask<String, String, JSONObject> {

    private int mCode = -1;
    private NetworkCallListener mListener;
    private String mUrl;
    private boolean mParseAllPages;

    public PageParser(int code, String url, boolean parseAllPages, NetworkCallListener listener){
        this.mCode = code;
        this.mListener = listener;
        this.mUrl = url;
        this.mParseAllPages = parseAllPages;
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

        JSONObject image_details = new JSONObject();
        String extended_url = "?max_id=";

        boolean has_next_page = true;
        String end_cursor = "";
        String username = "default_user";

        JSONArray list_of_images = new JSONArray();

        while(has_next_page) {

            extended_url = "?max_id="+end_cursor;
            Log.e("Parsing page", mUrl + extended_url);
            Document doc = Jsoup.connect(mUrl + extended_url).get();
            Elements links = doc.select("script[type]");
            for (Element link : links) {
                if (link.attributes().size() == 1 && link.data().contains("window._sharedData")) {
                    String jsonString = link.data().substring(21).replaceAll("\\;", "");
                    System.out.println("text : " + jsonString);
                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONObject entry_data = jsonObject.getJSONObject("entry_data");

                    if (isProfilePage(entry_data)) {
                        JSONArray ProfilePage = entry_data.getJSONArray("ProfilePage");
                        JSONObject user = getUser(ProfilePage.getJSONObject(0));
                        username = getUserName(user);
                        JSONObject media = getMedia(user);
                        JSONArray nodes = getNodes(media);
                        for (int index = 0; index < nodes.length(); index++) {
                            JSONObject node = nodes.getJSONObject(index);
                            list_of_images.put(processPostPageFromProfile(node));
                        }
                        JSONObject page_info = getPageInfo(media);

                        if(mParseAllPages){
                            has_next_page = page_info.optBoolean("has_next_page", false);
                            end_cursor = page_info.getString("end_cursor");
                        }else{
                            has_next_page = false;
                            end_cursor = null;
                        }
                    } else if (isPostPage(entry_data)) {
                        JSONArray PostPage = entry_data.getJSONArray("PostPage");
                        JSONObject media = getMedia(PostPage.getJSONObject(0));
                        JSONObject owner = getOwner(media);
                        username = getUserName(owner);
                        list_of_images.put(processPostPage(media));
                        JSONObject comments = getComments(media);
                        JSONObject page_info = getPageInfo(comments);
                        has_next_page = false;
                        end_cursor = "";
                    }

//                image_link = processJSON(entry_data);
                }
            }
        }

        image_details.put("username", username);
        image_details.put("list", list_of_images);

        return image_details;
    }

    private boolean isProfilePage(JSONObject entry_data){
        return entry_data.keys().next().equalsIgnoreCase("ProfilePage");
    }

    private boolean isPostPage(JSONObject entry_data){
        return entry_data.keys().next().equalsIgnoreCase("PostPage");
    }


    private JSONObject processPostPage(JSONObject media) throws JSONException {
        String link = "";
        String thumbnail_link = "";
        String code = "";
        String video_url = "";
        boolean is_video = false;

//        JSONObject media = getMedia(jsonObject);
        is_video = isVideo(media);
        if(is_video){
            video_url = getVideoUrl(media);
        }
        thumbnail_link = getThumbnailSrc(media);
        link = getDisplaySrc(media);
        code = getCode(media);

        System.out.println("Link : " + link);

        JSONObject fileDetail = new JSONObject();
        fileDetail.put("thumbnail_link", thumbnail_link!=null?thumbnail_link:"");
        fileDetail.put("imagelink", link);
        fileDetail.put("filename", code+(is_video?".mp4":".jpg"));
        fileDetail.put("is_video", is_video);
        fileDetail.put("video_url", video_url);

        return fileDetail;
    }


    private JSONObject processPostPageFromProfile(JSONObject jsonProfileObject) throws JSONException {

        JSONObject final_jsonObject = null;

        String code = "";
        boolean is_video = false;

        is_video = isVideo(jsonProfileObject);

        code = getCode(jsonProfileObject);
        if(is_video){

            try {
                String url = "https://www.instagram.com/p/" + code + "/";
                Document doc = Jsoup.connect(url).get();
                Elements links = doc.select("script[type]");

                for (Element video_link : links) {
                    if (video_link.attributes().size() == 1 && video_link.data().contains("window._sharedData")) {
                        String jsonString = video_link.data().substring(21).replaceAll("\\;", "");
                        JSONObject jsonObject = new JSONObject(jsonString);
                        JSONObject entry_data = jsonObject.getJSONObject("entry_data");
                        JSONArray PostPage = entry_data.getJSONArray("PostPage");
                        JSONObject media = getMedia(PostPage.getJSONObject(0));
                        final_jsonObject = processPostPage(media);
                    }
                }
            } catch (IOException exp){
                exp.printStackTrace();
            }
        }else {
            final_jsonObject = processPostPage(jsonProfileObject);
        }


        return final_jsonObject;
    }


    private JSONObject processJSON(JSONObject entry_data) throws JSONException {
        String link = "";
        String code = "";
        String video_url = "";
        boolean is_video = false;

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

    private String getUserName(JSONObject jsonObject) throws JSONException{
        return jsonObject.getString("username");
    }

    private JSONObject getMedia(JSONObject jsonObject) throws JSONException{
        return jsonObject.getJSONObject("media");
    }

    private JSONObject getOwner(JSONObject jsonObject) throws JSONException{
        return jsonObject.getJSONObject("owner");
    }

    private JSONArray getNodes(JSONObject jsonObject) throws JSONException{
        return jsonObject.getJSONArray("nodes");
    }

    private JSONObject getPageInfo(JSONObject jsonObject) throws JSONException{
        return jsonObject.getJSONObject("page_info");
    }

    private JSONObject getComments(JSONObject jsonObject) throws JSONException{
        return jsonObject.getJSONObject("comments");
    }

    private JSONObject getUser(JSONObject jsonObject) throws JSONException{
        return jsonObject.getJSONObject("user");
    }

    private String getDisplaySrc(JSONObject jsonObject) throws JSONException{
        return jsonObject.getString("display_src");
    }

    private String getThumbnailSrc(JSONObject jsonObject) throws JSONException{
        if(jsonObject.has("thumbnail_src")){
            return jsonObject.optString("thumbnail_src", null);
        }
        return null;
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