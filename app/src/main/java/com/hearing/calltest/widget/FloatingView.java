package com.hearing.calltest.widget;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hearing.calltest.FloatingManager;
import com.hearing.calltest.R;

/**
 * @author liujiadong
 * @since 2019/12/17
 */
public class FloatingView extends FrameLayout {
    private View mView;
    private FloatingManager mWindowManager;
    private OnCallListener mListener;
    private boolean mShown = false;

    public FloatingView(Context context) {
        super(context);
        mView = LayoutInflater.from(context).inflate(R.layout.floating_view, null);
        mView.findViewById(R.id.get_call).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
                if (mListener != null) {
                    mListener.onGet();
                }
            }
        });
        mView.findViewById(R.id.end_call).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
                if (mListener != null) {
                    mListener.onEnd();
                }
            }
        });
        mWindowManager = FloatingManager.getInstance(context);
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
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.MATCH_PARENT;
        mWindowManager.addView(mView, params);
        mShown = true;
    }

    public void hide() {
        if (mShown) {
            mWindowManager.removeView(mView);
            mShown = false;
        }
    }

    public interface OnCallListener {
        void onGet();

        void onEnd();
    }
}
