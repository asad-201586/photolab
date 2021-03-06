package com.tools.photolab.effect;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.appOpenManager.AppOpenManager;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.FirebaseApp;

public class MainApplication extends Application {



    public static Context context;
    private static AppOpenManager appOpenManager;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        MainApplication.context = context;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setContext(getApplicationContext());
        FirebaseApp.initializeApp(this);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        appOpenAds();

//        List<String> testDevices = new ArrayList<>();
//        testDevices.add(AdRequest.DEVICE_ID_EMULATOR);
//        testDevices.add("7BCD9CF420BB02B0F1B252AFAC067BE3");
//        testDevices.add("16805B774118DE1F6B1BE6F37825F291");


//        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);

//        RequestConfiguration requestConfiguration
//                = new RequestConfiguration.Builder()
//                .setTestDeviceIds(testDevices)
//                .build();
//        MobileAds.setRequestConfiguration(requestConfiguration);
    }

    private void appOpenAds() {
        Log.d("ads_db", "appOpenAds: app open ads");
        MobileAds.initialize(this, initializationStatus -> {});
        appOpenManager = new AppOpenManager(this);
    }
}
