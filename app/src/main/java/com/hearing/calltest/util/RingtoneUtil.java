package com.hearing.calltest.util;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author liujiadong
 * @since 2020/1/14
 */
public class RingtoneUtil {

    public static void playRingtone(Context context, Uri uri) {
        RingtoneManager manager = new RingtoneManager(context);
        manager.stopPreviousRingtone();
        RingtoneManager.getValidRingtoneUri(context);

        try {
            Ringtone ringtone = null;
            Class cls = Ringtone.class;
            Constructor[] constructors = cls.getDeclaredConstructors();
            for (Constructor constructor : constructors) {
                if (constructor.getModifiers() == Modifier.PUBLIC) {
                    ringtone = (Ringtone) constructor.newInstance(context, true);
                }
            }
            if (ringtone != null) {
                Method method = cls.getDeclaredMethod("setUri", Uri.class);
                method.invoke(ringtone, uri);
                ringtone.play();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
