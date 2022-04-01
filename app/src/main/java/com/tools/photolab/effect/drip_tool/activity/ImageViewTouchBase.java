package com.tools.photolab.effect.drip_tool.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.widget.AppCompatImageView;

import com.tools.photolab.effect.drip_tool.easing.Cubic;
import com.tools.photolab.effect.drip_tool.easing.Easing;
import com.tools.photolab.effect.drip_tool.graphic.FastBitmapDrawable;
import com.tools.photolab.effect.drip_tool.utils.IDisposable;

public abstract class ImageViewTouchBase extends AppCompatImageView implements IDisposable {
    public static final String LOG_TAG = "ImageViewTouchBase";
    protected Matrix mBaseMatrix = new Matrix();
    private boolean mBitmapChanged;
    protected RectF mBitmapRect = new RectF();
    private PointF mCenter = new PointF();
    protected RectF mCenterRect = new RectF();
    protected final Matrix mDisplayMatrix = new Matrix();
    private OnDrawableChangeListener mDrawableChangeListener;
    protected Easing mEasing = new Cubic();
    protected Handler mHandler = new Handler();
    protected Runnable mLayoutRunnable = null;
    protected final float[] mMatrixValues = new float[9];
    private float mMaxZoom = -1.0f;
    private boolean mMaxZoomDefined;
    private float mMinZoom = -1.0f;
    private boolean mMinZoomDefined;
    protected Matrix mNextMatrix;
    private OnLayoutChangeListener mOnLayoutChangeListener;
    protected DisplayType mScaleType = DisplayType.FIT_TO_SCREEN;
    private boolean mScaleTypeChanged;
    protected RectF mScrollRect = new RectF();
    protected Matrix mSuppMatrix = new Matrix();
    private int mThisHeight = -1;
    private int mThisWidth = -1;
    protected boolean mUserScaled = false;

    public enum DisplayType {
        NONE,
        FIT_TO_SCREEN,
        FIT_IF_BIGGER
    }

    public interface OnDrawableChangeListener {
        void onDrawableChanged(Drawable drawable);
    }

    public interface OnLayoutChangeListener {
        void onLayoutChanged(boolean z, int i, int i2, int i3, int i4);
    }

    @SuppressLint({"Override"})
    public float getRotation() {
        return 0.0f;
    }

    public void onImageMatrixChanged() {
    }

    public void onZoom(float f) {
    }

    public void onZoomAnimationCompleted(float f) {
    }

    public ImageViewTouchBase(Context context) {
        super(context);
        init();
    }

