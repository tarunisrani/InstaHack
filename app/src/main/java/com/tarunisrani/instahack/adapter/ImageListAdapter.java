package com.tarunisrani.instahack.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tarunisrani.instahack.R;

import java.util.ArrayList;

/**
 * Created by tarunisrani on 12/22/16.
 */
public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {

    private ArrayList<String> mList = new ArrayList<>();

    private Context mContext;

    public void addUrl(String expense){
        mList.add(expense);
    }

    public String getItem(int position){
        return mList.get(position);
    }

    public String removeItem(int position){
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
        String url = mList.get(position);
        holder.setImage(mContext, url);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView instahack_list_image_field;

        public void setImage(Context context, String url){
            Picasso.with(context).load(url).into(instahack_list_image_field, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {
                }
            });
        }

        public ViewHolder(View itemView) {
           super(itemView);
            this.instahack_list_image_field = (ImageView) itemView.findViewById(R.id.instahack_list_image_field);
        }
    }
}
