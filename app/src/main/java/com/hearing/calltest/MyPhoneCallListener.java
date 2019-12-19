package com.hearing.calltest;

import android.telephony.PhoneStateListener;
import android.util.Log;

/**
 * @author liujiadong
 * @since 2019/12/17
 */
public class MyPhoneCallListener extends PhoneStateListener {

    private OnCallStateChanged mListener;

    /**
     * CALL_STATE_IDLE 无任何状态时
     * CALL_STATE_OFFHOOK 接起电话时
     * CALL_STATE_RINGING 电话进来时
     */
    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        Log.d("LLL", "state = " + state + ", incomingNumber = " + incomingNumber);
        if (mListener != null) {
            mListener.onCallStateChanged(state, incomingNumber);
        }
        super.onCallStateChanged(state, incomingNumber);
    }

    public void setListener(OnCallStateChanged listener) {
        this.mListener = listener;
    }

    public interface OnCallStateChanged {
        void onCallStateChanged(int state, String number);
    }
}
