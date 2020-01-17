package com.hearing.calltest.business;

import android.Manifest;
import android.app.Notification;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;

import com.hearing.calltest.R;

/**
 * @author liujiadong
 * @since 2020/1/17
 */
public class ContactHelper {

    private static final String TAG = "LLL";

    private String mTitle;


    private ContactHelper() {
    }

    private static class SingleTon {
        private static ContactHelper sInstance = new ContactHelper();
    }

    public static ContactHelper getInstance() {
        return SingleTon.sInstance;
    }


    /**
     * 从通知栏读取来电信息
     *
     * @param sbn
     */
    public void setContact(StatusBarNotification sbn) {
        if (isCall(sbn)) {
            Bundle bundle = sbn.getNotification().extras;
            if (bundle != null) {
                mTitle = String.valueOf(bundle.getCharSequence(Notification.EXTRA_TITLE));
            }
        }
    }

    public void clearContact(StatusBarNotification sbn) {
        if (isCall(sbn)) {
            mTitle = null;
        }
    }

    private boolean isCall(StatusBarNotification sbn) {
        return sbn != null && sbn.getNotification() != null &&
                "com.google.android.dialer".equals(sbn.getPackageName())
                && sbn.getNotification().actions != null;
    }

    /**
     * 根据uri查找电话号码
     *
     * @param context
     * @param contactUri
     * @return
     */
    public String getContacts(Context context, Uri contactUri) {
        String phoneNumber = "";
        if (context == null || contactUri == null) {
            return "";
        }
        Cursor cursor = context.getContentResolver().query(contactUri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                            + "=" + id, null, null);
            if (phones != null && phones.moveToNext()) {
                phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phones.close();
            }
            cursor.close();
        }
        Log.d(TAG, "number = " + phoneNumber);
        return phoneNumber;
    }

    /**
     * 根据号码查找联系人姓名
     *
     * @param context
     * @param number
     * @return
     */
    public String getContactName(Context context, String number) {
        if (context == null) {
            return null;
        }

        if (context.checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED
                || context.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            Log.d("LLL", "mTitle = " + mTitle);
            return mTitle;
        }

        if (TextUtils.isEmpty(number)) {
            return null;
        }

        final ContentResolver resolver = context.getContentResolver();

        Uri lookupUri;
        String[] projection = new String[]{ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.DISPLAY_NAME};
        Cursor cursor = null;
        try {
            lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
            cursor = resolver.query(lookupUri, projection, null, null, null);
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                lookupUri = Uri.withAppendedPath(android.provider.Contacts.Phones.CONTENT_FILTER_URL,
                        Uri.encode(number));
                cursor = resolver.query(lookupUri, projection, null, null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String ret = context.getResources().getString(R.string.unknown_contract);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            ret = cursor.getString(1);
            cursor.close();
        }
        return ret;
    }
}
