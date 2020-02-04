package com.hearing.calltest.call;

import android.app.KeyguardManager;
import android.app.Notification;
import android.content.Context;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;

import com.hearing.calltest.widget.FloatingView;
import com.hearing.calltest.widget.LockGuideView;

/**
 * @author liujiadong
 * @since 2020/1/17
 */
public abstract class CallCore {

    protected static final String TAG = "CallCore";

    public static final String SYSTEM_CALL_PKG = "system_call";
    public static final String WHATS_APP_CALL_PKG = "com.whatsapp";
    public static final String WE_CHAT_CALL_PKG = "com.tencent.mm";

    protected static final int STATUS_NONE = 0;
    protected static final int STATUS_RINGING = 1;

    protected int mStatus = STATUS_NONE;

    Context mContext;
    FloatingView mFloatingView;
    Notification.Action[] mActions;

    private String mPackage;
    private boolean mClicked = false;

    CallCore(Context context, String pkg) {
        init(context, pkg);
    }

    private void init(Context context, String pkg) {
        if (context == null) {
            return;
        }
        mContext = context;
        mPackage = pkg;
        mFloatingView = new FloatingView(context);
        mFloatingView.setListener(new FloatingView.OnCallListener() {
            @Override
            public void onGet() {
                mClicked = true;
                acceptCall();
            }

            @Override
            public void onEnd() {
                mClicked = true;
                endCall();
            }
        });
    }

    public String getPackage() {
        return mPackage;
    }

    public void setPackage(String aPackage) {
        mPackage = aPackage;
    }

    /**
     * 有来电时：未接/挂断/通话结束
     */
    void showLockGuide() {
        if (!mClicked && isLocked()) {
            LockGuideView guideView = new LockGuideView(mContext);
            guideView.show();
        } else {
            mClicked = false;
        }
    }

    /**
     * 是否锁屏
     * 有密码亮屏：isKeyguardLocked = false, isKeyguardSecure = true, isDeviceLocked = false, isDeviceSecure = true
     * 无密码亮屏：isKeyguardLocked = false, isKeyguardSecure = false, isDeviceLocked = false, isDeviceSecure = false
     * 有密码锁屏：isKeyguardLocked = true, isKeyguardSecure = true, isDeviceLocked = true, isDeviceSecure = true
     * 无密码锁屏：isKeyguardLocked = true, isKeyguardSecure = false, isDeviceLocked = false, isDeviceSecure = false
     */
    protected boolean isLocked() {
        if (mContext != null) {
            KeyguardManager manager = (KeyguardManager) mContext.getSystemService(Context.KEYGUARD_SERVICE);
            return manager != null && manager.isKeyguardLocked();
        }
        return false;
    }

    protected void sendAction(int position) {
        if (mActions != null) {
            try {
                mActions[position].actionIntent.send();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mActions = null;
            }
        }
    }

    protected abstract void acceptCall();

    protected abstract void endCall();

    public abstract void onNotificationPosted(Notification notification);

    public abstract void onNotificationRemoved(Notification notification);

    public abstract void onDestroy();

    public static CallCore createCallCore(Context context, String pkg) {
        if (context == null || TextUtils.isEmpty(pkg)) {
            return null;
        }
        try {
            switch (pkg) {
                case SYSTEM_CALL_PKG:
                    return new SystemCallCore(context, pkg);
                case WHATS_APP_CALL_PKG:
                    return new WhatsAppCallCore(context, pkg);
                case WE_CHAT_CALL_PKG:
                    return new WeChatCallCore(context, pkg);
                default:
                    return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
