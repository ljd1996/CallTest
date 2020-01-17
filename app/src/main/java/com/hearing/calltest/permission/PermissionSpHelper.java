package com.hearing.calltest.permission;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;

/**
 * @author liujiadong
 * @since 2019/12/24
 */
public class PermissionSpHelper {
    private static final String PERMISSION_SP = "permission_sp";
    private static final String LOCK_PERMISSION_OPEN = "lock_permission_open";
    private static final String LAUNCH_PERMISSION_OPEN = "launch_permission_open";
    private static final String SETTING_PERMISSION_OPEN = "setting_permission_open";

    private PermissionSpHelper() {
    }

    private static class SingleTon {
        private static PermissionSpHelper sInstance = new PermissionSpHelper();
    }

    public static PermissionSpHelper getInstance() {
        return SingleTon.sInstance;
    }

    private SharedPreferences getSP(Context context) {
        return context.getSharedPreferences(PERMISSION_SP, Context.MODE_PRIVATE);
    }

    public boolean isLockOpen(Context context) {
        if (context == null) {
            return false;
        }
        return getSP(context).getBoolean(LOCK_PERMISSION_OPEN, false);
    }

    public void setLockOpen(Context context) {
        if (context == null) {
            return;
        }
        SharedPreferences.Editor editor = getSP(context).edit();
        editor.putBoolean(LOCK_PERMISSION_OPEN, true);
        editor.apply();
    }

    public boolean isLaunchOpen(Context context) {
        if (context == null) {
            return false;
        }
        return getSP(context).getBoolean(LAUNCH_PERMISSION_OPEN, false);
    }

    public void setLaunchOpen(Context context) {
        if (context == null) {
            return;
        }
        SharedPreferences.Editor editor = getSP(context).edit();
        editor.putBoolean(LAUNCH_PERMISSION_OPEN, true);
        editor.apply();
    }

    public boolean isSettingOpen(Context context) {
        if (context == null) {
            return false;
        }
        return getSP(context).getBoolean(SETTING_PERMISSION_OPEN, false);
    }

    public void setSettingOpen(Context context) {
        if (context == null) {
            return;
        }
        SharedPreferences.Editor editor = getSP(context).edit();
        editor.putBoolean(SETTING_PERMISSION_OPEN, true);
        editor.apply();
    }
}
