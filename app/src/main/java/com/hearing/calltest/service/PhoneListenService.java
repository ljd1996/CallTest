package com.hearing.calltest.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.internal.telephony.ITelephony;
import com.hearing.calltest.MyPhoneCallListener;
import com.hearing.calltest.R;
import com.hearing.calltest.util.ContractsUtil;
import com.hearing.calltest.widget.FloatingView;

import java.lang.reflect.Method;


/**
 * @author liujiadong
 * @since 2019/12/17
 */
public class PhoneListenService extends Service {

    public static final String TAG = "LLL";

    private FloatingView mFloatingView;
    private TelecomManager mTelManager;

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
    }

    /**
     * Android 7 不能接电话
     */
    private void acceptCall() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (mTelManager != null) {
                if (checkSelfPermission(Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mTelManager.acceptRingingCall();
            }
        } else {
            if (mTelManager != null) {
                mTelManager.showInCallScreen(false);
            }
        }
    }

    /**
     * 部分Android 8 不能挂电话
     */
    @SuppressLint("MissingPermission")
    private void endCall() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (mTelManager != null) {
                mTelManager.endCall();
            }
        } else {
            try {
                Method method = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
                IBinder binder = (IBinder) method.invoke(null, new Object[]{Context.TELEPHONY_SERVICE});
                ITelephony telephony = ITelephony.Stub.asInterface(binder);
                telephony.endCall();
            } catch (Exception e) {
                if (mTelManager != null) {
                    mTelManager.showInCallScreen(false);
                }
                e.printStackTrace();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = buildNotification();
        if (notification != null) {
            startForeground(1, notification);
        }

        registerPhoneStateListener();
        return super.onStartCommand(intent, flags, startId);
    }

    private Notification buildNotification() {
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
                    .build();
        } else {
            notification = new Notification.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("来电秀")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();
        }
        return notification;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void registerPhoneStateListener() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
            try {
                MyPhoneCallListener listener = new MyPhoneCallListener();
                listener.setListener(new MyPhoneCallListener.OnCallStateChanged() {
                    @Override
                    public void onCallStateChanged(int state, String number) {
                        switch (state) {
                            case TelephonyManager.CALL_STATE_IDLE:
                                Log.d(TAG, "无状态...");
                                mFloatingView.hide();
                                break;
                            case TelephonyManager.CALL_STATE_OFFHOOK:
                                Log.d(TAG, "正在通话...");
                                mFloatingView.hide();
                                break;
                            case TelephonyManager.CALL_STATE_RINGING:
                                Log.d(TAG, "电话响铃...");
                                mFloatingView.show();
                                mFloatingView.setPerson(ContractsUtil.getContactName(PhoneListenService.this, number), number);
                                break;
                        }
                    }
                });
                tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
