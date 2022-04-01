package com.tools.photolab.effect.support;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.tools.photolab.effect.MainApplication;
import com.tools.photolab.effect.activity.HomeActivity;

public class MyExceptionHandlerPix implements Thread.UncaughtExceptionHandler {
    private Activity activity;

    public MyExceptionHandlerPix(Activity a) {
        activity = a;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Intent intent = null;
        if (activity != null) {
            intent = new Intent(activity, HomeActivity.class);
        } else if (MainApplication.getContext() != null) {
            intent = new Intent(MainApplication.getContext(), HomeActivity.class);
        }


        if (intent != null) {
            intent.putExtra("crash", true);
//        add error into intent
//        intent.putExtra("report", ex.getMessage());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(MainApplication.getContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
            AlarmManager mgr = (AlarmManager) MainApplication.getContext().getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 10, pendingIntent);
            //activity.finish();
            System.exit(2);
        }
    }
}