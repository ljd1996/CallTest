package com.hearing.calltest.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;


/**
 * @author liujiadong
 * @since 2020/01/08
 */
public class SlidingFinishView extends RelativeLayout {
    private static final String TAG = "SlidingFinishView";
    private VelocityTracker mVelocityTracker;
    private float mDownY;
    private static final int MAX_VELOCITY_DP = 150;
    private static final int MIN_FINISH_DIS_DP = 80;
    private View mMoveView;
    private int mScreenHeight;
    private Listener mListener;
    private boolean mIsAnimation;
    private float mDensity;
    private int mMaxVelocityPx;
    private int mTouchSlop;

    public SlidingFinishView(Context context) {
        super(context);
        init(context);
    }

    public SlidingFinishView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SlidingFinishView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    protected void init(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        mScreenHeight = displayMetrics.heightPixels;
        mDensity = displayMetrics.density;
        mMaxVelocityPx = (int) (MAX_VELOCITY_DP * mDensity);
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        mTouchSlop = viewConfiguration.getScaledTouchSlop();
    }

    public void setMoveView(View moveView) {
        mMoveView = moveView;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mIsAnimation) {
            return super.onTouchEvent(event);
        }
        final float y = event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                } else {
                    mVelocityTracker.clear();
                }
                mVelocityTracker.addMovement(event);
                mDownY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mVelocityTracker != null) {
                    mVelocityTracker.addMovement(event);
                }
                handleMoveView(y);
                break;
            case MotionEvent.ACTION_UP:
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.addMovement(event);
                velocityTracker.computeCurrentVelocity(1000);
                int velocityY = (int) velocityTracker.getYVelocity();
                velocityTracker.clear();
                doTriggerEvent(y, velocityY);
                break;
        }
        return true;
    }

    private void handleMoveView(float y) {
        float disY = y - mDownY;
        if (disY > 0) {
            disY = 0;
        }
        Log.d(TAG, "handleMoveView--disY=" + disY);
        getMoveView().setTranslationY(disY);
    }

    private View getMoveView() {
        if (mMoveView == null) {
            return this;
        }
        return mMoveView;
    }

    private void doTriggerEvent(float y, int velocityY) {
        float moveY = y - mDownY;//moveY小于0，表示向上滑动
        Log.d(TAG, "doTriggerEvent--velocityY=" + velocityY + "|moveY=" + moveY);
        if (moveY >= 0 || Math.abs(moveY) < mTouchSlop) {
            Log.d(TAG, "doTriggerEvent--moveY >= 0 || Math.abs(moveY) < mTouchSlop");
            return;
        }
        if (-velocityY > mMaxVelocityPx) {
            moveView(0, true);
        } else if (-moveY > MIN_FINISH_DIS_DP * mDensity) {
            moveView(0, true);
        } else {
            moveView(0, false);
        }
    }

    public void setVisible() {
        getMoveView().setVisibility(VISIBLE);
    }

    private void moveView(float to, final boolean exit) {
        mIsAnimation = true;
        if (exit) {
            getMoveView().setVisibility(INVISIBLE);
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(getMoveView(), View.TRANSLATION_Y, to);
        int duration = Math.min((int) Math.abs(getMoveView().getTranslationY() - to) / 2, 250);
        Log.d(TAG, "moveView--duration=" + duration);
        animator.setDuration(duration).start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mIsAnimation = false;
                if (exit) {
                    if (mVelocityTracker != null) {
                        mVelocityTracker.recycle();
                        mVelocityTracker = null;
                    }
                    if (mListener != null) {
                        mListener.onSlidingOut();
                    }
                }
            }
        });
    }

    public interface Listener {
        void onSlidingOut();
    }
}