    public ImageViewTouchBase(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public void setOnDrawableChangedListener(OnDrawableChangeListener onDrawableChangeListener) {
        this.mDrawableChangeListener = onDrawableChangeListener;
    }

    public void setOnLayoutChangeListener(OnLayoutChangeListener onLayoutChangeListener) {
        this.mOnLayoutChangeListener = onLayoutChangeListener;
    }

    public void init() {
        setScaleType(ScaleType.MATRIX);
    }

    public void setScaleType(ScaleType scaleType) {
        if (scaleType == ScaleType.MATRIX) {
            super.setScaleType(scaleType);
        } else {
            Log.w(LOG_TAG, "Unsupported scaletype. Only MATRIX can be used");
        }
    }

    public void clear() {
        setImageBitmap(null);
    }

    public void setDisplayType(DisplayType displayType) {
        if (displayType != this.mScaleType) {
            this.mUserScaled = false;
            this.mScaleType = displayType;
            this.mScaleTypeChanged = true;
            requestLayout();
        }
    }

    public DisplayType getDisplayType() {
        return this.mScaleType;
    }

    public void setMinScale(float f) {
        this.mMinZoom = f;
    }

    public void setMaxScale(float f) {
        this.mMaxZoom = f;
    }

    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        int i6;
        float f;
        float defaultScale;
        int i7 = i;
        int i8 = i2;
        int i9 = i3;
        int i10 = i4;
        super.onLayout(z, i, i2, i3, i4);
        if (z) {
            int i11 = this.mThisWidth;
            int i12 = this.mThisHeight;
            this.mThisWidth = i9 - i7;
            this.mThisHeight = i10 - i8;
            i6 = this.mThisWidth - i11;
            i5 = this.mThisHeight - i12;
            this.mCenter.x = ((float) this.mThisWidth) / 2.0f;
            this.mCenter.y = ((float) this.mThisHeight) / 2.0f;
        } else {
            i6 = 0;
            i5 = 0;
        }
        Runnable runnable = this.mLayoutRunnable;
        if (runnable != null) {
            this.mLayoutRunnable = null;
            runnable.run();
        }
        Drawable drawable = getDrawable();
        if (drawable == null) {
            if (this.mBitmapChanged) {
                onDrawableChanged(drawable);
            }
            if (z || this.mBitmapChanged || this.mScaleTypeChanged) {
                onLayoutChanged(i7, i8, i9, i10);
            }
            if (this.mBitmapChanged) {
                this.mBitmapChanged = false;
            }
            if (this.mScaleTypeChanged) {
                this.mScaleTypeChanged = false;
            }
        } else if (z || this.mScaleTypeChanged || this.mBitmapChanged) {
            getDefaultScale(this.mScaleType);
            float scale = getScale(this.mBaseMatrix);
            float scale2 = getScale();
            float min = Math.min(1.0f, 1.0f / scale);
            getProperBaseMatrix(drawable, this.mBaseMatrix);
            float scale3 = getScale(this.mBaseMatrix);
            if (this.mBitmapChanged || this.mScaleTypeChanged) {
                if (this.mNextMatrix != null) {
                    this.mSuppMatrix.set(this.mNextMatrix);
                    this.mNextMatrix = null;
                    defaultScale = getScale();
                } else {
                    this.mSuppMatrix.reset();
                    defaultScale = getDefaultScale(this.mScaleType);
                }
                f = defaultScale;
                setImageMatrix(getImageViewMatrix());
                if (f != getScale()) {
                    zoomTo(f);
                }
            } else if (z) {
                if (!this.mMinZoomDefined) {
                    this.mMinZoom = -1.0f;
                }
                if (!this.mMaxZoomDefined) {
                    this.mMaxZoom = -1.0f;
                }
                setImageMatrix(getImageViewMatrix());
                postTranslate((float) (-i6), (float) (-i5));
                if (!this.mUserScaled) {
                    f = getDefaultScale(this.mScaleType);
                    zoomTo(f);
                } else {
                    f = ((double) Math.abs(scale2 - min)) > 0.001d ? (scale / scale3) * scale2 : 1.0f;
                    zoomTo(f);
                }
            } else {
                f = 1.0f;
            }
            this.mUserScaled = false;
            if (f > getMaxScale() || f < getMinScale()) {
                zoomTo(f);
            }
            center(true, true);
            if (this.mBitmapChanged) {
                onDrawableChanged(drawable);
            }
            if (z || this.mBitmapChanged || this.mScaleTypeChanged) {
                onLayoutChanged(i7, i8, i9, i10);
            }
            if (this.mScaleTypeChanged) {
                this.mScaleTypeChanged = false;
            }
            if (this.mBitmapChanged) {
                this.mBitmapChanged = false;
            }
        }
    }

    public void resetDisplay() {
        this.mBitmapChanged = true;
        requestLayout();
    }

    public float getDefaultScale(DisplayType displayType) {
        if (displayType == DisplayType.FIT_TO_SCREEN) {
            return 1.0f;
        }
        if (displayType == DisplayType.FIT_IF_BIGGER) {
            return Math.min(1.0f, 1.0f / getScale(this.mBaseMatrix));
        }
        return 1.0f / getScale(this.mBaseMatrix);
    }

    public void setImageResource(int i) {
        setImageDrawable(getContext().getResources().getDrawable(i));
    }

    public void setImageBitmap(Bitmap bitmap) {
        setImageBitmap(bitmap, null, -1.0f, -1.0f);
    }

    public void setImageBitmap(Bitmap bitmap, Matrix matrix, float f, float f2) {
        if (bitmap != null) {
            setImageDrawable(new FastBitmapDrawable(bitmap), matrix, f, f2);
        } else {
            setImageDrawable(null, matrix, f, f2);
        }
    }

    public void setImageDrawable(Drawable drawable) {
        setImageDrawable(drawable, null, -1.0f, -1.0f);
    }

    public void setImageDrawable(Drawable drawable, Matrix matrix, float f, float f2) {
        if (getWidth() <= 0) {
            final Drawable drawable2 = drawable;
            final Matrix matrix2 = matrix;
            final float f3 = f;
            final float f4 = f2;
            this.mLayoutRunnable = new Runnable() {
                public void run() {
                    ImageViewTouchBase.this.setImageDrawable(drawable2, matrix2, f3, f4);
                }
            };
            return;
        }
        _setImageDrawable(drawable, matrix, f, f2);
    }

