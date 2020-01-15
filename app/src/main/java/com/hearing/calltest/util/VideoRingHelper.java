package com.hearing.calltest.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.hearing.calltest.preference.PreferenceUtil;
import com.hearing.calltest.provider.CallProvider;

/**
 * @author liujiadong
 * @since 2020/1/8
 */
public class VideoRingHelper {

    private static final String VIDEO_RING_SP = "video_ring_sp";
    private static final String SELECT_VIDEO = "select_video";
    private static final String SELECT_RING = "select_ring";

    public static final String UNKNOWN_NUMBER = "unknown";
    private static final String AUTHORITY = "content://com.hearing.calltest.provider";
    private static final Uri VIDEO_URI = Uri.parse(AUTHORITY);


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

    public void setSelectVideo(Context context, String number, String path) {
        ContentValues values = new ContentValues();
        values.put(CallProvider.NUMBER, number.replace(" ", ""));
        values.put(CallProvider.PATH, path);
        context.getContentResolver().insert(VIDEO_URI, values);
    }

    public String getSelectVideo(Context context, String number) {
        String path = queryVideo(context, number);
        if (TextUtils.isEmpty(path)) {
            path = queryVideo(context, UNKNOWN_NUMBER);
        }
        Log.d("LLL", "path = " + path);
        return path;
    }

    private String queryVideo(Context context, String number) {
        Cursor cursor = context.getContentResolver().query(VIDEO_URI, null, CallProvider.NUMBER + "=\'" + number + "\'",
                null, null);
        String path = "";
        if (cursor != null && cursor.moveToFirst()) {
            path = cursor.getString(cursor.getColumnIndex(CallProvider.PATH));
            cursor.close();
        }
        return path;
    }
}
