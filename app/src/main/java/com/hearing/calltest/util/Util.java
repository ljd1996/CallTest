package com.hearing.calltest.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.hearing.calltest.service.PhoneListenService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * @author liujiadong
 * @since 2020/1/3
 */
public class Util {

    private static final String TAG = "LLL";

    public static void startMainActivity(Context context) {
        try {
            Toast.makeText(context, "service connected", Toast.LENGTH_LONG).show();
            context.startService(new Intent(context, PhoneListenService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 复制asset文件到指定目录
     *
     * @param oldPath asset下的路径
     * @param newPath SD卡下保存路径
     */
    public static void CopyAssets(Context context, String oldPath, String newPath) {
        try {
            String[] fileNames = context.getAssets().list(oldPath);// 获取assets目录下的所有文件及目录名
            if (fileNames.length > 0) {// 如果是目录
                File file = new File(newPath);
                file.mkdirs();// 如果文件夹不存在，则递归
                for (String fileName : fileNames) {
                    CopyAssets(context, oldPath + "/" + fileName, newPath + "/" + fileName);
                }
            } else {// 如果是文件
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount;
                while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取
                    // buffer字节
                    fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
                }
                fos.flush();// 刷新缓冲区
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> getAllName(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        File pathFile = new File(path);
        if (pathFile.exists() && pathFile.isDirectory()) {
            File[] files = pathFile.listFiles();
            if (files == null) {
                return null;
            }
            List<String> result = new ArrayList<>();
            for (File file : files) {
                result.add(file.getName());
            }
            return result;
        }
        return null;
    }

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
        String delete = MediaStore.MediaColumns.DATA + "=\"" + sdFile.getAbsolutePath() + "\"";

        Log.d(TAG, "delete = " + delete);
        Log.d(TAG, "delete = " + context.getContentResolver().delete(uri, delete, null));

        Uri newUri = context.getContentResolver().insert(uri, values);
        Log.d(TAG, "uri = " + uri);
        Log.d(TAG, "new uri = " + newUri);
        RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, newUri);
        Toast.makeText(context, "设置来电铃声成功！", Toast.LENGTH_SHORT).show();
    }

}
