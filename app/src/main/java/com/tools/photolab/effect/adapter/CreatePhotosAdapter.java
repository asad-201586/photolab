package com.tools.photolab.effect.adapter;

import android.content.Context;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.tools.photolab.R;
import com.tools.photolab.effect.custom.SquareImageView;
import com.tools.photolab.effect.callBack.PhotoPixClickListener;


import java.io.File;
import java.util.ArrayList;

public class CreatePhotosAdapter extends RecyclerView.Adapter<CreatePhotosAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<File> myPhotosFileList;
    private LayoutInflater mInflater;
    private PhotoPixClickListener photoViewClickListener;

    public CreatePhotosAdapter(Context mContext, ArrayList<File> myPhotosFileList, PhotoPixClickListener photoViewClickListener) {
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
        this.photoViewClickListener = photoViewClickListener;
        this.myPhotosFileList = myPhotosFileList;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.raw_my_create_photos, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(mContext).load(Uri.fromFile(myPhotosFileList.get(position)))
                .into(holder.ivPhotos);
    }

    @Override
    public int getItemCount() {
        return myPhotosFileList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public SquareImageView ivPhotos;

        public ViewHolder(View itemView) {
            super(itemView);
            ivPhotos = (SquareImageView) itemView.findViewById(R.id.iv_photos);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (photoViewClickListener != null)
                photoViewClickListener.onPhotoClick(myPhotosFileList.get(getAdapterPosition()).getAbsolutePath());
        }
    }
}
