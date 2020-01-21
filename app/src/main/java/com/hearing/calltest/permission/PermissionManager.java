package com.hearing.calltest.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Set;


/**
 * @author liujiadong
 * @since 2020/1/20
 */
public class PermissionManager {

    private static final int REQUEST_ID_PERMISSION = 0;
    private static final int REQUEST_ID_NOTIFICATION = 1;
    private static final int REQUEST_ID_POPUP = 2;
    private static final int REQUEST_ID_CONTACT_PERMISSION = 3;
    private static final int REQUEST_ID_SETTINGS_PERMISSION = 4;

    /**
     * 必需权限：读写sdcard，来电状态，接听/挂断电话，悬浮窗，通知栏
     */
    private String[] mEssentialPermissions = new String[]{
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            "",
    };

    /**
     * 联系人权限
     */
    private String[] mContactPermissions = new String[]{
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
    };

    private PermissionManager() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mEssentialPermissions[4] = Manifest.permission.ANSWER_PHONE_CALLS;
        }
    }

    private static class SingleTon {
        private static PermissionManager sInstance = new PermissionManager();
    }

    public static PermissionManager getInstance() {
        return SingleTon.sInstance;
    }

    public boolean checkEssential(Context context) {
        if (context == null) {
            return false;
        }
        for (String permission : mEssentialPermissions) {
            if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return isNotificationEnabled(context) && isFloatEnabled(context);
    }

    public void requestEssential(Activity activity) {
        if (activity == null) {
            return;
        }
        ActivityCompat.requestPermissions(activity, mEssentialPermissions, REQUEST_ID_PERMISSION);
    }

    public void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults) {
        if (activity == null || grantResults == null) {
            return;
        }
        if (requestCode == REQUEST_ID_PERMISSION && grantResults.length > 0) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[i])) {
                        ActivityCompat.requestPermissions(activity, mEssentialPermissions, REQUEST_ID_PERMISSION);
                    } else {
                        PermissionUtil.jumpPermissionPage(activity);
                    }
                }
            }
            openFloatSettings(activity);
            openNotificationSettings(activity);
        }
    }

    public void onActivityResult(Activity activity, int requestCode) {
        if (activity == null) {
            return;
        }
    }

    public boolean isContactEnabled(Context context) {
        return isPermissionsEnabled(context, mContactPermissions);
    }

    public void requestContact(Activity activity) {
        if (activity == null) {
            return;
        }
        ActivityCompat.requestPermissions(activity, mContactPermissions, REQUEST_ID_CONTACT_PERMISSION);
    }

    public void requestSettings(Activity activity) {
        try {
            if (isSettingsEnabled(activity)) {
                return;
            }
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            activity.startActivityForResult(intent, REQUEST_ID_SETTINGS_PERMISSION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isSdcardEnabled(Context context) {
        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
        };

        return isPermissionsEnabled(context, permissions);
    }

    public boolean isPhoneEnabled(Context context) {
        String[] permissions = new String[]{
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CALL_PHONE,
                "",
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            permissions[2] = Manifest.permission.ANSWER_PHONE_CALLS;
        }

        return isPermissionsEnabled(context, permissions);
    }

    public boolean isNotificationEnabled(Context context) {
        if (context == null) {
            return false;
        }
        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(context);
        return packageNames.contains(context.getPackageName());
    }

    public boolean isFloatEnabled(Context context) {
        if (context == null) {
            return false;
        }
        return Settings.canDrawOverlays(context);
    }

    public boolean isSettingsEnabled(Context context) {
        if (context == null) {
            return false;
        }
        return Settings.System.canWrite(context);
    }

    private boolean isPermissionsEnabled(Context context, String[] permissions) {
        if (context == null || permissions == null) {
            return false;
        }
        for (String permission : permissions) {
            if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void openNotificationSettings(Activity activity) {
        try {
            if (isNotificationEnabled(activity)) {
                return;
            }
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            activity.startActivityForResult(intent, REQUEST_ID_NOTIFICATION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openFloatSettings(Activity activity) {
        try {
            if (isFloatEnabled(activity)) {
                return;
            }
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            activity.startActivityForResult(intent, REQUEST_ID_POPUP);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
