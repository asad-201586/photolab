package com.tools.photolab.effect.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.tools.photolab.R;
import com.tools.photolab.effect.callBack.StickerClickListener;

import java.util.ArrayList;


public class StickerCategoryListAdapter extends RecyclerView.Adapter<StickerCategoryListAdapter.ViewHolder> {

    ArrayList<Integer> stickerList;
    StickerClickListener listener;
    Activity context;


    public StickerCategoryListAdapter(Activity activity, ArrayList<Integer >mstickerList, StickerClickListener clickListener) {
        super();
        this.context = activity;
        this.stickerList = mstickerList;
        this.listener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.raw_sticker_new, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {

        viewHolder.ivImage.setImageResource(stickerList.get(i));
    }


    @Override
    public int getItemCount() {
        return stickerList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivImage;
        RelativeLayout rel_main;


        public ViewHolder(final View itemView) {
            super(itemView);
            ivImage = (ImageView) itemView.findViewById(R.id.iv_image);
            rel_main = (RelativeLayout) itemView.findViewById(R.id.rel_main);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TAG", "clicked position:" + getLayoutPosition());
                    listener.onItemClick(itemView, getLayoutPosition());
                }
            });
        }
    }
}
