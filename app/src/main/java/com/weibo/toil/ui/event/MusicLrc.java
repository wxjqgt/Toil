package com.weibo.toil.ui.event;

/**
 * Created by Administrator on 2016/7/18.
 */
public class MusicLrc {

    //歌词
    private String lrc;
    //歌词路径
    private String path;
    //是否下载成功
    private boolean isSuccess;

    public MusicLrc() {
    }

    public MusicLrc(String lrc, String path, boolean isSuccess) {
        this.lrc = lrc;
        this.path = path;
        this.isSuccess = isSuccess;
    }
    public MusicLrc(String path, boolean isSuccess) {
        this.path = path;
        this.isSuccess = isSuccess;
    }
    @Override
    public String toString() {
        return "MusicLrc{" +
                "lrc='" + lrc + '\'' +
                ", path='" + path + '\'' +
                ", isSuccess=" + isSuccess +
                '}';
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getLrc() {
        return lrc;
    }

    public void setLrc(String lrc) {
        this.lrc = lrc;
    }
}
