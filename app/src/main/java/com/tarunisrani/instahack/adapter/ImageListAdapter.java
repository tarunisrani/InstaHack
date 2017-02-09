package com.tarunisrani.instahack.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.tarunisrani.instahack.R;
import com.tarunisrani.instahack.helper.MySingleton;
import com.tarunisrani.instahack.listeners.ImageListClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by tarunisrani on 12/22/16.
 */
public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {

    public void setmListener(ImageListClickListener mListener) {
        this.mListener = mListener;
    }

    private ImageListClickListener mListener;

    private ArrayList<JSONObject> mList = new ArrayList<>();

    private Context mContext;
    private ImageLoader imageLoader;

    public void addUrl(JSONObject jsonObject){
        mList.add(jsonObject);
    }

    public JSONObject getItem(int position){
        return mList.get(position);
    }

    public void clear(){
        mList.clear();
    }

    public JSONObject removeItem(int position){
        return mList.remove(position);
    }

    public ImageListAdapter(Context context){
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_list_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        JSONObject jsonObject = mList.get(position);
        try {
            holder.setImage(mContext, jsonObject);
        }catch (JSONException exp){
            exp.printStackTrace();
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener!=null){
                    mListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private NetworkImageView instahack_list_image_field;

        public void setImage(final Context context, JSONObject jsonObject) throws JSONException{

            String salt = String.valueOf(System.currentTimeMillis());

            final String thumbnail_url = jsonObject.getString("thumbnail_link");//+"?"+salt;
            final String image_url = jsonObject.getString("imagelink");//+"?"+salt;


            imageLoader = MySingleton.getInstance(mContext)
                    .getImageLoader();
            imageLoader.get((thumbnail_url!=null && !thumbnail_url.isEmpty())?thumbnail_url:image_url, ImageLoader.getImageListener(instahack_list_image_field,
                    android.R.drawable.ic_menu_gallery, android.R.drawable
                            .ic_dialog_alert));
            instahack_list_image_field.setImageUrl((thumbnail_url!=null && !thumbnail_url.isEmpty())?thumbnail_url:image_url, imageLoader);

        }

        public ViewHolder(View itemView) {
           super(itemView);
            this.instahack_list_image_field = (NetworkImageView) itemView.findViewById(R.id.instahack_list_image_field);
        }
    }
}
