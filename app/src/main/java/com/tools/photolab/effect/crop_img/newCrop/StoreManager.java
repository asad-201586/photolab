package com.tools.photolab.effect.crop_img.newCrop;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;


import com.tools.photolab.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class StoreManager {
    public static int croppedLeft = 0;
    public static int croppedTop = 0;
    public static boolean isNull = false;
    private static String BITMAP_CROPED_FILE_NAME = "temp_croped_bitmap.png";
    private static String BITMAP_CROPED_MASK_FILE_NAME = "temp_croped_mask_bitmap.png";
    private static String BITMAP_FILE_NAME = "temp_bitmap.png";
    private static String BITMAP_ORIGINAL_FILE_NAME = "temp_original_bitmap.png";


    public static void setCurrentEffecdedBitmap(Activity activity, Bitmap bitmap) {
    }

    public static Bitmap getCurrentCroppedMaskBitmap(Activity activity) {
        if (isNull) {
            return null;
        }
        return getBitmapByFileName(activity, BITMAP_CROPED_MASK_FILE_NAME);
    }

    public static Bitmap getCurrentBitmap(Activity activity) {
        return getBitmapByFileName(activity, BITMAP_FILE_NAME);
    }

    public static Bitmap getCurrentCropedBitmap(Activity activity) {
        if (isNull) {
            return null;
        }
        return getBitmapByFileName(activity, BITMAP_CROPED_FILE_NAME);
    }

    public static Bitmap getCurrentOriginalBitmap(Activity activity) {
        //get Saved Bitmap
        return getBitmapByFileName(activity, BITMAP_ORIGINAL_FILE_NAME);
    }

    private static Bitmap getBitmapByFileName(Activity r1, String r2) {
        Bitmap bitmap = null;
        ContextWrapper cw = new ContextWrapper(r1);
        File directory = cw.getDir(r1.getResources().getString(R.string.directory), Context.MODE_PRIVATE);
        String s=directory.getAbsolutePath()+"/"+r2;
        Log.i("PathOfFile", s);
        bitmap = BitmapFactory.decodeFile(directory.getAbsolutePath()+"/"+r2);
        return bitmap;
    }


    public static Bitmap getCurrentEffecdedBitmap(Activity activity) {
        return getCurrentBitmap(activity);
    }

    public static void saveFile(Context r2, Bitmap r3, String r4) {
        if (r3 == null)
            return;
        try {
            ContextWrapper cw = new ContextWrapper(r2);
            File directory = cw.getDir(r2.getResources().getString(R.string.directory), Context.MODE_PRIVATE);
            File dir = new File(directory, r4);
            FileOutputStream fos = null;

            fos = new FileOutputStream(dir);
            r3.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void deleteFile(Context context, String str) {
        File file = new File(context.getFilesDir().getAbsolutePath() + "/" + str);
        if (file.exists()) {
            file.delete();
        }
    }

    public static void setCurrentBitmap(Activity activity, Bitmap bitmap) {
        if (bitmap == null) {
            deleteFile(activity, BITMAP_FILE_NAME);
        }
        saveFile(activity, bitmap, BITMAP_FILE_NAME);
    }

    public static void setCurrentCropedBitmap(Activity activity, Bitmap bitmap) {
        if (bitmap == null) {
            deleteFile(activity, BITMAP_CROPED_FILE_NAME);
            isNull = true;
        } else {
            isNull = false;
        }
        saveFile(activity, bitmap, BITMAP_CROPED_FILE_NAME);
    }

    public static void setCurrentCroppedMaskBitmap(Activity activity, Bitmap bitmap) {
        if (bitmap == null) {
            deleteFile(activity, BITMAP_CROPED_MASK_FILE_NAME);
        }
        saveFile(activity, bitmap, BITMAP_CROPED_MASK_FILE_NAME);
    }

    public static void setCurrentOriginalBitmap(Activity activity, Bitmap bitmap) {
        //save Saved Bitmap for to remove background in image
        if (bitmap == null) {
            deleteFile(activity, BITMAP_ORIGINAL_FILE_NAME);
        }
        saveFile(activity, bitmap, BITMAP_ORIGINAL_FILE_NAME);
    }

}
