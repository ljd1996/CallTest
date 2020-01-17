package com.hearing.calltest.call;

import android.Manifest;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;

import com.android.internal.telephony.ITelephony;
import com.hearing.calltest.business.ContactHelper;
import com.hearing.calltest.service.EmptyNotificationListenService;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author liujiadong
 * @since 2020/1/17
 */
public class SystemCallCore extends CallCore {

    private TelecomManager mTelManager;
    private BroadcastReceiver mPhoneStateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, "android.intent.action.PHONE_STATE")) {
                if (mFloatingView == null) {
                    return;
                }

                String state = intent.getStringExtra("state");
                String number = intent.getStringExtra("incoming_number");

                Log.d(TAG, this + " state = " + state + ", number = " + number);

                if (TelephonyManager.EXTRA_STATE_RINGING.equalsIgnoreCase(state)) {
                    mFloatingView.show(number);
                    mFloatingView.setPerson(ContactHelper.getInstance().getContactName(mContext, number), number);
                } else {
                    mFloatingView.hide();
                }
            }
        }
    };


    SystemCallCore(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        if (context == null) {
            return;
        }

        mTelManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PHONE_STATE");
        context.registerReceiver(mPhoneStateReceiver, filter);
    }

    @Override
    protected void acceptCall() {
        if (mContext == null) {
            return;
        }

        if (mTelManager != null) {
            if (mContext.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mTelManager.showInCallScreen(false);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (mTelManager != null) {
                mTelManager.acceptRingingCall();
            }
        } else {
            sendHeadsetHook(true);
        }
    }

    @Override
    protected void endCall() {
        if (mContext == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (mTelManager != null) {
                mTelManager.endCall();
            }
        } else {
            sendHeadsetHook(false);
            try {
                Method method = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
                IBinder binder = (IBinder) method.invoke(null, new Object[]{Context.TELEPHONY_SERVICE});
                ITelephony telephony = ITelephony.Stub.asInterface(binder);
                telephony.endCall();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onNotificationPosted(Notification notification) {

    }

    /**
     * 通过模拟耳机接听/挂断电话
     *
     * @param isAnswer: true, 接听; false: 挂断
     */
    private void sendHeadsetHook(boolean isAnswer) {
        if (mContext == null) {
            return;
        }

        MediaSessionManager sessionManager = (MediaSessionManager) mContext.getSystemService(Context.MEDIA_SESSION_SERVICE);

        if (sessionManager == null) {
            return;
        }

        try {
            List<MediaController> controllers = sessionManager.getActiveSessions(new ComponentName(mContext, EmptyNotificationListenService.class));

            for (MediaController m : controllers) {
                if ("com.android.server.telecom".equals(m.getPackageName())) {

                    if (isAnswer) {
                        m.dispatchMediaButtonEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK));
                    } else {
                        long now = SystemClock.uptimeMillis();
                        m.dispatchMediaButtonEvent(new KeyEvent(now, now, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK,
                                1, 0, KeyCharacterMap.VIRTUAL_KEYBOARD,
                                0, KeyEvent.FLAG_LONG_PRESS, InputDevice.SOURCE_KEYBOARD));
                    }

                    m.dispatchMediaButtonEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
                    Log.d(TAG, this + " headset sent to tel");
                    break;
                }
            }
        } catch (Exception e) {
            Log.d(TAG, this + " Permission error, Access to notification not granted to the app.");
        }
    }
}
