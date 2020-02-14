package com.github.audioplay;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.support.v4.content.ContextCompat;

/**
 * Created by luoxiangcheng on 2020/1/2 16:52
 */

public class AudioPlayManager {

    private Context mContext;
    private static AudioPlayManager manager;
    private SoundPool mSoundPool;
    private boolean isBgMusicClose = false;
    private boolean isBtMusicClose = false;


    private int clickRightSoundId = 0;
    private int clickErrorSoundId = 0;
    private int clickSoundId = 0;

    private AudioPlayManager() {
        super();
    }

    public static AudioPlayManager getInstance() {
        if (manager == null) {
            synchronized (AudioPlayManager.class) {
                if (manager == null) {
                    manager = new AudioPlayManager();
                }
            }
        }
        return manager;
    }

    public void init(Context context) {
        init(context, false, false);
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context, boolean isBgMusicClose, boolean isBtMusicClose) {
        if (context == null) {
            return;
        }
        this.isBgMusicClose = isBgMusicClose;
        this.isBtMusicClose = isBtMusicClose;
        mContext = context.getApplicationContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSoundPool = new SoundPool.Builder().setMaxStreams(1).build();
        } else {
            mSoundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 0);
        }
        clickRightSoundId = mSoundPool.load(mContext, R.raw.click_right, 1);
        clickErrorSoundId = mSoundPool.load(mContext, R.raw.click_error, 1);
        clickSoundId = mSoundPool.load(mContext, R.raw.click, 1);
    }

    /**
     * 播放背景音乐
     */
    public void playBg() {
        if (isBgMusicClose) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sendJobServiceCommand(MusicJobService.TAG_START);
        } else {
            sendServiceCommand(MusicService.TAG_START);
        }
    }

    /**
     * 循环播放背景音乐
     */
    public void loopPlayBg() {
        if (isBgMusicClose) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sendJobServiceCommand(MusicJobService.TAG_LOOP_START);
        } else {
            sendServiceCommand(MusicService.TAG_LOOP_START);
        }
    }

    /**
     * 暂停播放背景音乐
     */
    public void pauseBg() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sendJobServiceCommand(MusicJobService.TAG_PAUSE);
        } else {
            sendServiceCommand(MusicService.TAG_PAUSE);
        }
    }

    /**
     * 继续播放背景音乐
     */
    public void continueBg() {
        if (isBgMusicClose) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sendJobServiceCommand(MusicJobService.TAG_CONTINUE);
        } else {
            sendServiceCommand(MusicService.TAG_CONTINUE);
        }
    }

    /**
     * 停止播放背景音乐
     */
    public void stopBg() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sendJobServiceCommand(MusicJobService.TAG_STOP);
        } else {
            sendServiceCommand(MusicService.TAG_STOP);
        }
    }

    /**
     * 重新播放背景音乐
     */
    public void restartBg() {
        if (isBgMusicClose) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sendJobServiceCommand(MusicJobService.TAG_RESTART);
        } else {
            sendServiceCommand(MusicService.TAG_RESTART);
        }
    }

    /**
     * 播放点击音效
     */
    public void clickSound() {
        if (isBtMusicClose) {
            return;
        }
        playClickSound(clickSoundId);
    }

    /**
     * 播放点击音效
     */
    public void clickRightSound() {
        if (isBtMusicClose) {
            return;
        }
        playClickSound(clickRightSoundId);
    }

    /**
     * 播放点击音效
     */
    public void clickErrorSound() {
        if (isBtMusicClose) {
            return;
        }
        playClickSound(clickErrorSoundId);
    }

    private void playClickSound(int soundId) {
        if (mSoundPool == null || soundId == 0) {
            return;
        }
        try {
            mSoundPool.play(soundId, 1, 1, 0, 0, 1);
        } catch (Throwable t) {
        }
    }

    /**
     * 给MusicJobService发送命令
     *
     * @param command
     */
    private void sendJobServiceCommand(int command) {
        if (mContext == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Intent intent = new Intent(mContext, MusicJobService.class);
            intent.putExtra(MusicJobService.TAG_MUSIC, command);
            mContext.startService(intent);
        }
    }

    /**
     * 给MusicService发送命令
     *
     * @param command
     */
    private void sendServiceCommand(int command) {
        if (mContext == null) {
            return;
        }
        Intent intent = new Intent(mContext, MusicService.class);
        intent.putExtra(MusicService.TAG_MUSIC, command);
        ContextCompat.startForegroundService(mContext, intent);
    }
}
