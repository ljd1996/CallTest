package com.hearing.calltest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.PermissionChecker;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hearing.calltest.adapter.MyAdapter;
import com.hearing.calltest.service.PhoneListenService;
import com.hearing.calltest.util.PermissionUtil;
import com.hearing.calltest.util.Util;
import com.hearing.calltest.widget.PlayerDialog;

import java.util.List;
import java.util.Set;


public class MainActivity extends AppCompatActivity {

    private String[] mPermissions = new String[]{
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            "",
    };

    private static final int REQUEST_ID_POPUP = 0;
    private static final int REQUEST_ID_PERMISSION = 1;
    private static final int REQUEST_ID_NOTIFICATION = 2;

    private RecyclerView mVideoRecycleView;
    private RecyclerView mRingRecycleView;
    private MyAdapter mVideoAdapter;
    private MyAdapter mRingAdapter;

    private String mVideoPath;
    private String mRingPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mPermissions[6] = Manifest.permission.ANSWER_PHONE_CALLS;
        }

        getPermissions();

        init();
    }

    private void init() {
        mVideoPath = getFilesDir().getAbsolutePath() + "/video";
        mRingPath = getFilesDir().getAbsolutePath() + "/ring";

        mVideoRecycleView = findViewById(R.id.video_rv);
        mRingRecycleView = findViewById(R.id.ring_rv);
        mVideoRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mRingRecycleView.setLayoutManager(new LinearLayoutManager(this));

        mVideoAdapter = new MyAdapter();
        mVideoRecycleView.setAdapter(mVideoAdapter);

        mRingAdapter = new MyAdapter();
        mRingRecycleView.setAdapter(mRingAdapter);

        mVideoAdapter.setOnItemClickListener((index, holder) -> {
            String path = mVideoPath + "/" + mVideoAdapter.getData(index);
            PlayerDialog dialog = new PlayerDialog(MainActivity.this, path);
            dialog.setListener(() -> {
                holder.mTextView.setTextColor(Color.RED);

            });
            dialog.show();
        });

        mRingAdapter.setOnItemClickListener((index, holder) -> {
            String path = mRingPath + "/" + mRingAdapter.getData(index);
            PlayerDialog dialog = new PlayerDialog(MainActivity.this, path);
            dialog.setListener(() -> {
                holder.mTextView.setTextColor(Color.RED);
                Util.setRing(MainActivity.this, path);
            });
            dialog.show();
        });

        copyData("video", mVideoPath, list -> {
            if (list != null) {
                mVideoAdapter.setData(list);
            }
        });
        copyData("ring", mRingPath, list -> {
            if (list != null) {
                mRingAdapter.setData(list);
            }
        });
    }

    private void copyData(final String assetPath, final String filePath, final OnDataLoadListener listener) {
        new Thread(() -> {
            Util.CopyAssets(MainActivity.this, assetPath, filePath);
            if (listener != null) {
                listener.onLoadFinish(Util.getAllName(filePath));
            }
        }).start();
    }

    private void getPermissions() {
        if (!Settings.canDrawOverlays(this)) {
            new AlertDialog.Builder(this)
                    .setTitle("获取悬浮窗权限")
                    .setMessage("点击跳转到悬浮窗权限页面")
                    .setPositiveButton("确认", (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        startActivityForResult(intent, REQUEST_ID_POPUP);
                    })
                    .setNegativeButton("取消", (dialog, which) -> MainActivity.this.finish())
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
        if (!PermissionUtil.getInstance().isLockOpen(this)) {
            new AlertDialog.Builder(this)
                    .setMessage("请开启锁屏显示权限")
                    .setPositiveButton("取消", (dialog, which) -> dialog.dismiss())
                    .setNegativeButton("确认", (dialog, which) -> {
                        PermissionUtil.getInstance().setLockOpen(MainActivity.this);
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.fromParts("package", getPackageName(), null));
                        startActivity(intent);
                    })
                    .create()
                    .show();
        }
        if (!PermissionUtil.getInstance().isLaunchOpen(this)) {
            new AlertDialog.Builder(this)
                    .setMessage("请开启自启动权限")
                    .setPositiveButton("取消", (dialog, which) -> dialog.dismiss())
                    .setNegativeButton("确认", (dialog, which) -> {
                        PermissionUtil.getInstance().setLaunchOpen(MainActivity.this);
                        try {
                            startActivity(getAutoStartSettingIntent(MainActivity.this));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    })
                    .create()
                    .show();
        }
        if (!PermissionUtil.getInstance().isSettingOpen(this)) {
            new AlertDialog.Builder(this)
                    .setMessage("请允许修改系统设置")
                    .setPositiveButton("取消", (dialog, which) -> dialog.dismiss())
                    .setNegativeButton("确认", (dialog, which) -> {
                        PermissionUtil.getInstance().setSettingOpen(MainActivity.this);
                        requestWriteSettings();
                    })
                    .create()
                    .show();
        }

        init();
    }

    private void requestWriteSettings() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    private Intent getAutoStartSettingIntent(Context context) throws Exception {
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

    interface OnDataLoadListener {
        void onLoadFinish(List<String> list);
    }
}
