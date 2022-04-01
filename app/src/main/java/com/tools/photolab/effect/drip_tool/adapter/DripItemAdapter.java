package com.tools.photolab.effect.drip_tool.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tools.photolab.R;
import com.tools.photolab.effect.callBack.MenuItemClickLister;
import com.tools.photolab.effect.custom.CustomTextView;


import java.util.ArrayList;

public class DripItemAdapter extends RecyclerView.Adapter<DripItemAdapter.ViewHolder> {
    public MenuItemClickLister clickListener;
    public int selectedPos = 0;
    private ArrayList<String> dripItemList = new ArrayList<>();
    Context mContext ;

    public DripItemAdapter(Context context) {
        mContext = context;
    }

    public void addData(ArrayList<String> arrayList) {
        this.dripItemList.clear();
        this.dripItemList.addAll(arrayList);
        notifyDataSetChanged();
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.raw_neon_item, viewGroup, false));
    }

    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mSelectedBorder.setVisibility(position == selectedPos ? View.VISIBLE : View.GONE);
        String sb2 = "file:///android_asset/drip/thumb/" + dripItemList.get(position) +/* (position == 0 ? ".png" : */"_icon.jpg";
        Glide.with(mContext)
                .load(sb2)
                .fitCenter()
                .into(holder.mIvImage);
    }

    public int getItemCount() {
        return this.dripItemList.size();
    }

    public void setClickListener(MenuItemClickLister clickListener) {
        this.clickListener = clickListener;
    }

    public ArrayList<String> getItemList() {
        return dripItemList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        CustomTextView mSelectedBorder;
        ImageView mIvImage;
        CustomTextView mTvFilterName;

        ViewHolder(View view) {
            super(view);
            mIvImage = (ImageView) view.findViewById(R.id.img_filter_icon);
            mTvFilterName = (CustomTextView) view.findViewById(R.id.tv_filter_name);
            mTvFilterName.setVisibility(View.GONE);
            mSelectedBorder = (CustomTextView) view.findViewById(R.id.selectedBorder);
            view.setTag(view);
            view.setOnClickListener(this);
        }

        public void onClick(View view) {
            int p = selectedPos;
            selectedPos = getAdapterPosition();
            notifyItemChanged(p);
            notifyItemChanged(selectedPos);
            clickListener.onMenuListClick(view, getAdapterPosition());
        }
    }
}
