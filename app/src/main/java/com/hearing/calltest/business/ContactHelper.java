package com.hearing.calltest.business;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import com.hearing.calltest.R;

/**
 * @author liujiadong
 * @since 2020/1/17
 */
public class ContactHelper {

    private static final String TAG = "LLL";


    private ContactHelper() {
    }

    private static class SingleTon {
        private static ContactHelper sInstance = new ContactHelper();
    }

    public static ContactHelper getInstance() {
        return SingleTon.sInstance;
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
