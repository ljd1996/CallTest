package com.hearing.calltest.main;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hearing.calltest.R;
import com.hearing.calltest.detail.DetailActivity;
import com.hearing.calltest.util.Util;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private MyAdapter mVideoAdapter;

    private String mVideoPath;

    private Handler mHandle = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {

        try {
            mVideoPath = getExternalFilesDir(null).getAbsolutePath() + "/video";
        } catch (Exception e) {
            mVideoPath = getFilesDir().getAbsolutePath() + "/video";
        }

        RecyclerView videoRecycleView = findViewById(R.id.video_rv);
        videoRecycleView.setLayoutManager(new LinearLayoutManager(this));

        mVideoAdapter = new MyAdapter();
        videoRecycleView.setAdapter(mVideoAdapter);

        mVideoAdapter.setOnItemClickListener((index) -> {
            String path = mVideoPath + "/" + mVideoAdapter.getData(index);
            DetailActivity.startSelf(this, path);
        });

        copyData("video", mVideoPath, list -> {
            if (list != null) {
                mHandle.post(() -> mVideoAdapter.setData(list));
            }
        });
    }

    private void copyData(final String assetPath, final String filePath, final OnDataLoadListener listener) {
        new Thread(() -> {
            Util.CopyAssets(MainActivity.this, assetPath, filePath);
            if (listener != null) {
                listener.onLoadFinish(Util.getAllName(filePath));
            }
        }).start();
    }

    interface OnDataLoadListener {
        void onLoadFinish(List<String> list);
    }
}
