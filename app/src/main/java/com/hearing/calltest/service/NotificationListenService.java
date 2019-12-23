package com.hearing.calltest.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;


/**
 * @author liujiadong
 * @since 2019/12/17
 */
public class NotificationListenService extends NotificationListenerService {

    public static final String TAG = "LLL";


    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.d(TAG, "onNotificationPosted");
        super.onNotificationPosted(sbn);
        try {
            if (sbn.getNotification().actions != null) {
                for (Notification.Action action : sbn.getNotification().actions) {
                    if ("Answer".equalsIgnoreCase(action.title.toString())) {
                        Log.d(TAG, "answer is true");

                        PendingIntent intent = action.actionIntent;
                        intent.send();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.d(TAG, "onNotificationRemoved");
        super.onNotificationRemoved(sbn);
    }

    @Override
    public void onListenerConnected() {
        Log.d(TAG, "onListenerConnected");
        super.onListenerConnected();
    }
}
