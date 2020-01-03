package com.hearing.calltest.util;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.hearing.calltest.MainActivity;

/**
 * @author liujiadong
 * @since 2020/1/3
 */
public class Util {

    public static void startMainActivity(Context context) {
        try {
            Toast.makeText(context, "service connected", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
