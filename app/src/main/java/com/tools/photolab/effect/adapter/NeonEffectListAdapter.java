package com.tools.photolab.effect.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.bumptech.glide.Glide;
import com.tools.photolab.R;
import com.tools.photolab.effect.callBack.MenuItemClickLister;
import com.tools.photolab.effect.custom.CustomTextView;

import java.util.ArrayList;

public class NeonEffectListAdapter extends Adapter<NeonEffectListAdapter.ViewHolder> {

    public MenuItemClickLister clickListener;
    public int selectedPos = 0;
    Context mContext;
    private ArrayList<String> mSpiralIcons = new ArrayList<>();

    public NeonEffectListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void addData(ArrayList<String> arrayList) {
        this.mSpiralIcons.clear();
        this.mSpiralIcons.addAll(arrayList);
        notifyDataSetChanged();
    }

    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.raw_neon_item, viewGroup, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mSelectedBorder.setVisibility(position == selectedPos ? View.VISIBLE : View.GONE);

        String sb2 = "file:///android_asset/spiral/thumb/" + mSpiralIcons.get(position) + ".jpg";
        Glide.with(mContext)
                .load(sb2)
                .fitCenter()
                .into(holder.mIvImage);
    }

    public int getItemCount() {
        return mSpiralIcons.size();
    }

    public ArrayList<String> getItemList() {
        return mSpiralIcons;
    }

    public void setClickListener(MenuItemClickLister menuItemClickLister) {
        this.clickListener = menuItemClickLister;
    }

    public class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder implements OnClickListener {

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
