package com.tarunisrani.instahack.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tarunisrani.instahack.R;
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

    public void addUrl(JSONObject jsonObject){
        mList.add(jsonObject);
    }

    public JSONObject getItem(int position){
        return mList.get(position);
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

        private ImageView instahack_list_image_field;

        public void setImage(Context context, JSONObject jsonObject) throws JSONException{

            String image_url = jsonObject.getString("imagelink");

            /*if (isVideo) {
                file_url = jsonObject.getString("video_url");
            } else {
                file_url = jsonObject.getString("imagelink");
            }*/

            Picasso.with(context).load(image_url).into(instahack_list_image_field, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {
                    Log.e("Error", "Error occurred while loading image");
                }
            });
        }

        public ViewHolder(View itemView) {
           super(itemView);
            this.instahack_list_image_field = (ImageView) itemView.findViewById(R.id.instahack_list_image_field);
        }
    }
}
