package com.weibo.toil.bean.music;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2015/11/30.
 */
public class Mp3Info implements Parcelable {

    private long id;
    private long albumId;
    private long duration;
    private long size;
    private int isMusic;
    private String url;
    private String title;
    private String artist;
    private String album;
    private String display;

    public Mp3Info() {
    }

    public Mp3Info(String display, long id, long albumId, long duration, long size, int isMusic, String url, String title, String artist, String album) {
        this.id = id;
        this.albumId = albumId;
        this.duration = duration;
        this.size = size;
        this.isMusic = isMusic;
        this.url = url;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.display = display;
    }

    protected Mp3Info(Parcel in) {
        id = in.readLong();
        albumId = in.readLong();
        duration = in.readLong();
        size = in.readLong();
        isMusic = in.readInt();
        url = in.readString();
        title = in.readString();
        artist = in.readString();
        album = in.readString();
        display = in.readString();
    }

    public static final Creator<Mp3Info> CREATOR = new Creator<Mp3Info>() {
        @Override
        public Mp3Info createFromParcel(Parcel in) {
            return new Mp3Info(in);
        }

        @Override
        public Mp3Info[] newArray(int size) {
            return new Mp3Info[size];
        }
    };

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getIsMusic() {
        return isMusic;
    }

    public void setIsMusic(int isMusic) {
        this.isMusic = isMusic;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    @Override
    public String toString() {
        return "Mp3Info{" +
                "id=" + id +
                ", albumId=" + albumId +
                ", duration=" + duration +
                ", size=" + size +
                ", isMusic=" + isMusic +
                ", url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", display='" + display + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(albumId);
        dest.writeLong(duration);
        dest.writeLong(size);
        dest.writeInt(isMusic);
        dest.writeString(url);
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeString(album);
        dest.writeString(display);
    }
}
