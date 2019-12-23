package com.hearing.calltest;


import android.app.PendingIntent;

/**
 * @author liujiadong
 * @since 2019/12/23
 */
public class PhoneHelper {

    private PendingIntent mAnswerIntent;
    private PendingIntent mEndIntent;


    private PhoneHelper() {
    }

    private static class SingleTon {
        private static PhoneHelper sInstance = new PhoneHelper();
    }

    public static PhoneHelper getInstance() {
        return SingleTon.sInstance;
    }

    public void setAnswerIntent(PendingIntent intent) {
        this.mAnswerIntent = intent;
    }

    public void setEndIntent(PendingIntent intent) {
        this.mEndIntent = intent;
    }

    public void answer() {
        if (mAnswerIntent != null) {
            try {
                mAnswerIntent.send();
                mAnswerIntent = null;
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }
    }

    public void endCall() {
        if (mEndIntent != null) {
            try {
                mEndIntent.send();
                mEndIntent = null;
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }
    }

    public void reset() {
        mAnswerIntent = null;
        mEndIntent = null;
    }
}
