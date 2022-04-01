package com.tools.photolab.effect.motion_tool;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.Log;




class CropAsyncTaskPaint extends AsyncTask<Void, Void, Void> {
    double alpha;
    int currentProgressDensity;
    int currentProgressOpacity;


    float f145dx;

    float f146dy;
    boolean isReady;
    Bitmap mCropped;
    int mLeft;
    int mTop;
    Bitmap mainBitmap = null;
    OnCropPaintTaskCompleted onTaskCompleted;
    Bitmap oraginal;
    private double roatetAngle = 0.0d;


    float f147xC;

    float f148yC;

    private Bitmap paintBitmaps() {
        Paint paint = null;
        if (!this.isReady) {
            return null;
        }
        Log.d("alpha=", "" + this.alpha);
        Bitmap copy = this.oraginal.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(copy);
        Bitmap changeAlpha = Utils.changeAlpha(this.mCropped, this.currentProgressOpacity);
        int i = this.mLeft;
        int i2 = this.mTop;
        double cos = Math.cos(this.alpha);
        double sin = Math.sin(this.alpha);
        Log.d("alphacos=", "" + cos);
        Log.d("alphasin=", "" + sin);
        if (this.currentProgressDensity > 0) {
            int dpToPx = Utils.dpToPx(300 / this.currentProgressDensity);
            int i3 = this.currentProgressDensity;
            while (i3 > 0) {
                double d = (double) this.mLeft;
                double d2 = (double) (dpToPx * i3);
                Double.isNaN(d2);
                Double.isNaN(d);
                double d3 = (double) this.mTop;
                Double.isNaN(d2);
                Double.isNaN(d3);
                canvas = canvas;
                canvas.drawBitmap(changeAlpha, (float) ((int) (d + (d2 * cos))), (float) ((int) (d3 - (d2 * sin))), (Paint) null);
                i3--;
                paint = null;
            }
        }
        canvas.drawBitmap(this.mCropped, (float) this.mLeft, (float) this.mTop, paint);
        return copy;
    }

    public CropAsyncTaskPaint(OnCropPaintTaskCompleted onCropPaintTaskCompleted, float f, float f2, Bitmap bitmap, Bitmap bitmap2, int i, int i2, double d, int i3, int i4, boolean z, float f3, float f4) {
        double d2 = d;
        this.onTaskCompleted = onCropPaintTaskCompleted;
        this.f147xC = f;
        this.f148yC = f2;
        this.oraginal = bitmap;
        this.mCropped = bitmap2;
        this.mLeft = i;
        this.mTop = i2;
        this.alpha = d2;
        this.currentProgressOpacity = i3;
        this.currentProgressDensity = i4;
        this.isReady = z;
        this.f145dx = f3;
        this.f146dy = f4;
        this.roatetAngle = d2;
    }


    public void onPreExecute() {
        super.onPreExecute();
    }


    public Void doInBackground(Void... voidArr) {
        this.mainBitmap = paintBitmaps();
        return null;
    }


    public void onPostExecute(Void voidR) {
        this.onTaskCompleted.onTaskCompleted(this.mainBitmap, this.alpha);
    }
}
