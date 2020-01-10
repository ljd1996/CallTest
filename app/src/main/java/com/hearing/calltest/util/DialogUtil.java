package com.hearing.calltest.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * @author liujiadong
 * @since 2020/1/10
 */
public class DialogUtil {

    public static void showDialog(Context context, String msg, String pos, String neg,
                                  DialogInterface.OnClickListener posListener, DialogInterface.OnClickListener negListener) {
        new AlertDialog.Builder(context)
                .setMessage(msg)
                .setPositiveButton(pos, posListener)
                .setNegativeButton(neg, negListener)
                .create()
                .show();
    }
}
