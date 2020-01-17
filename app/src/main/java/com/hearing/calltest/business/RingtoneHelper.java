package com.hearing.calltest.business;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author liujiadong
 * @since 2020/1/14
 */
public class RingtoneHelper {

    private static final String TAG = "LLL";
    public static final String NO_PATH = "/";


    /**
     * 通过Ringtone类播放铃声
     *
     * @param context
     * @param uri
     */
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

    /**
     * 设置铃声为null
     *
     * @param context
     */
    public static void setNoRing(Context context) {
        if (context == null) {
            return;
        }
        RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, null);
        Toast.makeText(context, "已清除铃声！", Toast.LENGTH_SHORT).show();
    }

    /**
     * 设置系统铃声
     *
     * @param context
     * @param path
     */
    public static void setRing(Context context, String path) {
        if (context == null || TextUtils.isEmpty(path)) {
            return;
        }
        File sdFile = new File(path);
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, sdFile.getAbsolutePath());
        values.put(MediaStore.MediaColumns.TITLE, sdFile.getName());
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
        values.put(MediaStore.Audio.Media.IS_ALARM, false);
        values.put(MediaStore.Audio.Media.IS_MUSIC, false);

        Uri uri = MediaStore.Audio.Media.getContentUriForPath(sdFile.getAbsolutePath());
        if (uri == null) {
            Log.d(TAG, "uri is null");
            return;
        }

        Uri newUri;
        String query = MediaStore.MediaColumns.DATA + "=\"" + sdFile.getAbsolutePath() + "\"";

        Cursor cursor = context.getContentResolver().query(uri, null, query, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String id = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                newUri = Uri.parse(uri.toString() + "/" + id);
            } else {
                newUri = context.getContentResolver().insert(uri, values);
            }
            cursor.close();
        } else {
            newUri = context.getContentResolver().insert(uri, values);
        }

        Log.d(TAG, "uri = " + uri);
        Log.d(TAG, "new uri = " + newUri);

        RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, newUri);
        Toast.makeText(context, "设置来电铃声成功！", Toast.LENGTH_SHORT).show();
    }

    /**
     * 给不同联系人设置铃声
     *
     * @param context
     * @param path
     * @param number
     */
    public static void setRing(Context context, String path, String number) {
        final Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, number);
        final String[] projection = new String[]{
                ContactsContract.Contacts._ID, ContactsContract.Contacts.LOOKUP_KEY
        };
        final Cursor data = context.getContentResolver().query(lookupUri, projection, null, null, null);
        try {
            if (data != null && data.moveToFirst()) {
                final long contactId = data.getLong(0);
                final String lookupKey = data.getString(1);
                final Uri contactUri = ContactsContract.Contacts.getLookupUri(contactId, lookupKey);
                if (contactUri == null) {
                    Log.d(TAG, "Invalid arguments");
                    return;
                }

                final File file = new File(path);
                final String value = Uri.fromFile(file).toString();

                Log.d(TAG, "uri = " + contactUri);
                Log.d(TAG, "value = " + value);

                final ContentValues values = new ContentValues(1);
                values.put(ContactsContract.Contacts.CUSTOM_RINGTONE, value);
                context.getContentResolver().update(contactUri, values, null, null);
                Toast.makeText(context, "设置联系人铃声成功！", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                data.close();
            }
        }
    }
}
