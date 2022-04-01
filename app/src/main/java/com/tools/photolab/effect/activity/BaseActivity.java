package com.tools.photolab.effect.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.tools.photolab.R;
import com.tools.photolab.effect.ads.FullScreenAdManager;
import com.tools.photolab.effect.model.PathModelPix;
import com.tools.photolab.effect.support.Constants;
import com.tools.photolab.effect.support.SupportedClass;

import java.util.ArrayList;

public class BaseActivity extends AppCompatActivity {
    public LinearLayout adView;

    public void openPlaystoreApps(String applicationUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(applicationUrl));
        try {
            startActivity(intent);
        } catch (Exception e) {
            intent.setData(Uri.parse(applicationUrl));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FullScreenAdManager.initFullScreenAds(this);
    }

    public ArrayList<PathModelPix> getIconAllFrames() {
        ArrayList<PathModelPix> arrSticker = new ArrayList<>();
        for (int i = 1; i <= Constants.PIX_CATEGORY_SIZE; i++) {
            PathModelPix pathModel = new PathModelPix();
            pathModel.setPathInt(getResources().getIdentifier(Constants.PIX_ICON_FILE_NAME + i, "drawable", getPackageName()));
            arrSticker.add(pathModel);
        }
        return arrSticker;
    }

    public ArrayList<PathModelPix> getMaskAll() {
        ArrayList<PathModelPix> arrSticker = new ArrayList<>();
        for (int i = 1; i <= Constants.PIX_CATEGORY_SIZE; i++) {
            PathModelPix pathModel = new PathModelPix();
            pathModel.setPathInt(getResources().getIdentifier(Constants.PIX_MASK_FILE_NAME + i, "drawable", getPackageName()));
            arrSticker.add(pathModel);
        }
        return arrSticker;
    }


    private AdSize getAdSize(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;
        int adWidth = (int) (widthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }


    public void loadBannerAds(final RelativeLayout adContainerView) {
        //banner ads if internet is on than it will show
        if (SupportedClass.checkConnection(this)) {
            AdView mAdView = new AdView(this);
            mAdView.setAdUnitId(getString(R.string.admob_banner_ads_id));
            adContainerView.removeAllViews();
            adContainerView.addView(mAdView);

            AdSize adSize = getAdSize(this);
            mAdView.setAdSize(adSize);

            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    //super.onAdFailedToLoad(loadAdError);
                    Log.e("ADSTAG", "Banner onAdFailedToLoad()" + loadAdError.getCode());
                    adContainerView.setVisibility(View.GONE);
                }

                @Override
                public void onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                    Log.e("ADSTAG", "Banner onAdLoaded()");
                    adContainerView.setVisibility(View.VISIBLE);
                }

                /* @Override
                 public void onAdFailedToLoad(LoadAdError loadAdError) {
                     // Code to be executed when an ad request fails.
                     Log.e("ADSTAG", "Banner onAdFailedToLoad()" + loadAdError.getCode());
                 }
*/
                @Override
                public void onAdOpened() {
                    // Code to be executed when the ad is displayed.
                    Log.e("ADSTAG", "Banner onAdOpened()");
                }


                @Override
                public void onAdClosed() {
                    // Code to be executed when the banner ad is closed.
                    Log.e("ADSTAG", "Banner onAdClosed()");
                }
            });
        } else {
            adContainerView.setVisibility(View.GONE);
        }


    }


}
