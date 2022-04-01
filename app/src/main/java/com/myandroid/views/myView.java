
package com.myandroid.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.widget.ImageView;

public class myView extends ImageView {
    private static final int PADDING = 0;
    private static final float STROKE_WIDTH = 0.0f;
    private Paint mBorderPaint;

    public myView(Context context) {
        this(context, null);
    }

    public myView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
        setPadding(0, 0, 0, 0);
    }

    public myView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initBorderPaint();
    }

    private void initBorderPaint() {
        Paint paint = new Paint();
        this.mBorderPaint = paint;
        paint.setAntiAlias(true);
        this.mBorderPaint.setStyle(Style.STROKE);
        this.mBorderPaint.setColor(0);
        this.mBorderPaint.setStrokeWidth(0.0f);
    }


    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0.0f, 0.0f, (float) (getWidth() + 0), (float) (getHeight() + 0), this.mBorderPaint);
    }
}
