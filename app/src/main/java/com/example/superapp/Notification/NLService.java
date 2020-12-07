package com.example.superapp.Notification;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class NLService extends NotificationListenerService {

    private String TAG = this.getClass().getSimpleName();
    private NLServiceReceiver mReceiver;


    @Override
    public void onCreate() {
        super.onCreate();
        mReceiver = new NLServiceReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.superapp.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Notification mNotification = sbn.getNotification();
        Bundle extras = mNotification.extras;
        String notificationTitle = extras.getString(Notification.EXTRA_TITLE);
        String notificationSub = extras.getString(Notification.EXTRA_TEXT);

        Bitmap id =
                ((Bitmap) extras.getParcelable(Notification.EXTRA_LARGE_ICON));


        int id1 = extras.getInt(Notification.EXTRA_SMALL_ICON);

        String pack = sbn.getPackageName();
        Context remotePackageContext = null;
        Bitmap bmp = null;
        try {
            remotePackageContext = getApplicationContext().createPackageContext(pack, 0);
            Drawable icon = remotePackageContext.getResources().getDrawable(id1);
            if(icon !=null) {
                bmp = ((BitmapDrawable) icon).getBitmap();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        Log.i(TAG, "onNotificationPosted");
        Log.i(TAG, "ID :" + sbn.getId() + "\\t" + sbn.getNotification().tickerText + "\\t" +
                sbn.getPackageName() + "\\t" +
                sbn.getPostTime() + "\\t" +
                notificationTitle + "\\t" +
                notificationSub);
        Intent intent = new Intent("com.example.superapp.NOTIFICATION_LISTENER_EXAMPLE");
        intent.putExtra("notification_event", "onNotificationPosted:\\n" + sbn.getId() + "\\t" +
                sbn.getNotification().tickerText + "\\t" +
                sbn.getPackageName() + "\\t" +
                sbn.getPostTime() + "\\t" +
                notificationTitle + "\\t" +
                notificationSub);

        if (id != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            id.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            intent.putExtra("notification_event1", byteArray);
            Log.i(TAG, "Hekko" + byteArray);
        }

        if (bmp != null) {
            ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream1);
            byte[] byteArray1 = stream1.toByteArray();
            intent.putExtra("notification_event2", byteArray1);
            Log.i(TAG, "Hekko" + byteArray1);
        }



        sendBroadcast(intent);


    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Notification mNotification = sbn.getNotification();
        Bundle extras = mNotification.extras;
        String notificationTitle = extras.getString(Notification.EXTRA_TITLE);
        String notificationSub = extras.getString(Notification.EXTRA_TEXT);

        Log.i(TAG, "onNOtificationRemoved");
        Log.i(TAG, "ID :" + sbn.getId() + "\\t" + sbn.getNotification().tickerText + "\\t" + sbn.getPackageName());
        Intent intent = new Intent("com.example.superapp.NOTIFICATION_LISTENER_EXAMPLE");
        intent.putExtra("notification_event", "onNotificationRemoved:\\n" + sbn.getId() + "\\t" +
                sbn.getNotification().tickerText + "\\t" +
                sbn.getPackageName() + "\\t" +
                sbn.getPostTime() + "\\t" +
                notificationTitle + "\\t" +
                notificationSub);
        sendBroadcast(intent);
    }

    class NLServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("command").equals("clearall")) {
                NLService.this.cancelAllNotifications();
            } else if (intent.getStringExtra("command").equals("list")) {
                Intent notificationIntent = new Intent("com.example.superapp.NOTIFICATION_LISTENER_EXAMPLE");
                notificationIntent.putExtra("notification_event", "=======");
                sendBroadcast(notificationIntent);

                int i = 1;
                for (StatusBarNotification sbn : NLService.this.getActiveNotifications()) {
                    Intent infoIntent = new Intent("com.example.superapp.NOTIFICATION_LISTENER_EXAMPLE");
                    infoIntent.putExtra("notification_event", i + " " + sbn.getPackageName() + "\\n");
                    sendBroadcast(infoIntent);
                    i++;
                }

                Intent listIntent = new Intent("com.example.superapp.NOTIFICATION_LISTENER_EXAMPLE");
                listIntent.putExtra("notification_event", "Notification List");
                sendBroadcast(listIntent);
            }
        }
    }
}