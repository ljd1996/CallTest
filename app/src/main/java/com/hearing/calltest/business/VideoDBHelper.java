package com.hearing.calltest.business;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.hearing.calltest.provider.CallProvider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author liujiadong
 * @since 2020/1/8
 */
public class VideoDBHelper {

    public static final String UNKNOWN_NUMBER = "unknown";
    private static final String AUTHORITY = "content://com.hearing.calltest.provider";
    private static final Uri VIDEO_URI = Uri.parse(AUTHORITY);


    private VideoDBHelper() {
    }

    private static class SingleTon {
        private static VideoDBHelper sInstance = new VideoDBHelper();
    }

    public static VideoDBHelper getInstance() {
        return SingleTon.sInstance;
    }

    public void setSelectVideo(Context context, String number, String path) {
        ContentValues values = new ContentValues();
        values.put(CallProvider.NUMBER, handleNumber(number));
        values.put(CallProvider.PATH, path);
        if (!TextUtils.isEmpty(queryVideo(context, number))) {
            context.getContentResolver().delete(VIDEO_URI, CallProvider.NUMBER + "=\'" + number + "\'", null);
        }
        context.getContentResolver().insert(VIDEO_URI, values);
    }

    public String getSelectVideo(Context context, String number) {
        String path = queryVideo(context, handleNumber(number));
        if (TextUtils.isEmpty(path)) {
            path = queryVideo(context, UNKNOWN_NUMBER);
        }
        Log.d("LLL", "path = " + path);
        return path;
    }

    private String handleNumber(String number) {
        if (TextUtils.isEmpty(number) || UNKNOWN_NUMBER.equals(number)) {
            return number;
        }
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(number);
        return m.replaceAll("").trim();
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
