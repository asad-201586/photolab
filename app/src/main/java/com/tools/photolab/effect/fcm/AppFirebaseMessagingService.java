package com.tools.photolab.effect.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tools.photolab.R;
import com.tools.photolab.effect.activity.HomeActivity;
import com.tools.photolab.effect.support.Constants;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.tools.photolab.effect.rate.PreferenceHelper.getIsAgreeShowDialog;


public class AppFirebaseMessagingService extends FirebaseMessagingService {

    public static final String RATE_US = "rateus";
    public static final String NEW_FRAMES = "newimages";
    private static final String TAG = "FirebaseMessageService";
    public String CHANNEL_ID = "";
    Bitmap bitmap;
    String message, action, actionText, title;
    String imageUri;

    public static int getNotificationIcon(NotificationCompat.Builder notificationBuilder, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int color = ContextCompat.getColor(context, R.color.colorPrimary);
            //notificationBuilder.setColor(color);
            return R.mipmap.ic_launcher;

        } else {
            return R.mipmap.ic_launcher;
        }
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        //
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        //The message which i send will have keys named [message, image, AnotherActivity] and corresponding values.
        //You can change as per the requirement.

        action = remoteMessage.getData().get("action");

        //message will contain the Push Message
        message = remoteMessage.getData().get("message");
        //  Log.e("message" , message);
        //Toast.makeText(this, ""+message, Toast.LENGTH_SHORT).show();
        //imageUri will contain URL of the image to be displayed with Notification
        actionText = remoteMessage.getData().get("actionText");
        title = remoteMessage.getData().get("title");
        imageUri = remoteMessage.getData().get("image");
        bitmap = getBitmapfromUrl(imageUri);

        sendNotification(message, bitmap);

    }

    private void createNotificationChannel(NotificationManager notificationManager) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        CHANNEL_ID = getString(R.string.app_name);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel);
        }
    }

    public PendingIntent defineRateUsIntent() {
        Intent pendingIntent;
        String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            pendingIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
        } catch (android.content.ActivityNotFoundException anfe) {
            pendingIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
        }
        pendingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pi = PendingIntent.getActivity(this, 111, pendingIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return pi;
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     */
    private void sendNotification(String messageBody, Bitmap image) {

        Intent intent = null;
        PendingIntent pendingIntent;

        intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);


        if (!TextUtils.isEmpty(action)) {
            if (TextUtils.equals(RATE_US, action)) {
                intent.putExtra(Constants.ATTR_RATE_US, true);
            } else if (TextUtils.equals(NEW_FRAMES, action)) {
                intent.putExtra(Constants.ATTR_MORE_FRAME_DATA, true);
            }
        }
        pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT, null);


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel(notificationManager);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        if (image != null)
            notificationBuilder.setLargeIcon(image);

        if (!TextUtils.isEmpty(action)) {
            notificationBuilder.addAction(0, actionText, pendingIntent);
        }


        notificationBuilder.setSmallIcon(getNotificationIcon(notificationBuilder, getApplicationContext()));
        notificationBuilder.setVibrate(new long[]{1000, 1000});
        playSound(notificationBuilder);
        if (!TextUtils.isEmpty(action)) {
            if (TextUtils.equals(RATE_US, action)) {
                if (getIsAgreeShowDialog(this))
                    notificationManager.notify(4789, notificationBuilder.build());
            } else {
                notificationManager.notify(4789, notificationBuilder.build());
            }
        } else {
            notificationManager.notify(4789, notificationBuilder.build());
        }
    }

    private void playSound(NotificationCompat.Builder builder) {

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (sound == null)
            sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification);
        builder.setSound(sound);

    }

    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }


    @Override
    public void onNewToken(String s) {
        Log.d(TAG, "Refreshed token: " + s);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(s);
    }

    private void sendRegistrationToServer(String token) {

        FirebaseMessaging.getInstance().subscribeToTopic("PhotoLab").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("success", "success topic register");
            }
        });
    }

    public Bundle createBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("message", message);
        bundle.putString("image", imageUri);
        return bundle;
    }

}
