package com.hearing.calltest.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;


/**
 * @author liujiadong
 * @since 2020/01/08
 */
public class LockSlidingView extends SlidingFinishView {

    private static final String TAG = "LockSlidingView";

    private GestureDetector mGestureDetector;
    private OnSingleTapListener mOnSingleTapListener;

    public LockSlidingView(Context context) {
        super(context);
    }

    public LockSlidingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LockSlidingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void init(Context context) {
        super.init(context);
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Log.d(TAG, "onSingleTapConfirmed()");
                if (mOnSingleTapListener != null) {
                    mOnSingleTapListener.onSingleTapConfirmed(e);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    public void setOnSingleTapListener(OnSingleTapListener onSingleTapListener) {
        this.mOnSingleTapListener = onSingleTapListener;
    }

    public interface OnSingleTapListener {
        void onSingleTapConfirmed(MotionEvent e);
    }
}
