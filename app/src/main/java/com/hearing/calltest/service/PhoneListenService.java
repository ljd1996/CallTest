package com.hearing.calltest.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.hearing.calltest.main.MainActivity;
import com.hearing.calltest.R;
import com.hearing.calltest.call.CallCore;
import com.hearing.calltest.call.SystemCallCore;


/**
 * @author liujiadong
 * @since 2019/12/17
 */
public class PhoneListenService extends NotificationListenerService {

    public static final String TAG = "PhoneListenService";

    private SystemCallCore mSystemCallCore;
    private CallCore mCallCore;


    @Override
    public void onCreate() {
        super.onCreate();
        mSystemCallCore = (SystemCallCore) CallCore.createSystemCallCore(this);
    }

    private Notification buildNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager == null) {
                return null;
            }
            String channelId = getString(R.string.app_name);
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription(channelId);
            notificationChannel.setSound(null, null);
            notificationManager.createNotificationChannel(notificationChannel);

            notification = new Notification.Builder(this, channelId)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("来电秀")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(contentIntent)
                    .build();
        } else {
            notification = new Notification.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("来电秀")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(contentIntent)
                    .build();
        }
        return notification;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.d(TAG, getClass().getSimpleName() + ": onNotificationPosted: " + sbn);
        super.onNotificationPosted(sbn);

        if (mSystemCallCore.isCall(sbn)) {
            mSystemCallCore.onNotificationPosted(sbn.getNotification());
        }

        mCallCore = CallCore.createCallCore(this, sbn);
        if (mCallCore != null) {
            mCallCore.onNotificationPosted(sbn.getNotification());
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.d(TAG, getClass().getSimpleName() + ": onNotificationRemoved");
        super.onNotificationRemoved(sbn);

        if (mCallCore != null) {
            mCallCore.onNotificationRemoved();
        }
    }

    @Override
    public void onListenerConnected() {
        Log.d(TAG, getClass().getSimpleName() + ": onListenerConnected");
        super.onListenerConnected();
    }

    @Override
    public void onListenerDisconnected() {
        Log.d(TAG, getClass().getSimpleName() + ": onListenerDisconnected");
        super.onListenerDisconnected();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, getClass().getSimpleName() + ": onDestroy");
        mSystemCallCore.onDestroy();
        super.onDestroy();
    }
}
