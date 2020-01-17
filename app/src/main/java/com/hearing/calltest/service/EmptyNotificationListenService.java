package com.hearing.calltest.service;

import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.hearing.calltest.call.CallCore;


/**
 * @author liujiadong
 * @since 2019/12/17
 */
public class EmptyNotificationListenService extends NotificationListenerService {

    private static final String TAG = "PhoneListenService";

    private CallCore mCallCore;


    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, getClass().getSimpleName() + ": onBind");
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.d(TAG, getClass().getSimpleName() + ": onNotificationPosted: " + sbn);
        super.onNotificationPosted(sbn);

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
        PhoneListenService.startSelf(this);
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
        PhoneListenService.startSelf(this);
        super.onDestroy();
    }
}
