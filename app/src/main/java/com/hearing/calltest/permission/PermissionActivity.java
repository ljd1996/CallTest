package com.hearing.calltest.permission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hearing.calltest.R;

/**
 * @author liujiadong
 * @since 2020/1/20
 */
public class PermissionActivity extends AppCompatActivity {

    private ImageView mSdcardIv;
    private ImageView mPhoneIv;
    private ImageView mFloatIv;
    private ImageView mNotificationIv;

    public static void startSelf(Context context) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, PermissionActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        init();
        updateUI();
    }

    private void init() {
        mSdcardIv = findViewById(R.id.sdcard_iv);
        mPhoneIv = findViewById(R.id.phone_iv);
        mFloatIv = findViewById(R.id.float_iv);
        mNotificationIv = findViewById(R.id.notification_iv);
    }

    private void updateUI() {
        mSdcardIv.setImageResource(PermissionManager.getInstance().isSdcardEnabled(this)
                ? R.drawable.get_permission : R.drawable.no_permission);
        mPhoneIv.setImageResource(PermissionManager.getInstance().isPhoneEnabled(this)
                ? R.drawable.get_permission : R.drawable.no_permission);
        mFloatIv.setImageResource(PermissionManager.getInstance().isFloatEnabled(this)
                ? R.drawable.get_permission : R.drawable.no_permission);
        mNotificationIv.setImageResource(PermissionManager.getInstance().isNotificationEnabled(this)
                ? R.drawable.get_permission : R.drawable.no_permission);
        if (PermissionManager.getInstance().checkEssential(this)) {
            finish();
        }
    }

    public void getPermission(View view) {
        PermissionManager.getInstance().requestEssential(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        updateUI();
        PermissionManager.getInstance().onActivityResult(this, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        updateUI();
        PermissionManager.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }
}
