package com.tools.photolab.effect.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.tools.photolab.R;
import com.tools.photolab.effect.custom.SquareImageView;
import com.tools.photolab.effect.callBack.StickerClickListener;

import java.util.List;

public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.ViewHolder> {
    private Context mContext;
    private List<Integer> stickerList;
    private LayoutInflater mInflater;
    StickerClickListener listener;

    public StickerAdapter(Context mContext, List<Integer> mStickerList, StickerClickListener clickListener) {
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
        this.stickerList = mStickerList;
        this.listener = clickListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.raw_sticker_pix_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (position < stickerList.size()) {
            holder.ivSticker.setImageResource(stickerList.get(position));
        }
    }


    @Override
    public int getItemCount() {
        return stickerList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public SquareImageView ivSticker;

        public ViewHolder(View itemView) {
            super(itemView);
            ivSticker = (SquareImageView) itemView.findViewById(R.id.iv_sticker);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) listener.onItemClick(view, getAdapterPosition());
        }
    }
}
