package com.reactnativenotificationbadge;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.module.annotations.ReactModule;

import java.util.List;
import java.util.Map;

@ReactModule(name = NotificationBadgeModule.NAME)
public class NotificationBadgeModule extends ReactContextBaseJavaModule {
    public static final String NAME = "NotificationBadge";
    private static Integer UNIQUE_ID = 1;

    public NotificationBadgeModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    @NonNull
    public String getName() {
        return "NotificationBadge";
    }

    @ReactMethod
    public void configure(String title, String text) {
        ReactApplicationContext context = getReactApplicationContext();

        SharedPreferences sharedPreferences = context.getSharedPreferences(getName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("title", title);
        editor.putString("text", text);
        editor.apply();
    }

    @ReactMethod
    public void setNumber(int count) {
        ReactApplicationContext context = getReactApplicationContext();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            List < NotificationChannel > notificationChannels = notificationManager.getNotificationChannels();

            if (notificationChannels.size() == 0) {
                NotificationChannel notificationChannel = new NotificationChannel("channel", "Count", NotificationManager.IMPORTANCE_LOW);
                notificationChannel.setShowBadge(true);
                notificationChannel.setDescription("Used to show badges on the app icon");
                notificationManager.createNotificationChannel(notificationChannel);
            }

            //        SharedPreferences sharedPref = context.getSharedPreferences("notification", Context.MODE_PRIVATE);
            //        SharedPreferences.Editor editor = sharedPref.edit();
            //        editor.putInt("COUNT", count);
            //        editor.apply();

            if (count == 0) {
                notificationManager.cancel(UNIQUE_ID);
                return;
            }

            SharedPreferences sharedPreferences = context.getSharedPreferences(getName(), Context.MODE_PRIVATE);

            String title = sharedPreferences.getString("title", null);
            String text = sharedPreferences.getString("text", null);
            String processedText = text.replace("%count%", Integer.toString(count));

            // TODO: Use channelID from Firebase when present. Because otherwise it will fail.
            Notification notification = new NotificationCompat.Builder(context, "channel")
                .setContentTitle(title)
                .setContentText(processedText)
                .setNumber(count)
                .setSmallIcon(R.drawable.ic_notify_status)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .build();

            notificationManager.notify(UNIQUE_ID, notification);

//          Log.d(getName(), String.valueOf(notificationManager.getActiveNotifications()[0]));
      }
    }
}
