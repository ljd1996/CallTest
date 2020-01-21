package com.hearing.calltest.detail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hearing.calltest.R;
import com.hearing.calltest.business.RingtoneHelper;
import com.hearing.calltest.permission.PermissionManager;


/**
 * @author liujiadong
 * @since 2020/1/20
 */
public class DetailApplyActivity extends AppCompatActivity {

    private TextView mRingtoneView;
    private boolean mSetRingtone = true;
    private String mVideoPath;

    public static void startSelf(Context context, String path, boolean setRingtone) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, DetailApplyActivity.class);
        intent.putExtra(DetailActivity.KEY_VIDEO_PATH, path);
        intent.putExtra(DetailActivity.KEY_SET_RINGTONE, setRingtone);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_apply);

        handleIntent();
        init();
        setRingtone();
    }

    private void init() {
        mRingtoneView = findViewById(R.id.ringtone_apply_tv);
    }

    private void handleIntent() {
        mSetRingtone = getIntent().getBooleanExtra(DetailActivity.KEY_SET_RINGTONE, true);
        mVideoPath = getIntent().getStringExtra(DetailActivity.KEY_VIDEO_PATH);
    }

    private void setRingtone() {
        if (PermissionManager.getInstance().isSettingsEnabled(this)) {
            if (mSetRingtone) {
                RingtoneHelper.setRing(this, mVideoPath);
                mRingtoneView.setVisibility(View.VISIBLE);
            }
        } else {
            PermissionManager.getInstance().requestSettings(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PermissionManager.getInstance().onActivityResult(this, requestCode);
        if (PermissionManager.getInstance().isSettingsEnabled(this)) {
            if (mSetRingtone) {
                RingtoneHelper.setRing(this, mVideoPath);
            }
        }
    }
}
