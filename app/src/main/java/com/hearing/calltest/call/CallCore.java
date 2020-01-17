package com.hearing.calltest.call;

import android.app.Notification;
import android.content.Context;
import android.service.notification.StatusBarNotification;

import androidx.annotation.NonNull;

import com.hearing.calltest.widget.FloatingView;

/**
 * @author liujiadong
 * @since 2020/1/17
 */
public abstract class CallCore {

    protected static final String TAG = "LLL";

    protected Context mContext;
    protected FloatingView mFloatingView;
    protected Notification.Action[] mActions;

    CallCore(Context context) {
        init(context);
    }

    private void init(Context context) {
        if (context == null) {
            return;
        }
        mContext = context;
        mFloatingView = new FloatingView(context);
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

    protected abstract void acceptCall();

    protected abstract void endCall();

    public abstract void onNotificationPosted(Notification notification);

    public abstract void onDestroy();

    public void onNotificationRemoved() {
        mActions = null;
        if (mFloatingView != null) {
            mFloatingView.hide();
        }
    }

    public static CallCore createCallCore(@NonNull Context context, StatusBarNotification sbn) {
        if (context == null || sbn == null || sbn.getNotification() == null) {
            return null;
        }
        try {
            if ("com.whatsapp".equals(sbn.getPackageName()) && "call".equals(sbn.getNotification().category)
                    && "call_notification_group".equals(sbn.getNotification().getGroup())
                    && sbn.getNotification().actions != null) {
                return new WhatsAppCallCore(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static CallCore createSystemCallCore(Context context) {
        if (context == null) {
            return null;
        }
        return new SystemCallCore(context);
    }
}
