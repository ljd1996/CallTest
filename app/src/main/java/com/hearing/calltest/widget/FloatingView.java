package com.hearing.calltest.widget;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.hearing.calltest.FloatingManager;
import com.hearing.calltest.R;
import com.hearing.calltest.util.VideoRingHelper;

/**
 * @author liujiadong
 * @since 2019/12/17
 */
public class FloatingView extends FrameLayout {
    private Context mContext;
    private View mView;
    private VideoView mVideoView;
    private FloatingManager mWindowManager;
    private OnCallListener mListener;
    private boolean mShown = false;

    public FloatingView(Context context) {
        super(context);
        mContext = context;

        mView = LayoutInflater.from(context).inflate(R.layout.floating_view, null);

        mView.findViewById(R.id.get_call).setOnClickListener(v -> {
            hide();
            if (mListener != null) {
                mListener.onGet();
            }
        });
        mView.findViewById(R.id.end_call).setOnClickListener(v -> {
            hide();
            if (mListener != null) {
                mListener.onEnd();
            }
        });

        mWindowManager = FloatingManager.getInstance(context);
        mVideoView = mView.findViewById(R.id.video_view);
    }

    public void setPerson(String name, String number) {
        if (!TextUtils.isEmpty(name)) {
            ((TextView) mView.findViewById(R.id.name_tv)).setText(name);
        }
        if (!TextUtils.isEmpty(number)) {
            ((TextView) mView.findViewById(R.id.number_tv)).setText(number);
        }
    }

    public void setListener(OnCallListener listener) {
        this.mListener = listener;
    }

    public void show() {
        mVideoView.setVideoPath(VideoRingHelper.getInstance().getSelectVideo(mContext));

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
        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.MATCH_PARENT;
        mWindowManager.addView(mView, params);
        mShown = true;

        mVideoView.start();
    }

    public void hide() {
        if (mShown) {
            mWindowManager.removeView(mView);
            mShown = false;
            mVideoView.pause();
        }
    }

    public interface OnCallListener {
        void onGet();

        void onEnd();
    }
}
