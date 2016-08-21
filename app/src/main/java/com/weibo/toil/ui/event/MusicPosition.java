package com.weibo.toil.ui.event;

import android.net.Uri;

import com.weibo.toil.bean.music.Mp3Info;

/**
 * Created by Administrator on 2016/7/18.
 */
public class MusicPosition {

    private int musicPlayUpdatePosition;
    private Mp3Info mp3Info;

    public Mp3Info getMp3Info() {
        return mp3Info;
    }

    public void setMp3Info(Mp3Info mp3Info) {
        this.mp3Info = mp3Info;
    }

    public MusicPosition(int musicPlayUpdatePosition, Mp3Info mp3Info) {

        this.musicPlayUpdatePosition = musicPlayUpdatePosition;
        this.mp3Info = mp3Info;
    }

    public MusicPosition() {}

    public int getMusicPlayUpdatePosition() {
        return musicPlayUpdatePosition;
    }

    public void setMusicPlayUpdatePosition(int musicPlayUpdatePosition) {
        this.musicPlayUpdatePosition = musicPlayUpdatePosition;
    }
}
