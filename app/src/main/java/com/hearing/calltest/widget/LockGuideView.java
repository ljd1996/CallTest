package com.hearing.calltest.widget;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.hearing.calltest.R;
import com.hearing.calltest.permission.PermissionUtil;

/**
 * @author liujiadong
 * @since 2020/1/21
 */
public class LockGuideView {

    private View mView;
    private WindowManager mWindowManager;
    private boolean mShown = false;

    public LockGuideView(Context context) {
        mView = LayoutInflater.from(context).inflate(R.layout.float_lock_guide, null);

        mView.findViewById(R.id.close).setOnClickListener(v -> hide());
        mView.findViewById(R.id.go_open).setOnClickListener(v -> {
            hide();
            PermissionUtil.jumpPermissionPage(context);
        });

        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    public void show() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        }
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        try {
            mWindowManager.addView(mView, params);
            mShown = true;
        } catch (Exception e) {
        }
    }

    public void hide() {
        if (mShown) {
            try {
                mWindowManager.removeView(mView);
                mShown = false;
            } catch (Exception e) {
            }
        }
    }
}
