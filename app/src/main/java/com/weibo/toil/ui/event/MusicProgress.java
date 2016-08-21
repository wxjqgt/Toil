package com.weibo.toil.ui.event;

/**
 * Created by Administrator on 2016/7/18.
 */
public class MusicProgress {
    private long musicPlayUpdateProgress;
    private String musicPlayUpdateProgressText;

    public MusicProgress(){}

    public String getMusicPlayUpdateProgressText() {
        return musicPlayUpdateProgressText;
    }

    public void setMusicPlayUpdateProgressText(String musicPlayUpdateProgressText) {
        this.musicPlayUpdateProgressText = musicPlayUpdateProgressText;
    }

    public long getMusicPlayUpdateProgress() {
        return musicPlayUpdateProgress;
    }

    public void setMusicPlayUpdateProgress(long musicPlayUpdateProgress) {
        this.musicPlayUpdateProgress = musicPlayUpdateProgress;
    }
}
