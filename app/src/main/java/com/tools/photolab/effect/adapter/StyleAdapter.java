package com.tools.photolab.effect.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.tools.photolab.R;
import com.tools.photolab.effect.custom.CustomTextView;
import com.tools.photolab.effect.callBack.PIXStyleClickListener;
import com.tools.photolab.effect.model.PathModelPix;


import java.util.ArrayList;

public class StyleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public int selectedPos = 0;
    Context context;
    private PIXStyleClickListener filterItemClickListener;
    ArrayList<PathModelPix> arrIcon = new ArrayList<>();

    public StyleAdapter(Context context, ArrayList<PathModelPix> arrIcon, PIXStyleClickListener filterItemClickListener) {
        this.context = context;
        this.filterItemClickListener = filterItemClickListener;
        this.arrIcon = arrIcon;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_item_option, parent, false);
        return new CommonHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        CommonHolder holder = (CommonHolder) viewHolder;
        holder.mSelectedBorder.setVisibility(position == selectedPos ? View.VISIBLE : View.GONE);

        if(!arrIcon.get(position).getPathString().equalsIgnoreCase("offLine")){

            String pathLoad = arrIcon.get(position).getPathString();
            Glide.with(context)
                    .asBitmap()
                    .load(pathLoad)
                    .into(holder.ivImage);
        }else {
            int pathLoadOff = arrIcon.get(position).getPathInt();
            Glide.with(context)
                    .asBitmap()
                    .load(pathLoadOff)
                    .into(holder.ivImage);
        }
    }


    public void setSelectedPos(int pos) {
        int p = pos;
        this.selectedPos = pos;
        notifyItemChanged(p);
        notifyItemChanged(selectedPos);
    }

    @Override
    public int getItemCount() {
        return arrIcon.size();
    }

    class CommonHolder extends RecyclerView.ViewHolder {

        ImageView ivImage;
        CustomTextView tvFilterName;
        CustomTextView mSelectedBorder;

        CommonHolder(View view) {
            super(view);
            ivImage = (ImageView) view.findViewById(R.id.img_filter);
            tvFilterName = (CustomTextView) view.findViewById(R.id.tv_filter);
            mSelectedBorder = (CustomTextView) view.findViewById(R.id.selectedBorder);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int p = selectedPos;
                    selectedPos = getAdapterPosition();
                    notifyItemChanged(p);
                    notifyItemChanged(selectedPos);
                    filterItemClickListener.onFilterClicked(getAdapterPosition());
                }
            });
        }
    }
}
