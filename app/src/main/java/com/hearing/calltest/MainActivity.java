package com.hearing.calltest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.PermissionChecker;

import com.hearing.calltest.service.PhoneListenService;
import com.hearing.calltest.util.PermissionUtil;

import java.util.Set;


public class MainActivity extends AppCompatActivity {

    private String[] mPermissions = new String[]{
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            "",
    };

    private static final int REQUEST_ID_POPUP = 0;
    private static final int REQUEST_ID_PERMISSION = 1;
    private static final int REQUEST_ID_NOTIFICATION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mPermissions[4] = Manifest.permission.ANSWER_PHONE_CALLS;
        }

        getPermissions();
    }

    private void getPermissions() {
        if (!Settings.canDrawOverlays(this)) {
            new AlertDialog.Builder(this)
                    .setTitle("获取悬浮窗权限")
                    .setMessage("点击跳转到悬浮窗权限页面")
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                            startActivityForResult(intent, REQUEST_ID_POPUP);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.this.finish();
                        }
                    })
                    .show();
        } else if (!isNotificationEnabled(this)) {
            openNotificationListenSettings();
        } else {
            if (checkPermission()) {
                afterPermissions();
            } else {
                ActivityCompat.requestPermissions(this, mPermissions, REQUEST_ID_PERMISSION);
            }
        }
    }

    // 判断是否打开了通知监听权限
    public boolean isNotificationEnabled(Context context) {
        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(this);
        return packageNames.contains(context.getPackageName());
    }

    public void openNotificationListenSettings() {
        try {
            Intent intent;
            intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            startActivityForResult(intent, REQUEST_ID_NOTIFICATION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("WrongConstant")
    private boolean checkPermission() {
        boolean hasPermission = true;
        for (String permission : mPermissions) {
            if (!TextUtils.isEmpty(permission) && PermissionChecker.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                hasPermission = false;
            }
        }
        return hasPermission;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ID_POPUP) {
            getPermissions();
        } else if (requestCode == REQUEST_ID_NOTIFICATION) {
            getPermissions();
        }
    }

    private void afterPermissions() {
        startService(new Intent(this, PhoneListenService.class));
        if (!PermissionUtil.getInstance().isOpen(this)) {
            PermissionUtil.getInstance().setOpen(this);
            new AlertDialog.Builder(this)
                    .setMessage("请开启锁屏显示权限")
                    .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.fromParts("package", getPackageName(), null));
                            startActivity(intent);
                        }
                    })
                    .create()
                    .show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_ID_PERMISSION && grantResults.length > 0) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    finish();
                }
            }
            afterPermissions();
        }
    }
}
