package com.hearing.calltest.service;

import android.app.Notification;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.hearing.calltest.util.Util;
import com.hearing.calltest.business.VideoDBHelper;
import com.hearing.calltest.widget.FloatingView;


/**
 * @author liujiadong
 * @since 2019/12/17
 */
public class EmptyNotificationListenService extends NotificationListenerService {

    private static final String TAG = "LLL";
    private FloatingView mFloatingView;
    private Notification.Action[] mActions;


    @Override
    public void onCreate() {
        super.onCreate();
        mFloatingView = new FloatingView(this);
        mFloatingView.setListener(new FloatingView.OnCallListener() {
            @Override
            public void onGet() {
                acceptCall();
            }

            @Override
            public void onEnd() {
                endCall();
            }
        });
    }

    private void endCall() {
        if (mActions != null) {
            try {
                mActions[0].actionIntent.send();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mActions = null;
            }
        }
    }

    private void acceptCall() {
        if (mActions != null) {
            try {
                mActions[1].actionIntent.send();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mActions = null;
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, this + " onBind");
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.d(TAG, this + " onNotificationPosted");
        super.onNotificationPosted(sbn);
        Log.d("getPackageName", sbn.getPackageName());

        if (checkWhatsApp(sbn)) {
            Bundle bundle = sbn.getNotification().extras;
            if (bundle != null) {
                mFloatingView.setPerson(String.valueOf(bundle.getCharSequence(Notification.EXTRA_TITLE)), null);
            }
            mActions = sbn.getNotification().actions;
            mFloatingView.show(VideoDBHelper.UNKNOWN_NUMBER);

            Icon small = sbn.getNotification().getSmallIcon();
            if (small != null) {
                mFloatingView.setHead(small.loadDrawable(this));
            }
            Icon large = sbn.getNotification().getLargeIcon();
            if (large != null) {
                mFloatingView.setHead(large.loadDrawable(this));
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.d(TAG, this + " onNotificationRemoved");
        super.onNotificationRemoved(sbn);
        if (checkWhatsApp(sbn)) {
            mActions = null;
            mFloatingView.hide();
        }
    }

    private boolean checkWhatsApp(StatusBarNotification sbn) {
        if (sbn == null || sbn.getNotification() == null) {
            return false;
        }
        try {
            if ("com.whatsapp".equals(sbn.getPackageName()) && "call".equals(sbn.getNotification().category)
                    && "call_notification_group".equals(sbn.getNotification().getGroup())
                    && sbn.getNotification().actions != null) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
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
