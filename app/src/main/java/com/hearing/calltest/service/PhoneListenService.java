package com.hearing.calltest.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import androidx.annotation.Nullable;

import com.android.internal.telephony.ITelephony;
import com.hearing.calltest.MainActivity;
import com.hearing.calltest.R;
import com.hearing.calltest.util.ContractsUtil;
import com.hearing.calltest.util.Util;
import com.hearing.calltest.widget.FloatingView;

import java.lang.reflect.Method;
import java.util.List;


/**
 * @author liujiadong
 * @since 2019/12/17
 */
public class PhoneListenService extends Service {

    public static final String TAG = "LLL";

    private FloatingView mFloatingView;
    private TelecomManager mTelManager;

    private BroadcastReceiver mPhoneStateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, "android.intent.action.PHONE_STATE")) {
                String state = intent.getStringExtra("state");
                String number = intent.getStringExtra("incoming_number");

                Log.d(TAG, this + " state = " + state + ", number = " + number);

                if (TelephonyManager.EXTRA_STATE_RINGING.equalsIgnoreCase(state)) {
                    mFloatingView.show(number);
                    mFloatingView.setPerson(ContractsUtil.getContactName(PhoneListenService.this, number), number);
                } else {
                    mFloatingView.hide();
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        mTelManager = (TelecomManager) getSystemService(Context.TELECOM_SERVICE);

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

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PHONE_STATE");
        registerReceiver(mPhoneStateReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, this + " onStartCommand");
//        Notification notification = buildNotification();
//        if (notification != null) {
//            startForeground(1, notification);
//        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, this + " onBind");
        return null;
    }

    private Notification buildNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager == null) {
                return null;
            }
            String channelId = getString(R.string.app_name);
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription(channelId);
            notificationChannel.setSound(null, null);
            notificationManager.createNotificationChannel(notificationChannel);

            notification = new Notification.Builder(this, channelId)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("来电秀")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(contentIntent)
                    .build();
        } else {
            notification = new Notification.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("来电秀")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(contentIntent)
                    .build();
        }
        return notification;
    }

    @SuppressLint("MissingPermission")
    private void acceptCall() {
        if (mTelManager != null) {
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

    @SuppressLint("MissingPermission")
    private void endCall() {
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
    public void onDestroy() {
        Log.d(TAG, this + " onDestroy");
        Util.startMainActivity(this);
        super.onDestroy();
    }

    /**
     * 通过模拟耳机接听/挂断电话
     *
     * @param isAnswer: true, 接听; false: 挂断
     */
    private void sendHeadsetHook(boolean isAnswer) {
        MediaSessionManager sessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);

        if (sessionManager == null) {
            return;
        }

        try {
            List<MediaController> controllers = sessionManager.getActiveSessions(new ComponentName(this, EmptyNotificationListenService.class));

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
        } catch (SecurityException e) {
            Log.d(TAG, this + " Permission error, Access to notification not granted to the app.");
        }
    }
}
