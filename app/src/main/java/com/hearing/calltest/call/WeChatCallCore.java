package com.hearing.calltest.call;

import android.app.Notification;
import android.content.Context;

public class WeChatCallCore extends CallCore {


    WeChatCallCore(Context context, String pkg) {
        super(context, pkg);
    }

    @Override
    protected void acceptCall() {
    }

    @Override
    protected void endCall() {

    }

    @Override
    public void onNotificationPosted(Notification notification) {

    }

    @Override
    public void onNotificationRemoved(Notification notification) {

    }

    @Override
    public void onDestroy() {

    }
}