    public void _setImageDrawable(Drawable drawable, Matrix matrix, float f, float f2) {
        if (drawable != null) {
            super.setImageDrawable(drawable);
        } else {
            this.mBaseMatrix.reset();
            super.setImageDrawable(null);
        }
        if (f == -1.0f || f2 == -1.0f) {
            this.mMinZoom = -1.0f;
            this.mMaxZoom = -1.0f;
            this.mMinZoomDefined = false;
            this.mMaxZoomDefined = false;
        } else {
            float min = Math.min(f, f2);
            float max = Math.max(min, f2);
            this.mMinZoom = min;
            this.mMaxZoom = max;
            this.mMinZoomDefined = true;
            this.mMaxZoomDefined = true;
            if (this.mScaleType == DisplayType.FIT_TO_SCREEN || this.mScaleType == DisplayType.FIT_IF_BIGGER) {
                if (this.mMinZoom >= 1.0f) {
                    this.mMinZoomDefined = false;
                    this.mMinZoom = -1.0f;
                }
                if (this.mMaxZoom <= 1.0f) {
                    this.mMaxZoomDefined = true;
                    this.mMaxZoom = -1.0f;
                }
            }
        }
        if (matrix != null) {
            this.mNextMatrix = new Matrix(matrix);
        }
        this.mBitmapChanged = true;
        requestLayout();
    }

    public void onDrawableChanged(Drawable drawable) {
        fireOnDrawableChangeListener(drawable);
    }

    public void fireOnLayoutChangeListener(int i, int i2, int i3, int i4) {
        if (this.mOnLayoutChangeListener != null) {
            this.mOnLayoutChangeListener.onLayoutChanged(true, i, i2, i3, i4);
        }
    }

    public void fireOnDrawableChangeListener(Drawable drawable) {
        if (this.mDrawableChangeListener != null) {
            this.mDrawableChangeListener.onDrawableChanged(drawable);
        }
    }

    public void onLayoutChanged(int i, int i2, int i3, int i4) {
        fireOnLayoutChangeListener(i, i2, i3, i4);
    }

