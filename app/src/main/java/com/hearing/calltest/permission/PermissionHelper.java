package com.hearing.calltest.permission;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

/**
 * @author liujiadong
 * @since 2020/1/16
 */
public class PermissionHelper {

    private static final String sManufacturer = Build.MANUFACTURER.toLowerCase();


    private PermissionHelper() {
    }

    private static class SingleTon {
        private static PermissionHelper sInstance = new PermissionHelper();
    }

    public static PermissionHelper getInstance() {
        return SingleTon.sInstance;
    }

    private boolean isIntentIllegal(Context paramContext, Intent paramIntent) {
        return paramContext.getPackageManager().queryIntentActivities(paramIntent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }

    private Intent getAll(Context paramContext) {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", paramContext.getPackageName(), null));
        return intent;
    }

    private Intent getHuaWei(Context paramContext) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity"));
        if (isIntentIllegal(paramContext, intent))
            return intent;
        intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.addviewmonitor.AddViewMonitorActivity"));
        if (isIntentIllegal(paramContext, intent))
            return intent;
        intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.notificationmanager.ui.NotificationManagmentActivity"));
        return intent;
    }

    private Intent getXiaoMi(Context paramContext) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.putExtra("extra_pkgname", paramContext.getPackageName());
        if (isIntentIllegal(paramContext, intent))
            return intent;
        intent.setPackage("com.miui.securitycenter");
        if (isIntentIllegal(paramContext, intent))
            return intent;
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        return intent;
    }

    private Intent getOPPO(Context paramContext) {
        Intent intent = new Intent();
        intent.putExtra("packageName", paramContext.getPackageName());
        intent.setClassName("com.color.safecenter", "com.color.safecenter.permission.floatwindow.FloatWindowListActivity");
        if (isIntentIllegal(paramContext, intent))
            return intent;
        intent.setClassName("com.coloros.safecenter", "com.coloros.safecenter.sysfloatwindow.FloatWindowListActivity");
        if (isIntentIllegal(paramContext, intent))
            return intent;
        intent.setClassName("com.oppo.safe", "com.oppo.safe.permission.PermissionAppListActivity");
        return intent;
    }

    private Intent getIqoo(Context paramContext) {
        Intent intent = new Intent();
        intent.setClassName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.FloatWindowManager");
        intent.putExtra("packagename", paramContext.getPackageName());
        if (isIntentIllegal(paramContext, intent))
            return intent;
        intent.setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.safeguard.SoftPermissionDetailActivity"));
        return intent;
    }

    private Intent getMeiZu(Context paramContext) {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.putExtra("packageName", paramContext.getPackageName());
        intent.setComponent(new ComponentName("com.meizu.safe", "com.meizu.safe.security.AppSecActivity"));
        return intent;
    }

    public void jumpPermissionPage(Context context) {
        if (context == null) {
            return;
        }
        Intent intent;
        if (sManufacturer.contains("huawei")) {
            intent = getHuaWei(context);
        } else if (sManufacturer.contains("xiaomi")) {
            intent = getXiaoMi(context);
        } else if (sManufacturer.contains("oppo")) {
            intent = getOPPO(context);
        } else if (sManufacturer.contains("vivo")) {
            intent = getIqoo(context);
        } else if (sManufacturer.contains("meizu")) {
            intent = getMeiZu(context);
        } else {
            intent = getAll(context);
        }
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public Intent getAutoStartSettingIntent(Context context) throws Exception {
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
