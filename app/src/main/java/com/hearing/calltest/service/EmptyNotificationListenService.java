package com.hearing.calltest.service;

import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

import com.hearing.calltest.MainActivity;
import com.hearing.calltest.util.Util;


/**
 * @author liujiadong
 * @since 2019/12/17
 */
public class EmptyNotificationListenService extends NotificationListenerService {

    private static final String TAG = "LLL";

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, this + " onBind");
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.d(TAG, this + " onNotificationPosted");
        super.onNotificationPosted(sbn);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.d(TAG, this + " onNotificationRemoved");
        super.onNotificationRemoved(sbn);
    }

    @Override
    public void onListenerConnected() {
        Log.d(TAG, this + " onListenerConnected");
        Util.startMainActivity(this);
        super.onListenerConnected();
    }

    @Override
    public void onListenerDisconnected() {
        Log.d(TAG, this + " onListenerDisconnected");
        super.onListenerDisconnected();
    }

    @Override
    public void onNotificationRankingUpdate(RankingMap rankingMap) {
        Log.d(TAG, this + " onNotificationRankingUpdate");
        super.onNotificationRankingUpdate(rankingMap);
    }

    @Override
    public void onListenerHintsChanged(int hints) {
        Log.d(TAG, this + " onListenerHintsChanged");
        super.onListenerHintsChanged(hints);
    }

    @Override
    public void onSilentStatusBarIconsVisibilityChanged(boolean hideSilentStatusIcons) {
        Log.d(TAG, this + " onSilentStatusBarIconsVisibilityChanged");
        super.onSilentStatusBarIconsVisibilityChanged(hideSilentStatusIcons);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, this + " onDestroy");
        Util.startMainActivity(this);
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, this + " onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, this + " onRebind");
        super.onRebind(intent);
    }
}
