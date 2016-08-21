package com.weibo.toil.ui.event;

import android.net.Uri;

import java.io.File;

/**
 * Created by 巴巴 on 2016/8/20.
 */
public class MusicCover {

    private File file;
    private Uri uri;

    @Override
    public String toString() {
        return "MusicCover{" +
                "file=" + file +
                ", uri=" + uri +
                '}';
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public MusicCover() {
    }

    public MusicCover(File file, Uri uri) {
        this.file = file;
        this.uri = uri;
    }
}
