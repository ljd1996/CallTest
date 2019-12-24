package com.hearing.calltest.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author liujiadong
 * @since 2019/12/24
 */
public class PermissionUtil {
    private static final String PERMISSION_SP = "permission_sp";
    private static final String PERMISSION_OPEN = "permission_open";

    private PermissionUtil() {
    }

    private static class SingleTon {
        private static PermissionUtil sInstance = new PermissionUtil();
    }

    public static PermissionUtil getInstance() {
        return SingleTon.sInstance;
    }

    private SharedPreferences getSP(Context context) {
        return context.getSharedPreferences(PERMISSION_SP, Context.MODE_PRIVATE);
    }

    public boolean isOpen(Context context) {
        if (context == null) {
            return false;
        }
        return getSP(context).getBoolean(PERMISSION_OPEN, false);
    }

    public void setOpen(Context context) {
        if (context == null) {
            return;
        }
        SharedPreferences.Editor editor = getSP(context).edit();
        editor.putBoolean(PERMISSION_OPEN, true);
        editor.apply();
    }
}
