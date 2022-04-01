package com.tools.photolab.effect.erase_tool.eraser;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.internal.view.SupportMenu;

import com.tools.photolab.R;
import com.tools.photolab.effect.erase_tool.StickerEraseActivity;
import com.tools.photolab.effect.erase_tool.ImageUtils;

public class EraseSView extends AppCompatImageView {
    private Paint bPaint = null;
    private Bitmap bit2;
    int bitmappx;
    private int brushSize;
    int canvaspx;
    private Context context;
    private boolean isRectEnable = false;
    private boolean needToDraw = false;
    private boolean onLeft = false;
    Paint p = new Paint();
    private int screenWidth;
    private Paint shaderPaint = null;

    public EraseSView(Context context2) {
        super(context2);
        initVariables(context2);
    }

    public EraseSView(Context context2, @Nullable AttributeSet attributeSet) {
        super(context2, attributeSet);
        initVariables(context2);
    }

    public EraseSView(Context context2, @Nullable AttributeSet attributeSet, int i) {
        super(context2, attributeSet, i);
        initVariables(context2);
    }

    public void initVariables(Context context2) {
        this.context = context2;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context2).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        this.screenWidth = displayMetrics.widthPixels;
        this.bitmappx = ImageUtils.dpToPx(context2, 150.0f);
        this.canvaspx = ImageUtils.dpToPx(context2, 75.0f);
        this.p.setColor(SupportMenu.CATEGORY_MASK);
        Paint paint = this.p;
        double dpToPx = (double) ImageUtils.dpToPx(getContext(), 1.0f);
        Double.isNaN(dpToPx);
        paint.setStrokeWidth((float) (dpToPx * 1.5d));
        this.bPaint = new Paint();
        this.bPaint.setAntiAlias(true);
        this.bPaint.setColor(SupportMenu.CATEGORY_MASK);
        this.bPaint.setAntiAlias(true);
        this.bPaint.setStyle(Style.STROKE);
        this.bPaint.setStrokeJoin(Join.MITER);
        Paint paint2 = this.bPaint;
        double dpToPx2 = (double) ImageUtils.dpToPx(getContext(), 2.0f);
        Double.isNaN(dpToPx2);
        paint2.setStrokeWidth((float) (dpToPx2 * 1.5d));
        this.bit2 = BitmapFactory.decodeResource(context2.getResources(), R.drawable.circle1);
        try {
            this.bit2 = Bitmap.createScaledBitmap(this.bit2, this.bitmappx, this.bitmappx, true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.needToDraw && this.shaderPaint != null) {
            if (this.onLeft) {
                canvas.drawBitmap(StickerEraseActivity.bgCircleBit, 0.0f, 0.0f, null);
                int i = this.canvaspx;
                canvas.drawCircle((float) i, (float) i, (float) i, this.shaderPaint);
                if (EraseView.MODE == 2 || EraseView.MODE == 3) {
                    int i2 = this.canvaspx;
                    canvas.drawCircle((float) i2, (float) i2, (float) this.brushSize, this.bPaint);
                    int i3 = this.canvaspx;
                    int i4 = this.brushSize;
                    Canvas canvas2 = canvas;
                    canvas2.drawLine((float) (i3 - i4), (float) i3, (float) (i4 + i3), (float) i3, this.p);
                    int i5 = this.canvaspx;
                    float f = (float) i5;
                    int i6 = this.brushSize;
                    canvas2.drawLine(f, (float) (i5 - i6), (float) i5, (float) (i5 + i6), this.p);
                } else if (this.isRectEnable) {
                    int i7 = this.canvaspx;
                    int i8 = this.brushSize;
                    canvas.drawRect((float) (i7 - i8), (float) (i7 - i8), (float) (i7 + i8), (float) (i7 + i8), this.bPaint);
                } else {
                    int i9 = this.canvaspx;
                    canvas.drawCircle((float) i9, (float) i9, (float) this.brushSize, this.bPaint);
                }
                try {
                    canvas.drawBitmap(this.bit2, 0.0f, 0.0f, null);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                return;
            }
            canvas.drawBitmap(StickerEraseActivity.bgCircleBit, (float) (this.screenWidth - this.bitmappx), 0.0f, null);
            int i10 = this.screenWidth;
            int i11 = this.canvaspx;
            canvas.drawCircle((float) (i10 - i11), (float) i11, (float) i11, this.shaderPaint);
            if (EraseView.MODE == 2 || EraseView.MODE == 3) {
                int i12 = this.screenWidth;
                int i13 = this.canvaspx;
                canvas.drawCircle((float) (i12 - i13), (float) i13, (float) this.brushSize, this.bPaint);
                int i14 = this.screenWidth;
                int i15 = this.canvaspx;
                int i16 = i14 - i15;
                int i17 = this.brushSize;
                canvas.drawLine((float) (i16 - i17), (float) i15, (float) ((i14 - i15) + i17), (float) i15, this.p);
                int i18 = this.screenWidth;
                int i19 = this.canvaspx;
                float f2 = (float) (i18 - i19);
                int i20 = this.brushSize;
                canvas.drawLine(f2, (float) (i19 - i20), (float) (i18 - i19), (float) (i19 + i20), this.p);
            } else if (this.isRectEnable) {
                int i21 = this.screenWidth;
                int i22 = this.canvaspx;
                int i23 = i21 - i22;
                int i24 = this.brushSize;
                canvas.drawRect((float) (i23 - i24), (float) (i22 - i24), (float) ((i21 - i22) + i24), (float) (i22 + i24), this.bPaint);
            } else {
                int i25 = this.screenWidth;
                int i26 = this.canvaspx;
                canvas.drawCircle((float) (i25 - i26), (float) i26, (float) this.brushSize, this.bPaint);
            }
            try {
                canvas.drawBitmap(this.bit2, (float) (this.screenWidth - this.bitmappx), 0.0f, null);
            } catch (NullPointerException e2) {
                e2.printStackTrace();
            }
        }
    }

    public void updateShaderView(Paint paint, int i, boolean z, boolean z2, boolean z3) {
        this.needToDraw = z;
        this.onLeft = z2;
        this.isRectEnable = z3;
        this.shaderPaint = paint;
        this.brushSize = i;
        invalidate();
    }
}
