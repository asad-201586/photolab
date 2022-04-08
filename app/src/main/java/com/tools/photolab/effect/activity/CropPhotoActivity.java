package com.tools.photolab.effect.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.ads.AdsNetwork;
import com.tools.photolab.R;
import com.tools.photolab.effect.activity.crop_fragment.CropImageFragmentPix;
import com.tools.photolab.effect.support.MyExceptionHandlerPix;

public class CropPhotoActivity extends BaseActivity {

    public Uri currentImgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_crop_image);
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandlerPix(CropPhotoActivity.this));

        RelativeLayout mAdView = findViewById(R.id.adView);
        AdsNetwork.showAdmobBanner(this,mAdView);
        //loadBannerAds(mAdView);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        currentImgUri = Uri.parse(intent.getStringExtra("cropUri"));

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, CropImageFragmentPix.newInstance()).commit();
        }

    }

    public void cancelCropping() {
        if (isFinishing()) return;
        setResult(RESULT_CANCELED);
        finish();
    }

    public void startResultActivity(Uri uri) {
        if (isFinishing()) return;
        // Start ResultActivity
        Log.e("TECHTAG", "Set Back with Bitmap");
        Intent intent = new Intent();
        intent.putExtra("croppedUri", uri);
        setResult(RESULT_OK, intent);
        finish();
    }
}
