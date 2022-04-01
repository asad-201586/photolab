package com.tools.photolab.effect.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tools.photolab.R;
import com.tools.photolab.effect.custom.CustomTextView;
import com.tools.photolab.effect.callBack.FilterPixItemClickListener;


import com.zomato.photofilters.utils.ThumbnailItem;

import java.util.List;

public class FiltersForegroundAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ThumbnailItem> filterModelList;
    public int selectedPos = 0;
    private FilterPixItemClickListener filterItemClickListener;
    private int rotateImage;

    public FiltersForegroundAdapter(List<ThumbnailItem> filterModelList, int rotateImage, FilterPixItemClickListener filterItemClickListener) {
        this.filterModelList = filterModelList;
        this.filterItemClickListener = filterItemClickListener;
        this.rotateImage = rotateImage;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_neon_item, parent, false);
        return new CommonHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        CommonHolder holder = (CommonHolder) viewHolder;
        ThumbnailItem thumbnailItem = filterModelList.get(position);
        holder.mSelectedBorder.setVisibility(position == selectedPos ? View.VISIBLE : View.GONE);
        holder.mIvImage.setRotation(rotateImage);
        holder.mIvImage.setImageBitmap(thumbnailItem.image);
        holder.mTvFilterName.setText(thumbnailItem.filterName);
    }

    public void setSelectedFirstPos() {
        int p = selectedPos;
        this.selectedPos = 0;
        notifyItemChanged(p);
        notifyItemChanged(selectedPos);
    }

    @Override
    public int getItemCount() {
        return filterModelList.size();
    }

    class CommonHolder extends RecyclerView.ViewHolder {
        CustomTextView mSelectedBorder;
        ImageView mIvImage;
        CustomTextView mTvFilterName;
        CommonHolder(View view) {
            super(view);
            mIvImage = (ImageView) view.findViewById(R.id.img_filter_icon);
            mTvFilterName = (CustomTextView) view.findViewById(R.id.tv_filter_name);
            mSelectedBorder = (CustomTextView) view.findViewById(R.id.selectedBorder);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int p = selectedPos;
                    selectedPos = getAdapterPosition();
                    notifyItemChanged(p);
                    notifyItemChanged(selectedPos);
                    filterItemClickListener.onFilterClicked(filterModelList.get(getAdapterPosition()).filter);
                }
            });
        }
    }
}
