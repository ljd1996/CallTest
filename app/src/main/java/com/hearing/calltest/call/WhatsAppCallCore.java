package com.hearing.calltest.call;

import android.app.Notification;
import android.content.Context;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.hearing.calltest.business.VideoDBHelper;

/**
 * @author liujiadong
 * @since 2020/1/17
 */
public class WhatsAppCallCore extends CallCore {

    WhatsAppCallCore(Context context, String pkg) {
        super(context, pkg);
    }

    @Override
    protected void acceptCall() {
        sendAction(1);
    }

    @Override
    protected void endCall() {
        sendAction(0);
    }

    @Override
    public void onNotificationPosted(Notification notification) {
        Log.d(TAG, "onNotificationPosted: " + notification);

        if (mContext == null || notification == null || mFloatingView == null) {
            return;
        }
        if (isCall(notification)) {
            Bundle bundle = notification.extras;
            if (bundle != null) {
                mFloatingView.setPerson(String.valueOf(bundle.getCharSequence(Notification.EXTRA_TITLE)), null);
            }
            mActions = notification.actions;
            mFloatingView.show(VideoDBHelper.UNKNOWN_NUMBER);

            Icon small = notification.getSmallIcon();
            if (small != null) {
                mFloatingView.setHead(small.loadDrawable(mContext));
            }
            Icon large = notification.getLargeIcon();
            if (large != null) {
                mFloatingView.setHead(large.loadDrawable(mContext));
            }
            if (isLocked()) {
                mStatus = STATUS_RINGING;
            }
        } else if (isAnswer(notification)) {
            if (mStatus == STATUS_RINGING) {
                mStatus = STATUS_NONE;
                showLockGuide();
            }
        }
    }

    @Override
    public void onNotificationRemoved(Notification notification) {
        Log.d(TAG, "onNotificationRemoved: " + notification);
        if (isCall(notification)) {
            mActions = null;
            if (mFloatingView != null) {
                mFloatingView.hide();
            }
        }
    }

    @Override
    public void onDestroy() {

    }

    private boolean isCall(Notification notification) {
        return notification != null && "call".equals(notification.category)
                && "call_notification_group".equals(notification.getGroup())
                && notification.actions != null;
    }

    private boolean isAnswer(Notification notification) {
        return notification != null && "call".equals(notification.category)
                && TextUtils.isEmpty(notification.getGroup());
    }
}
