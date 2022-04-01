package com.tools.photolab.effect.motion_tool;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ProgressBar;


import com.tools.photolab.effect.crop_img.newCrop.BitmapUtils;
import com.tools.photolab.effect.crop_img.newCrop.DeeplabMobile;
import com.tools.photolab.effect.crop_img.newCrop.StoreManager;
import com.tools.photolab.effect.support.SupportedClass;


public class MLCropAsyncTask extends AsyncTask<Void, Void, Void> {
    Activity activity;
    ProgressBar crop_progress_bar;
    Bitmap cropped;
    int left;
    Bitmap mask;
    MLOnCropTaskCompleted onTaskCompleted;
    int top;

    private void show(final boolean z) {
        if (this.activity != null) {
            this.activity.runOnUiThread(new Runnable() {
                public void run() {
                    if (z) {
                        MLCropAsyncTask.this.crop_progress_bar.setVisibility(0);
                    } else {
                        MLCropAsyncTask.this.crop_progress_bar.setVisibility(8);
                    }
                }
            });
        }
    }

    public MLCropAsyncTask(MLOnCropTaskCompleted mLOnCropTaskCompleted, Activity activity2, ProgressBar progressBar) {
        this.onTaskCompleted = mLOnCropTaskCompleted;
        this.activity = activity2;
        this.crop_progress_bar = progressBar;
    }


    public void onPreExecute() {
        super.onPreExecute();
        show(true);
    }


    public Void doInBackground(Void... voidArr) {
        this.cropped = StoreManager.getCurrentCropedBitmap(this.activity);
        this.left = StoreManager.croppedLeft;
        this.top = StoreManager.croppedTop;
        this.mask = StoreManager.getCurrentCroppedMaskBitmap(this.activity);
        if (this.cropped != null || this.mask != null) {
            return null;
        }
        DeeplabMobile deeplabMobile = new DeeplabMobile();
        deeplabMobile.initialize(this.activity.getApplicationContext());
        this.cropped = loadInBackground(StoreManager.getCurrentOriginalBitmap(this.activity), deeplabMobile);
        StoreManager.setCurrentCropedBitmap(this.activity, this.cropped);
        return null;
    }


    public void onPostExecute(Void voidR) {
        show(false);
        this.onTaskCompleted.onTaskCompleted(this.cropped, this.mask, this.left, this.top);
    }

    public Bitmap loadInBackground(Bitmap bitmap, DeeplabMobile deeplabMobile) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float max = 513.0f / ((float) Math.max(bitmap.getWidth(), bitmap.getHeight()));
        int round = Math.round(((float) width) * max);
        int round2 = Math.round(((float) height) * max);
        this.mask = deeplabMobile.segment(SupportedClass.tfResizeBilinear(bitmap, round, round2));
        if (this.mask == null) {
            return null;
        }
        this.mask = BitmapUtils.createClippedBitmap(this.mask, (this.mask.getWidth() - round) / 2, (this.mask.getHeight() - round2) / 2, round, round2);
        this.mask = BitmapUtils.scaleBitmap(this.mask, width, height);
        this.left = (this.mask.getWidth() - width) / 2;
        this.top = (this.mask.getHeight() - height) / 2;
        StoreManager.croppedLeft = this.left;
        int i = this.top;
        StoreManager.croppedTop = i;
        this.top = i;
        StoreManager.setCurrentCroppedMaskBitmap(this.activity, this.mask);
        return Utils.cropBitmapWithMask(bitmap, this.mask, 0, 0);
    }
}
