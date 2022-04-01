package com.tools.photolab.effect.erase_tool;

import android.content.Context;
import android.os.Environment;

import com.tools.photolab.R;

import java.io.File;

public class Configure {
    public static File GetFileDir(Context context) {
        if ("mounted".equals(Environment.getExternalStorageState())) {
            return context.getExternalFilesDir(null);
        }
        return context.getFilesDir();
    }

    public static File GetCacheDir(Context context) {
        if ("mounted".equals(Environment.getExternalStorageState())) {
            return context.getExternalCacheDir();
        }
        return context.getCacheDir();
    }

    public static File GetSaveDir(Context context) {
        File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        StringBuilder sb = new StringBuilder();
        sb.append(externalStoragePublicDirectory.getPath());
        sb.append(File.separator);
        sb.append(context.getString(R.string.app_name));
        File file = new File(sb.toString());
        return (file.exists() || file.mkdirs()) ? file : externalStoragePublicDirectory;
    }

    public static File GetProjectDir(Context context) {
        File GetFileDir = GetFileDir(context);
        StringBuilder sb = new StringBuilder();
        sb.append(GetFileDir.getPath());
        sb.append(File.separator);
        sb.append("project");
        File file = new File(sb.toString());
        return (file.exists() || file.mkdirs()) ? file : GetFileDir;
    }

    public static File GetProjectBitmapDir(Context context) {
        File GetFileDir = GetFileDir(context);
        StringBuilder sb = new StringBuilder();
        sb.append(GetFileDir.getPath());
        sb.append(File.separator);
        sb.append("project");
        File file = new File(sb.toString());
        return (file.exists() || file.mkdirs()) ? file : GetFileDir;
    }

    public static File GetFontDir(Context context) {
        File GetFileDir = GetFileDir(context);
        StringBuilder sb = new StringBuilder();
        sb.append(GetFileDir.getPath());
        sb.append(File.separator);
        sb.append("font");
        File file = new File(sb.toString());
        return (file.exists() || file.mkdirs()) ? file : GetFileDir;
    }

    public static File GetTextDir(Context context) {
        File GetFileDir = GetFileDir(context);
        StringBuilder sb = new StringBuilder();
        sb.append(GetFileDir.getPath());
        sb.append(File.pathSeparator);
        sb.append("text");
        File file = new File(sb.toString());
        return (file.exists() || file.mkdirs()) ? file : GetFileDir;
    }

    public static File GetUnsplashJsonDir(Context context) {
        File GetFileDir = GetFileDir(context);
        StringBuilder sb = new StringBuilder();
        sb.append(GetFileDir.getPath());
        sb.append(File.separator);
        sb.append("unsplash");
        File file = new File(sb.toString());
        return (file.exists() || file.mkdirs()) ? file : GetFileDir;
    }


}
