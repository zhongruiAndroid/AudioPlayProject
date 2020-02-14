package com.github.audioplay;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * Created by luoxiangcheng on 2019/12/31 10:55
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MusicJobService extends JobService {

    public static final String TAG_MUSIC = "tag_music";
    public static final int TAG_START = 1001; // 开始播放
    public static final int TAG_PAUSE = 1002; // 暂停播放
    public static final int TAG_CONTINUE = 1003; // 继续播放
    public static final int TAG_STOP = 1004; // 停止播放
    public static final int TAG_RESTART = 1005; // 重新开始播放
    public static final int TAG_LOOP_START = 1006; // 循环播放

    private Context mContext;
    private MediaPlayer mPlayer;
    private int mBgResId;
    private boolean mIsLoopStart = false; // 是否是循环播放

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        mBgResId = R.raw.bg;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }

        initMediaPlayer();
        int tag = intent.getIntExtra(TAG_MUSIC, 0);
        switch (tag) {
            case TAG_START:
                mIsLoopStart = false;
                doPlayRecord();
                break;

            case TAG_LOOP_START:
                mIsLoopStart = true;
                doPlayRecord();
                break;

            case TAG_PAUSE:
                if (mPlayer != null && mPlayer.isPlaying()) {
                    mPlayer.pause();
                }
                break;

            case TAG_CONTINUE:
                if (mPlayer != null && !mPlayer.isPlaying()) {
                    mPlayer.start();
                    break;
                }
                break;

            case TAG_STOP:
                stopPlayer();
                break;

            case TAG_RESTART:
                stopPlayer();
                initMediaPlayer();
                doPlayRecord();
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    @Override
    public void onDestroy() {
        stopPlayer();
        super.onDestroy();
    }

    private void initMediaPlayer() {
        if (mPlayer == null) {
            mPlayer = MediaPlayer.create(mContext, mBgResId);
            mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    stopPlayer();
                    return false;
                }
            });
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (mIsLoopStart) {
                        doPlayRecord();
                    } else {
                        stopPlayer();
                    }
                }
            });
        }
    }

    private void doPlayRecord() {
        try {
            mPlayer.setLooping(false); // 设置是否循环播放，设置为true也不生效
            mPlayer.start();
        } catch (Exception e) {
            stopPlayer();
        }
    }

    /**
     * 停止播放并释放MediaPlayer资源
     */
    public void stopPlayer() {
        if (mPlayer == null) {
            return;
        }
        try {
            mPlayer.setOnCompletionListener(null);
            mPlayer.setOnErrorListener(null);
            mPlayer.stop();
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;
            mIsLoopStart = false;
        } catch (Throwable t) {
        }
    }
}
