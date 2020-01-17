package com.hearing.calltest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.text.TextUtils;

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
import com.hearing.calltest.business.ContactHelper;
import com.hearing.calltest.business.RingtoneHelper;
import com.hearing.calltest.permission.PermissionHelper;
import com.hearing.calltest.service.PhoneListenService;
import com.hearing.calltest.util.DialogUtil;
import com.hearing.calltest.permission.PermissionSpHelper;
import com.hearing.calltest.util.Util;
import com.hearing.calltest.business.VideoDBHelper;
import com.hearing.calltest.widget.PlayerDialog;

import java.util.List;
import java.util.Set;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "LLL";

    private String[] mPermissions = new String[]{
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            "",
    };

    private static final int REQUEST_ID_POPUP = 0;
    private static final int REQUEST_ID_PERMISSION = 1;
    private static final int REQUEST_ID_NOTIFICATION = 2;
    private static final int REQUEST_ID_PICK_RING_CONTACT = 3;
    private static final int REQUEST_ID_PICK_VIDEO_CONTACT = 4;

    private RecyclerView mVideoRecycleView;
    private RecyclerView mRingRecycleView;
    private MyAdapter mVideoAdapter;
    private MyAdapter mRingAdapter;

    private String mVideoPath;
    private String mRingPath;

    private int mSelectRingIndex = 0;
    private String mSelectRingPath = "";

    private int mSelectVideoIndex = 0;
    private String mSelectVideoPath = "";

    private Handler mHandle = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mPermissions[7] = Manifest.permission.ANSWER_PHONE_CALLS;
        }

        getPermissions();

        init();
    }

    private void init() {

        try {
            mVideoPath = getExternalFilesDir(null).getAbsolutePath() + "/video";
            mRingPath = getExternalFilesDir(null).getAbsolutePath() + "/ring";
        } catch (Exception e) {
            mVideoPath = getFilesDir().getAbsolutePath() + "/video";
            mRingPath = getFilesDir().getAbsolutePath() + "/ring";
        }

        mVideoRecycleView = findViewById(R.id.video_rv);
        mRingRecycleView = findViewById(R.id.ring_rv);
        mVideoRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mRingRecycleView.setLayoutManager(new LinearLayoutManager(this));

        mVideoAdapter = new MyAdapter();
        mVideoRecycleView.setAdapter(mVideoAdapter);

        mRingAdapter = new MyAdapter();
        mRingRecycleView.setAdapter(mRingAdapter);

        mVideoAdapter.setOnItemClickListener((index) -> {
            if (index == mVideoAdapter.getItemCount() - 1) {
                setVideo(index, RingtoneHelper.NO_PATH);
                return;
            }
            String path = mVideoPath + "/" + mVideoAdapter.getData(index);
            PlayerDialog dialog = new PlayerDialog(MainActivity.this, path);
            dialog.setListener(() -> setVideo(index, path));
            dialog.show();
        });

        mRingAdapter.setOnItemClickListener((index) -> {
            if (index == mRingAdapter.getItemCount() - 1) {
                setRing(index, RingtoneHelper.NO_PATH);
                return;
            }
            String path = mRingPath + "/" + mRingAdapter.getData(index);
            PlayerDialog dialog = new PlayerDialog(MainActivity.this, path);
            dialog.setListener(() -> setRing(index, path));
            dialog.show();
        });

        copyData("video", mVideoPath, list -> {
            if (list != null) {
                list.add("不设置视频");
                mHandle.post(() -> mVideoAdapter.setData(list));
            }
        });
        copyData("ring", mRingPath, list -> {
            if (list != null) {
                list.add("不设置铃声");
                mHandle.post(() -> mRingAdapter.setData(list));
            }
        });
    }

    private void setRing(int index, String path) {
        DialogUtil.showDialog(this, "是否为指定联系人设置铃声？", "全部联系人", "选择联系人",
                (dialog, which) -> {
                    dialog.dismiss();
                    mRingAdapter.setSelectIndex(index);
                    RingtoneHelper.setRing(MainActivity.this, path);
                }, (dialog, which) -> {
                    dialog.dismiss();
                    mSelectRingIndex = index;
                    mSelectRingPath = path;
                    selectRingContact();
                });
    }

    private void setVideo(int index, String path) {
        DialogUtil.showDialog(this, "是否为指定联系人设置来电秀？", "全部联系人", "选择联系人",
                (dialog, which) -> {
                    dialog.dismiss();
                    mVideoAdapter.setSelectIndex(index);
                    RingtoneHelper.setRing(MainActivity.this, path);
                    VideoDBHelper.getInstance().setSelectVideo(MainActivity.this, VideoDBHelper.UNKNOWN_NUMBER, path);
                }, (dialog, which) -> {
                    dialog.dismiss();
                    mSelectVideoIndex = index;
                    mSelectVideoPath = path;
                    selectVideoContact();
                });
    }

    private void selectRingContact() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, REQUEST_ID_PICK_RING_CONTACT);
    }

    private void selectVideoContact() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, REQUEST_ID_PICK_VIDEO_CONTACT);
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
                        intent.setData(Uri.parse("package:" + getPackageName()));
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
        } else if (requestCode == REQUEST_ID_PICK_RING_CONTACT) {
            if (data != null) {
                String number = ContactHelper.getInstance().getContacts(this, data.getData());
                if (!TextUtils.isEmpty(number) && !TextUtils.isEmpty(mSelectRingPath)) {
                    mRingAdapter.setSelectIndex(mSelectRingIndex);
                    RingtoneHelper.setRing(MainActivity.this, mSelectRingPath, number);
                }
            }
        } else if (requestCode == REQUEST_ID_PICK_VIDEO_CONTACT) {
            if (data != null) {
                String number = ContactHelper.getInstance().getContacts(this, data.getData());
                if (!TextUtils.isEmpty(number) && !TextUtils.isEmpty(mSelectVideoPath)) {
                    mVideoAdapter.setSelectIndex(mSelectVideoIndex);
                    RingtoneHelper.setRing(MainActivity.this, mSelectVideoPath, number);
                    VideoDBHelper.getInstance().setSelectVideo(MainActivity.this, number, mSelectVideoPath);
                }
            }
        }
    }

    private void afterPermissions() {
        startService(new Intent(this, PhoneListenService.class));
        if (!PermissionSpHelper.getInstance().isLockOpen(this)) {
            new AlertDialog.Builder(this)
                    .setMessage("请开启锁屏显示权限")
                    .setPositiveButton("取消", (dialog, which) -> dialog.dismiss())
                    .setNegativeButton("确认", (dialog, which) -> {
                        PermissionSpHelper.getInstance().setLockOpen(MainActivity.this);
                        PermissionHelper.getInstance().jumpPermissionPage(MainActivity.this);
                    })
                    .create()
                    .show();
        }
        if (!PermissionSpHelper.getInstance().isLaunchOpen(this)) {
            new AlertDialog.Builder(this)
                    .setMessage("请开启自启动权限")
                    .setPositiveButton("取消", (dialog, which) -> dialog.dismiss())
                    .setNegativeButton("确认", (dialog, which) -> {
                        PermissionSpHelper.getInstance().setLaunchOpen(MainActivity.this);
                        try {
                            startActivity(PermissionHelper.getInstance().getAutoStartSettingIntent(MainActivity.this));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    })
                    .create()
                    .show();
        }
        if (!PermissionSpHelper.getInstance().isSettingOpen(this)) {
            new AlertDialog.Builder(this)
                    .setMessage("请允许修改系统设置")
                    .setPositiveButton("取消", (dialog, which) -> dialog.dismiss())
                    .setNegativeButton("确认", (dialog, which) -> {
                        PermissionSpHelper.getInstance().setSettingOpen(MainActivity.this);
                        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    })
                    .create()
                    .show();
        }

        init();
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