    public float computeMaxZoom() {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return 1.0f;
        }
        return Math.max(((float) drawable.getIntrinsicWidth()) / ((float) this.mThisWidth), ((float) drawable.getIntrinsicHeight()) / ((float) this.mThisHeight)) * 8.0f;
    }

    public float computeMinZoom() {
        if (getDrawable() == null) {
            return 1.0f;
        }
        return Math.min(1.0f, 1.0f / getScale(this.mBaseMatrix));
    }

    public float getMaxScale() {
        if (this.mMaxZoom == -1.0f) {
            this.mMaxZoom = computeMaxZoom();
        }
        return this.mMaxZoom;
    }

    public float getMinScale() {
        if (this.mMinZoom == -1.0f) {
            this.mMinZoom = computeMinZoom();
        }
        return this.mMinZoom;
    }

    public Matrix getImageViewMatrix() {
        return getImageViewMatrix(this.mSuppMatrix);
    }

    public Matrix getImageViewMatrix(Matrix matrix) {
        this.mDisplayMatrix.set(this.mBaseMatrix);
        this.mDisplayMatrix.postConcat(matrix);
        return this.mDisplayMatrix;
    }

    public void setImageMatrix(Matrix matrix) {
        Matrix imageMatrix = getImageMatrix();
        boolean z = (matrix == null && !imageMatrix.isIdentity()) || (matrix != null && !imageMatrix.equals(matrix));
        super.setImageMatrix(matrix);
        if (z) {
            onImageMatrixChanged();
        }
    }

    public Matrix getDisplayMatrix() {
        return new Matrix(this.mSuppMatrix);
    }

    public void getProperBaseMatrix(Drawable drawable, Matrix matrix) {
        float f = (float) this.mThisWidth;
        float f2 = (float) this.mThisHeight;
        float intrinsicWidth = (float) drawable.getIntrinsicWidth();
        float intrinsicHeight = (float) drawable.getIntrinsicHeight();
        matrix.reset();
        if (intrinsicWidth > f || intrinsicHeight > f2) {
            float min = Math.min(f / intrinsicWidth, f2 / intrinsicHeight);
            matrix.postScale(min, min);
            matrix.postTranslate((f - (intrinsicWidth * min)) / 2.0f, (f2 - (intrinsicHeight * min)) / 2.0f);
            return;
        }
        float min2 = Math.min(f / intrinsicWidth, f2 / intrinsicHeight);
        matrix.postScale(min2, min2);
        matrix.postTranslate((f - (intrinsicWidth * min2)) / 2.0f, (f2 - (intrinsicHeight * min2)) / 2.0f);
    }

    public void getProperBaseMatrix2(Drawable drawable, Matrix matrix) {
        float f = (float) this.mThisWidth;
        float f2 = (float) this.mThisHeight;
        float intrinsicWidth = (float) drawable.getIntrinsicWidth();
        float intrinsicHeight = (float) drawable.getIntrinsicHeight();
        matrix.reset();
        float min = Math.min(f / intrinsicWidth, f2 / intrinsicHeight);
        matrix.postScale(min, min);
        matrix.postTranslate((f - (intrinsicWidth * min)) / 2.0f, (f2 - (intrinsicHeight * min)) / 2.0f);
    }

    public float getValue(Matrix matrix, int i) {
        matrix.getValues(this.mMatrixValues);
        return this.mMatrixValues[i];
    }

    public void printMatrix(Matrix matrix) {
        float value = getValue(matrix, 0);
        float value2 = getValue(matrix, 4);
        float value3 = getValue(matrix, 2);
        float value4 = getValue(matrix, 5);
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("matrix: { x: ");
        sb.append(value3);
        sb.append(", y: ");
        sb.append(value4);
        sb.append(", scalex: ");
        sb.append(value);
        sb.append(", scaley: ");
        sb.append(value2);
        sb.append(" }");
        Log.d(str, sb.toString());
    }

    public RectF getBitmapRect() {
        return getBitmapRect(this.mSuppMatrix);
    }

    public RectF getBitmapRect(Matrix matrix) {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return null;
        }
        Matrix imageViewMatrix = getImageViewMatrix(matrix);
        this.mBitmapRect.set(0.0f, 0.0f, (float) drawable.getIntrinsicWidth(), (float) drawable.getIntrinsicHeight());
        imageViewMatrix.mapRect(this.mBitmapRect);
        return this.mBitmapRect;
    }

    public float getScale(Matrix matrix) {
        return getValue(matrix, 0);
    }

    public float getScale() {
        return getScale(this.mSuppMatrix);
    }

    public void center(boolean z, boolean z2) {
        if (getDrawable() != null) {
            RectF center = getCenter(this.mSuppMatrix, z, z2);
            if (!(center.left == 0.0f && center.top == 0.0f)) {
                postTranslate(center.left, center.top);
            }
        }
    }

    public RectF getCenter(Matrix matrix, boolean z, boolean z2) {
        float f = 0;
        float f2 = 0;
        if (getDrawable() == null) {
            return new RectF(0.0f, 0.0f, 0.0f, 0.0f);
        }
        this.mCenterRect.set(0.0f, 0.0f, 0.0f, 0.0f);
        RectF bitmapRect = getBitmapRect(matrix);
        float height = bitmapRect.height();
        float width = bitmapRect.width();
        if (z2) {
            float f3 = (float) this.mThisHeight;
            if (height < f3) {
                f = ((f3 - height) / 2.0f) - bitmapRect.top;
            } else if (bitmapRect.top > 0.0f) {
                f = -bitmapRect.top;
            } else if (bitmapRect.bottom < f3) {
                f = ((float) this.mThisHeight) - bitmapRect.bottom;
            }
            if (z) {
                float f4 = (float) this.mThisWidth;
                if (width < f4) {
                    f2 = ((f4 - width) / 2.0f) - bitmapRect.left;
                } else if (bitmapRect.left > 0.0f) {
                    f2 = -bitmapRect.left;
                } else if (bitmapRect.right < f4) {
                    f2 = f4 - bitmapRect.right;
                }
                this.mCenterRect.set(f2, f, 0.0f, 0.0f);
                return this.mCenterRect;
            }
            f2 = 0.0f;
            this.mCenterRect.set(f2, f, 0.0f, 0.0f);
            return this.mCenterRect;
        }
        f = 0.0f;
        if (z) {
        }
        f2 = 0.0f;
        this.mCenterRect.set(f2, f, 0.0f, 0.0f);
        return this.mCenterRect;
    }

    public void postTranslate(float f, float f2) {
        if (f != 0.0f || f2 != 0.0f) {
            this.mSuppMatrix.postTranslate(f, f2);
            setImageMatrix(getImageViewMatrix());
        }
    }

    public void postScale(float f, float f2, float f3) {
        this.mSuppMatrix.postScale(f, f, f2, f3);
        setImageMatrix(getImageViewMatrix());
    }

    public PointF getCenter() {
        return this.mCenter;
    }

    public void zoomTo(float f) {
        if (f > getMaxScale()) {
            f = getMaxScale();
        }
        if (f < getMinScale()) {
            f = getMinScale();
        }
        PointF center = getCenter();
        zoomTo(f, center.x, center.y);
    }

    public void zoomTo(float f, float f2) {
        PointF center = getCenter();
        zoomTo(f, center.x, center.y, f2);
    }

    public void zoomTo(float f, float f2, float f3) {
        if (f > getMaxScale()) {
            f = getMaxScale();
        }
        postScale(f / getScale(), f2, f3);
        onZoom(getScale());
        center(true, true);
    }

    public void scrollBy(float f, float f2) {
        panBy((double) f, (double) f2);
    }

    public void panBy(double d, double d2) {
        RectF bitmapRect = getBitmapRect();
        this.mScrollRect.set((float) d, (float) d2, 0.0f, 0.0f);
        updateRect(bitmapRect, this.mScrollRect);
        postTranslate(this.mScrollRect.left, this.mScrollRect.top);
        center(true, true);
    }

    public void updateRect(RectF rectF, RectF rectF2) {
        if (rectF != null) {
            if (rectF.top >= 0.0f && rectF.bottom <= ((float) this.mThisHeight)) {
                rectF2.top = 0.0f;
            }
            if (rectF.left >= 0.0f && rectF.right <= ((float) this.mThisWidth)) {
                rectF2.left = 0.0f;
            }
            if (rectF.top + rectF2.top >= 0.0f && rectF.bottom > ((float) this.mThisHeight)) {
                rectF2.top = (float) ((int) (0.0f - rectF.top));
            }
            if (rectF.bottom + rectF2.top <= ((float) (this.mThisHeight + 0)) && rectF.top < 0.0f) {
                rectF2.top = (float) ((int) (((float) (this.mThisHeight + 0)) - rectF.bottom));
            }
            if (rectF.left + rectF2.left >= 0.0f) {
                rectF2.left = (float) ((int) (0.0f - rectF.left));
            }
            if (rectF.right + rectF2.left <= ((float) (this.mThisWidth + 0))) {
                rectF2.left = (float) ((int) (((float) (this.mThisWidth + 0)) - rectF.right));
            }
        }
    }

    public void scrollBy(float f, float f2, double d) {
        final double d2 = (double) f;
        final double d3 = (double) f2;
        final long currentTimeMillis = System.currentTimeMillis();
        Handler handler = this.mHandler;
        final double d4 = d;
        handler.post(new Runnable() {
            double old_x = 0.0d;
            double old_y = 0.0d;

            public void run() {
                double min = Math.min(d4, (double) (System.currentTimeMillis() - currentTimeMillis));
                double d = min;
                double easeOut = ImageViewTouchBase.this.mEasing.easeOut(d, 0.0d, d2, d4);
                double easeOut2 = ImageViewTouchBase.this.mEasing.easeOut(d, 0.0d, d3, d4);
                ImageViewTouchBase.this.panBy(easeOut - this.old_x, easeOut2 - this.old_y);
                this.old_x = easeOut;
                this.old_y = easeOut2;
                if (min < d4) {
                    ImageViewTouchBase.this.mHandler.post(this);
                    return;
                }
                RectF center = ImageViewTouchBase.this.getCenter(ImageViewTouchBase.this.mSuppMatrix, true, true);
                if (center.left != 0.0f || center.top != 0.0f) {
                    ImageViewTouchBase.this.scrollBy(center.left, center.top);
                }
            }
        });
    }

    public void zoomTo(float f, float f2, float f3, float f4) {
        if (f > getMaxScale()) {
            f = getMaxScale();
        }
        final long currentTimeMillis = System.currentTimeMillis();
        final float scale = getScale();
        final float f5 = f - scale;
        Matrix matrix = new Matrix(this.mSuppMatrix);
        matrix.postScale(f, f, f2, f3);
        RectF center = getCenter(matrix, true, true);
        final float f6 = f2 + (center.left * f);
        final float f7 = f3 + (center.top * f);
        Handler handler = this.mHandler;
        final float f8 = f4;
        handler.post(new Runnable() {
            public void run() {
                float min = Math.min(f8, (float) (System.currentTimeMillis() - currentTimeMillis));
                ImageViewTouchBase.this.zoomTo(scale + ((float) ImageViewTouchBase.this.mEasing.easeInOut((double) min, 0.0d, (double) f5, (double) f8)), f6, f7);
                if (min < f8) {
                    ImageViewTouchBase.this.mHandler.post(this);
                    return;
                }
                ImageViewTouchBase.this.onZoomAnimationCompleted(ImageViewTouchBase.this.getScale());
                ImageViewTouchBase.this.center(true, true);
            }
        });
    }

    public void dispose() {
        clear();
    }
}
