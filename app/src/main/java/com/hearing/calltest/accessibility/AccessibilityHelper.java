package com.hearing.calltest.accessibility;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;

/**
 * @author liujiadong
 * @since 2020/1/15
 */
public class AccessibilityHelper {

    private static boolean hasAccessibility(Context context, Class serviceClass) {
        if (context == null || serviceClass == null) {
            return false;
        }
        int ok = 0;
        try {
            ok = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
        }

        TextUtils.SimpleStringSplitter ms = new TextUtils.SimpleStringSplitter(':');
        if (ok == 1) {
            String settingValue = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                ms.setString(settingValue);
                while (ms.hasNext()) {
                    String accessibilityService = ms.next();
                    if (accessibilityService.contains(serviceClass.getSimpleName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void openAccessibility(Context context, Class service) {
        if (context == null || service == null) {
            return;
        }

        if (hasAccessibility(context, service)) {
            return;
        }
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
