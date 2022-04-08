package com.tools.photolab.effect.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ads.AdsNetwork;
import com.bumptech.glide.Glide;
import com.tools.photolab.R;
import com.tools.photolab.effect.ads.FullScreenAdManager;
import com.tools.photolab.effect.blur_tool.BlurActivity;
import com.tools.photolab.effect.custom.MyBounceInterpolator;
import com.tools.photolab.effect.support.Constants;
import com.tools.photolab.effect.support.SupportedClass;
import com.tools.photolab.effect.support.MyExceptionHandlerPix;

import java.io.File;


public class ShareActivity extends BaseActivity {
    Uri mImgUri;
    ImageView mIvCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pix_share);
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandlerPix(ShareActivity.this));
        init();

        (findViewById(R.id.iv_home)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FullScreenAdManager.fullScreenAdsCheckPref(ShareActivity.this, FullScreenAdManager.ALL_PREFS.ATTR_ON_HOME_SCREEN, new FullScreenAdManager.GetBackPointer() {
                    @Override
                    public void returnAction() {
                        Intent finishIntent = new Intent(ShareActivity.this, HomeActivity.class);
                        finishIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(finishIntent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                });
            }
        });
        (findViewById(R.id.iv_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        (findViewById(R.id.iv_share)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareOnOthersApp();
            }
        });

    }

    public void init() {
        mIvCreate = (ImageView) findViewById(R.id.iv_create);
        mImgUri = Uri.parse(getIntent().getStringExtra(Constants.KEY_URI_IMAGE));
        RelativeLayout mAdView = findViewById(R.id.adView);
        AdsNetwork.showAdmobBanner(this,mAdView);
        loadBannerAds(mAdView);

        Glide.with(ShareActivity.this)
                .asBitmap()
                .load(mImgUri)
                .into(mIvCreate);
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);

        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);

        mIvCreate.startAnimation(myAnim);
    }

    private void shareOnOthersApp() {
        SupportedClass.shareWithOther(ShareActivity.this, getString(R.string.txt_app_share_info), mImgUri);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        this.finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

}
