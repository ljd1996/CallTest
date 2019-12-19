package com.hearing.calltest.service;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.telecom.Call;
import android.telecom.CallAudioState;
import android.telecom.InCallService;
import android.telecom.VideoProfile;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.hearing.calltest.widget.FloatingView;


/**
 * @author liujiadong
 * @since 2019/12/19
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class CallService extends InCallService {

    public static final String TAG = "LLL";

    private Call mCall;
    private FloatingView mFloatingView;


    @Override
    public void onCreate() {
        super.onCreate();

        mFloatingView = new FloatingView(this);
        mFloatingView.setListener(new FloatingView.OnCallListener() {
            @Override
            public void onGet() {
                mFloatingView.hide();
                if (mCall != null) {
                    mCall.answer(VideoProfile.STATE_AUDIO_ONLY);
                    gotoDialog();
                }
            }

            @Override
            public void onEnd() {
                mFloatingView.hide();
                if (mCall != null) {
                    mCall.disconnect();
                }
            }
        });
    }

    public CallService() {
        super();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return super.onBind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onCallAudioStateChanged(CallAudioState audioState) {
        Log.d(TAG, "onCallAudioStateChanged");
        super.onCallAudioStateChanged(audioState);
    }

    @Override
    public void onCallRemoved(Call call) {
        Log.d(TAG, "onCallRemoved");
        super.onCallRemoved(call);
    }

    @Override
    public void onCallAdded(Call call) {
        Log.d(TAG, "state = " + call.getState());
        mCall = call;
        switch (call.getState()) {
            case Call.STATE_RINGING:
                mFloatingView.show();
                break;
            default:
                mCall.answer(VideoProfile.STATE_AUDIO_ONLY);
                gotoDialog();
                break;
        }
    }

    private void gotoDialog() {
        Intent intent = new Intent(Intent.ACTION_CALL, null);
        if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
