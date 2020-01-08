package com.hearing.calltest.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.hearing.calltest.preference.PreferenceUtil;

/**
 * @author liujiadong
 * @since 2020/1/8
 */
public class VideoRingHelper {

    private static final String VIDEO_RING_SP = "video_ring_sp";
    private static final String SELECT_VIDEO = "select_video";
    private static final String SELECT_RING = "select_ring";


    private VideoRingHelper() {
    }

    private static class SingleTon {
        private static VideoRingHelper sInstance = new VideoRingHelper();
    }

    public static VideoRingHelper getInstance() {
        return SingleTon.sInstance;
    }

    private SharedPreferences getSp(Context context) {
        return PreferenceUtil.getSharedPreference(context, VIDEO_RING_SP);
    }

    private void setString(Context context, String key, String value) {
        SharedPreferences.Editor editor = getSp(context).edit();
        editor.putString(key, value);
        editor.apply();
    }

    private String getString(Context context, String key) {
        return getSp(context).getString(key, "");
    }

    public void setSelectVideo(Context context, String path) {
        setString(context, SELECT_VIDEO, path);
    }

    public void setSelectRing(Context context, String path) {
        setString(context, SELECT_RING, path);
    }

    public String getSelectVideo(Context context) {
        return getString(context, SELECT_VIDEO);
    }

    public String getSelectRing(Context context) {
        return getString(context, SELECT_RING);
    }
}
