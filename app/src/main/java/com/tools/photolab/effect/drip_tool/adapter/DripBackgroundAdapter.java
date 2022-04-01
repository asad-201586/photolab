package com.tools.photolab.effect.drip_tool.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tools.photolab.R;
import com.tools.photolab.effect.activity.DripEffectActivity;

import java.util.List;

import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;


public final class DripBackgroundAdapter extends RecyclerView.Adapter<DripBackgroundAdapter.OverlayViewHolder> {
    private final Context context;
    private final LayoutInflater layoutInflater;
    private final List<Integer> stickerIds;
    private int selectedPosition = -1;

    public DripBackgroundAdapter(Context context2, List<Integer> list) {
        Intrinsics.checkParameterIsNotNull(context2, "context");
        Intrinsics.checkParameterIsNotNull(list, "stickerIds");
        this.context = context2;
        this.stickerIds = list;
        LayoutInflater from = LayoutInflater.from(context2);
        Intrinsics.checkExpressionValueIsNotNull(from, "LayoutInflater.from(context)");
        this.layoutInflater = from;
    }

    public final int getSelectedPosition() {
        return this.selectedPosition;
    }

    public final void setSelectedPosition(int i) {
        this.selectedPosition = i;
    }

    public OverlayViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Intrinsics.checkParameterIsNotNull(viewGroup, "parent");
        View inflate = this.layoutInflater.inflate(R.layout.raw_overlay, viewGroup, false);
        Intrinsics.checkExpressionValueIsNotNull(inflate, "layoutInflater.inflate(R…t_overlay, parent, false)");
        return new OverlayViewHolder(this, inflate);
    }

    public void onBindViewHolder(OverlayViewHolder overlayViewHolder, int i) {
        Intrinsics.checkParameterIsNotNull(overlayViewHolder, "holder");
        Glide.with(this.context).load(Integer.valueOf(getItem(i))).into(overlayViewHolder.getImgeffect());
        if (this.selectedPosition == i) {
            overlayViewHolder.getImgeffect().setBackground(this.context.getResources().getDrawable(R.drawable.highlight));
            return;
        }
        overlayViewHolder.getImgeffect().setBackground((Drawable) null);
    }

    public int getItemCount() {
        return this.stickerIds.size();
    }


    public final int getItem(int i) {
        return this.stickerIds.get(i).intValue();
    }

    public final void onStickerSelected(int i, int i2) {
        if (i != 0) {
            Context context2 = this.context;
            if (context2 != null) {
                ((DripEffectActivity) context2).setBackground(i, i2);
                return;
            }
            throw new TypeCastException("null cannot be cast to non-null type com.my.fakeinstapost.activities.DripEditPhotoActivity");
        }
    }


    public final class OverlayViewHolder extends RecyclerView.ViewHolder {
        final /* synthetic */ DripBackgroundAdapter this$0;
        private ImageView imgeffect;

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        public OverlayViewHolder(DripBackgroundAdapter dripBackgroundAdapter, View view) {
            super(view);
            Intrinsics.checkParameterIsNotNull(view, "itemView");
            this.this$0 = dripBackgroundAdapter;
            View findViewById = view.findViewById(R.id.imgeffect);
            Intrinsics.checkExpressionValueIsNotNull(findViewById, "itemView.findViewById(R.id.imgeffect)");
            this.imgeffect = (ImageView) findViewById;
            view.setOnClickListener(new View.OnClickListener() {
                /* final *//* synthetic *//* OverlayViewHolder this$0;

                {
                    this.this$0 = r1;
                }*/

                public final void onClick(View view) {
                    int adapterPosition = getAdapterPosition();
                    if (adapterPosition >= 0) {
                        this$0.onStickerSelected(getItem(adapterPosition), adapterPosition);
                        this$0.setSelectedPosition(adapterPosition);
                        this$0.notifyDataSetChanged();
                    }
                }
            });
        }

        public final ImageView getImgeffect() {
            return this.imgeffect;
        }

        public final void setImgeffect(ImageView imageView) {
            Intrinsics.checkParameterIsNotNull(imageView, "<set-?>");
            this.imgeffect = imageView;
        }
    }
}
