package com.hearing.calltest.detail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hearing.calltest.R;
import com.hearing.calltest.business.ContactHelper;
import com.hearing.calltest.business.VideoDBHelper;
import com.hearing.calltest.permission.PermissionActivity;
import com.hearing.calltest.permission.PermissionManager;

/**
 * @author liujiadong
 * @since 2020/1/20
 */
public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "LLL";
    public static final String KEY_VIDEO_PATH = "video_path";
    public static final String KEY_SET_RINGTONE = "key_set_ringtone";

    public static final int REQUEST_ID_PICK_VIDEO_CONTACT = 0;

    private VideoView mVideoView;
    private String mVideoPath;
    private boolean mSetRingtone = true;
    private String mContact = VideoDBHelper.UNKNOWN_NUMBER;


    public static void startSelf(Context context, String path) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(DetailActivity.KEY_VIDEO_PATH, path);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        handleIntent();
        init();
    }

    private void handleIntent() {
        mVideoPath = getIntent().getStringExtra(KEY_VIDEO_PATH);
    }

    private void init() {
        mVideoView = findViewById(R.id.video_view);
        mVideoView.setOnPreparedListener(mp -> {
            mp.start();
            mp.setLooping(true);
        });
        mVideoView.setVideoPath(mVideoPath);
        mVideoView.start();
    }

    private void selectVideoContact() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, REQUEST_ID_PICK_VIDEO_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ID_PICK_VIDEO_CONTACT) {
            if (data != null) {
                mContact = ContactHelper.getInstance().getContacts(this, data.getData());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        if (PermissionManager.getInstance().isContactEnabled(this)) {
            selectVideoContact();
        }
    }

    public void setRingtone(View view) {
        mSetRingtone = !mSetRingtone;
        ((ImageView) view).setImageResource(mSetRingtone ? R.drawable.ringtone_set : R.drawable.ringtone_close);
    }

    public void setContact(View view) {
        if (PermissionManager.getInstance().isContactEnabled(this)) {
            selectVideoContact();
        } else {
            PermissionManager.getInstance().requestContact(this);
        }
    }

    public void setShow(View view) {
        if (PermissionManager.getInstance().checkEssential(this)) {
            VideoDBHelper.getInstance().setSelectVideo(this, mContact, mVideoPath);
            DetailApplyActivity.startSelf(this, mVideoPath, mSetRingtone);
        } else {
            PermissionActivity.startSelf(this);
        }
    }
}
