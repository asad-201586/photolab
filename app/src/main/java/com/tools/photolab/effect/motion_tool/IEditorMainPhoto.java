package com.tools.photolab.effect.motion_tool;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;

import androidx.fragment.app.Fragment;

import com.tools.photolab.effect.drip_tool.activity.ImageViewTouch;

public interface IEditorMainPhoto {
    void acceptFinished();

    void changeBottomFragment(Fragment fragment);

    void changeMainBitmap(Bitmap bitmap);

    void changeMenuToAccept();

    void changeMenuToSave();

    void changeMiddleFragment(Fragment fragment);

    void closeFromModule(IEditorModule iEditorModule);

    ImageViewTouch getImageViewCenter();

    Dialog getLoadingDialog(Context context, int i, boolean z);

    void hideProgressBar();

    boolean isAcceptExist();

    boolean isFromCamera();

    void setControlLineVisible();

    void setInitialFragments();

//    void setOnResumeListener(OnResumeListener onResumeListener);

    void showProgressBar();
}
