package com.hearing.calltest;

import android.content.Context;
import android.telecom.TelecomManager;

import com.hearing.calltest.widget.FloatingView;

import java.lang.ref.WeakReference;

/**
 * @author liujiadong
 * @since 2019/12/23
 */
public class PhoneHelper {

    private WeakReference<Context> mContext;
    private FloatingView mFloatingView;
    private TelecomManager mTelManager;


    private PhoneHelper() {
    }

    private static class SingleTon {
        private static PhoneHelper sInstance = new PhoneHelper();
    }

    public static PhoneHelper getInstance() {
        return SingleTon.sInstance;
    }

    public void init(Context context) {
        mContext = new WeakReference<>(context);

        mTelManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);

        mFloatingView = new FloatingView(context);
        mFloatingView.setListener(new FloatingView.OnCallListener() {
            @Override
            public void onGet() {
            }

            @Override
            public void onEnd() {
            }
        });
    }


}
