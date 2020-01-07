package com.hearing.calltest.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.hearing.calltest.R;

import java.io.File;

/**
 * @author liujiadong
 * @since 2020/1/7
 */
public class PlayerDialog extends AlertDialog {

    private Context mContext;
    private OnPlayerSelectListener mListener;
    private String mSource;
    private SimpleExoPlayer mPlayer;

    public PlayerDialog(Context context, String source) {
        super(context);
        mContext = context;
        mSource = source;
    }

    public void setListener(OnPlayerSelectListener listener) {
        this.mListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_player, null);
        setContentView(dialogView);

        dialogView.findViewById(R.id.confirm).setOnClickListener(v -> {
            dismiss();
            if (mListener != null) {
                mListener.onConfirm();
            }
        });

        dialogView.findViewById(R.id.cancel).setOnClickListener(v -> {
            dismiss();
        });

        mPlayer = new SimpleExoPlayer.Builder(mContext).build();
        PlayerView playerView = dialogView.findViewById(R.id.player_view);
        playerView.setPlayer(mPlayer);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext,
                Util.getUserAgent(mContext, mContext.getPackageName()));
        MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.fromFile(new File(mSource)));
        mPlayer.prepare(videoSource);

        Window window = getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawable(new BitmapDrawable());
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
        }
    }

    @Override
    public void show() {
        super.show();
        mPlayer.setPlayWhenReady(true);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mPlayer.setPlayWhenReady(false);
        mPlayer.release();
    }

    public interface OnPlayerSelectListener {

        void onConfirm();
    }
}
