package com.hearing.calltest.service;

import android.app.Notification;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.hearing.calltest.PhoneHelper;


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

                    if ("Answer".equalsIgnoreCase(action.title.toString())
                            || "接听".equalsIgnoreCase(action.title.toString())) {
                        Log.d(TAG, "answer is true");
                        PhoneHelper.getInstance().setAnswerIntent(action.actionIntent);
                    }

                    if ("Dismiss".equalsIgnoreCase(action.title.toString())
                            || "忽略".equalsIgnoreCase(action.title.toString())) {
                        Log.d(TAG, "dismiss is true");
                        PhoneHelper.getInstance().setEndIntent(action.actionIntent);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
