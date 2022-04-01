package com.tools.photolab.effect.ads;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.tools.photolab.R;
import com.tools.photolab.effect.support.SupportedClass;

public class FullScreenAdManager {

    public static final String PREF_ON_PHOTO_SCREEN = "PREF_ON_PHOTO_SCREEN";
    public static final String PREF_ON_HOME_SCREEN = "PREF_ON_HOME_SCREEN";
    public static final String PREF_ON_FIRST_PIX_SCREEN = "PREF_ON_FIRST_PIX_SCREEN";
    public static final String PREF_ON_SHARE_SCREEN = "PREF_ON_SHARE_SCREEN";
    public static final String PREF_SAVED_IMAGE_CLICKED = "SAVED_IMAGE_CLICKED";
    private static final String TAG = FullScreenAdManager.class.getCanonicalName();


    public static void initFullScreenAds(android.content.Context context) {
        if (SupportedClass.checkConnection(context)) {
            loadFullScreenAd(context);
        }

    }


    public static InterstitialAd interstitialAd;

    public static void loadFullScreenAd(android.content.Context context) {
        if(interstitialAd == null) {
            AdRequest adRequest = new AdRequest.Builder().build();

            InterstitialAd.load(context, context.getString(R.string.admob_interstitial_ads_id), adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd ad) {
                            // The mInterstitialAd reference will be null until
                            // an ad is loaded.
                            interstitialAd = ad;
                            Log.i(TAG, "onAdLoaded");
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error
                            Log.i(TAG, loadAdError.getMessage());
                            interstitialAd = null;
                        }
                    });
        }
    }

    public static void fullScreenAdsCheckPref(final Activity context, ALL_PREFS all_prefs, final GetBackPointer getBackPointer) {

        int getCount = SharedPreferencesHelper.getInstance().getInt(all_prefs.prefName, 0);
        int newCount = getCount + 1;
        SharedPreferencesHelper.getInstance().setInt(all_prefs.prefName, newCount);
        if (getCount != 0 && getCount % all_prefs.value == 0 && interstitialAd != null) {
            interstitialAd.show(context);
            interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when fullscreen content is dismissed.
                    loadFullScreenAd(context);
                    if (getBackPointer != null)
                        getBackPointer.returnAction();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // Called when fullscreen content failed to show.
                    Log.d("TAG", "The ad failed to show.");
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when fullscreen content is shown.
                    // Make sure to set your reference to null so you don't
                    // show it a second time.
                    interstitialAd = null;
                    Log.d("TAG", "The ad was shown.");
                }
            });

        } else if (getBackPointer != null)
            getBackPointer.returnAction();

    }


    public enum ALL_PREFS {

        ATTR_ON_PHOTO_SCREEN(PREF_ON_PHOTO_SCREEN, 2),   //number click per ads are comming
        ATTR_ON_HOME_SCREEN(PREF_ON_HOME_SCREEN, 2),
        ATTR_ON_FIRST_PIX_SCREEN(PREF_ON_FIRST_PIX_SCREEN, 2),
        ATTR_ON_SHARE_SCREEN(PREF_ON_SHARE_SCREEN, 2),
        ATTR_SAVED_IMAGE_CLICKED(PREF_SAVED_IMAGE_CLICKED, 2);

        private String prefName;
        private int value;

        /**
         * @param prefName Preferences name
         * @param value    Preferences default value
         */
        ALL_PREFS(String prefName, int value) {
            this.prefName = prefName;
            this.value = value;
        }
    }


    public interface GetBackPointer {
        public void returnAction();
    }


}
