package com.hearing.calltest.util;

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
public class PermissionUtil {
    private static final String PERMISSION_SP = "permission_sp";
    private static final String LOCK_PERMISSION_OPEN = "lock_permission_open";
    private static final String LAUNCH_PERMISSION_OPEN = "launch_permission_open";
    private static final String SETTING_PERMISSION_OPEN = "setting_permission_open";

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

    public static Intent getAutoStartSettingIntent(Context context) throws Exception {
        ComponentName componentName = null;
        String brand = Build.MANUFACTURER;
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        switch (brand.toLowerCase()) {
            case "samsung"://三星
                componentName = new ComponentName("com.samsung.android.sm",
                        "com.samsung.android.sm.app.dashboard.SmartManagerDashBoardActivity");
                break;
            case "huawei"://华为
                componentName = ComponentName.unflattenFromString("com.huawei.systemmanager/.startupmgr.ui.StartupNormalAppListActivity");
                break;
            case "xiaomi"://小米
                componentName = new ComponentName("com.miui.securitycenter",
                        "com.miui.permcenter.autostart.AutoStartManagementActivity");
                break;
            case "vivo"://VIVO
                componentName = new ComponentName("com.iqoo.secure",
                        "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity");
                break;
            case "oppo"://OPPO
//            componentName = new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity");
                componentName = new ComponentName("com.coloros.oppoguardelf",
                        "com.coloros.powermanager.fuelgaue.PowerUsageModelActivity");
                break;
            case "yulong":
            case "360"://360
                componentName = new ComponentName("com.yulong.android.coolsafe",
                        "com.yulong.android.coolsafe.ui.activity.autorun.AutoRunListActivity");
                break;
            case "meizu"://魅族
                componentName = new ComponentName("com.meizu.safe",
                        "com.meizu.safe.permission.SmartBGActivity");
                break;
            case "oneplus"://一加
                componentName = new ComponentName("com.oneplus.security",
                        "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity");
                break;
            case "letv"://乐视
                intent.setAction("com.letv.android.permissionautoboot");
            default://其他
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.fromParts("package", context.getPackageName(), null));
                break;
        }
        intent.setComponent(componentName);
        return intent;
    }
}
