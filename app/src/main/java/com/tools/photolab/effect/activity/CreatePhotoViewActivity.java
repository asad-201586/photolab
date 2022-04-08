package com.tools.photolab.effect.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ads.AdsNetwork;
import com.tools.photolab.R;
import com.tools.photolab.effect.custom.TouchImageView;
import com.tools.photolab.effect.support.Constants;
import com.tools.photolab.effect.support.SupportedClass;
import com.tools.photolab.effect.support.MyExceptionHandlerPix;


import java.io.File;

public class CreatePhotoViewActivity extends BaseActivity {

    TouchImageView mMainImage;
    Uri imgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_photo_view);
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandlerPix(CreatePhotoViewActivity.this));
        RelativeLayout mAdView = findViewById(R.id.adView);
        AdsNetwork.showAdmobBanner(this,mAdView);
        //loadBannerAds(mAdView);

        mMainImage = (TouchImageView) findViewById(R.id.iv_create_image);
        imgUri = Uri.parse(getIntent().getStringExtra(Constants.KEY_URI_IMAGE));
        mMainImage.setImageURI(imgUri);

        (findViewById(R.id.iv_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        (findViewById(R.id.iv_share)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SupportedClass.shareWithOther(CreatePhotoViewActivity.this, getString(R.string.txt_app_share_info), imgUri);
            }
        });
        (findViewById(R.id.iv_delete)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(CreatePhotoViewActivity.this);
                dialog.setContentView(R.layout.dialog_delete);
                int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
                dialog.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);

                dialog.findViewById(R.id.tvYes).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        File file = new File(getIntent().getStringExtra(Constants.KEY_URI_IMAGE));
                        file.delete();
                        Toast.makeText(CreatePhotoViewActivity.this, getString(R.string.txt_file_delete_successfully), Toast.LENGTH_SHORT).show();
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }
                });

                dialog.findViewById(R.id.tvNo).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });
    }

    @Override
    public void onBackPressed() {
        this.finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
}
