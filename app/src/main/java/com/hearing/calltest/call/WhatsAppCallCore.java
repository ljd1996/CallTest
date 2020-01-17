package com.hearing.calltest.call;

import android.app.Notification;
import android.content.Context;
import android.graphics.drawable.Icon;
import android.os.Bundle;

import com.hearing.calltest.business.VideoDBHelper;

/**
 * @author liujiadong
 * @since 2020/1/17
 */
public class WhatsAppCallCore extends CallCore {


    WhatsAppCallCore(Context context) {
        super(context);
    }

    @Override
    protected void acceptCall() {
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
    protected void endCall() {
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

    @Override
    public void onNotificationPosted(Notification notification) {
        if (mContext == null || notification == null || mFloatingView == null) {
            return;
        }
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
    }

    @Override
    public void onDestroy() {

    }

}
