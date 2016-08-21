package com.weibo.toil.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.weibo.toil.api.music.LrcApi;
import com.weibo.toil.bean.music.Mp3Info;
import com.weibo.toil.ui.event.MusicLrc;

import org.greenrobot.eventbus.EventBus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2015/11/30.
 */
public class MediaUtils {

    //获取专辑封面的Uri
    public static final Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
    public static final String url_search_lrc = "http://www.cnlyric.com/search.php?k=";
    public static final String url_home = "http://www.cnlyric.com/";

    //查询所有歌曲信息
    public static ArrayList<Mp3Info> getMp3InfoList(Context context) {
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                MediaStore.Audio.Media.DURATION + ">=" + "180000",
                null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        );
        ArrayList<Mp3Info> list = new ArrayList<>();
        int id = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
        int album = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
        int albumId = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
        int artList = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
        int duration = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
        int size = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
        int title = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
        int url = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        int display = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
        while (cursor.moveToNext()) {
            Mp3Info mp3Info = new Mp3Info();
            mp3Info.setId(cursor.getLong(id));
            mp3Info.setAlbum(cursor.getString(album));
            mp3Info.setAlbumId(cursor.getInt(albumId));
            mp3Info.setArtist(cursor.getString(artList));
            mp3Info.setDuration(cursor.getInt(duration));
            mp3Info.setSize(cursor.getLong(size));
            mp3Info.setTitle(cursor.getString(title));
            mp3Info.setUrl(cursor.getString(url));
            mp3Info.setDisplay(cursor.getString(display));
            list.add(mp3Info);
        }
        cursor.close();
        return list;
    }

    //格式化时间
    public static String formattime(long time) {
        String min = time / (1000 * 60) + "";
        String sec = time % (1000 * 60) + "";
        if (min.length() < 2) {
            min = "0" + time / (1000 * 60) + "";
        } else {
            min = time / (1000 * 60) + "";
        }
        if (sec.length() == 4) {
            sec = "0" + time % (1000 * 60) + "";
        } else if (sec.length() == 3) {
            sec = "00" + time % (1000 * 60) + "";
        } else if (sec.length() == 2) {
            sec = "000" + time % (1000 * 60) + "";
        } else if (sec.length() == 1) {
            sec = "0000" + time % (1000 * 60) + "";
        }
        return min + ":" + sec.trim().substring(0, 2);
    }

    public static final String COVER_DIRECTORY = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) + "/Cover/";

    public static File getCover(String artist,String title){
        String filename = artist + " - " + title + ".jpg";
        File file_lrc = new File(COVER_DIRECTORY);

        String[] lrcs = file_lrc.list();
        if (lrcs == null) {
            return null;
        } else {
            for (String lrc : lrcs) {
                if (!TextUtils.isEmpty(lrc) && lrc.equals(filename)) {
                    return new File(COVER_DIRECTORY + filename);
                }
            }
            return null;
        }
    }

    public static Uri getArtWorkUri(Long song_id, long abum_id) {
        if (abum_id < 0) {
            if (song_id < 0) {
                Uri uri = getArtWorkFileUri(song_id, abum_id);
                if (uri != null) {
                    return uri;
                }
            }
            return null;
        }
        Uri uri = ContentUris.withAppendedId(albumArtUri, abum_id);
        if (uri != null) {
            try {
                return uri;
            } catch (Exception e) {
                Uri uri1 = getArtWorkFileUri(song_id, abum_id);
                if (uri1 != null) {
                    return uri1;
                }
                return uri;
            }
        }
        return null;
    }

    private static Uri getArtWorkFileUri(long song_id, long album_id) {
        Uri uri = null;
        if (album_id < 0 && song_id < 0) {
            throw new IllegalArgumentException("Must specify an album or a song_id");
        }
        try {
            if (album_id < 0) {
                uri = Uri.parse("content://media/external/audio/media/" + song_id + "/albumart");
            } else {
                uri = ContentUris.withAppendedId(albumArtUri, album_id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uri;
    }

    public static final String LRC_DIRECTORY = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) + "/Lyric/";


    public static String getLrcFromLocal(String artist, String title) {

        String filename = artist + " - " + title + ".lrc";
        File file_lrc = new File(LRC_DIRECTORY);

        String[] lrcs = file_lrc.list();
        if (lrcs == null) {
            return null;
        } else {
            for (String lrc : lrcs) {
                if (lrc.equals(filename)) {
                    return LRC_DIRECTORY + filename;
                }
            }
            return null;
        }
    }

    //获得歌词的下载链接
    public static String getLrcUrl(String artist, String title) {
        try {
            String eartist = URLEncoder.encode(artist, "gbk");
            String etitle = URLEncoder.encode(title, "gbk");

            String full_url = url_search_lrc + eartist + "+" + etitle + "&t=s";
            Document doc = Jsoup.parse(new URL(full_url), 5000);
            Elements ele2 = doc.select("a.ld");

            if (ele2.size() > 0) {
                return url_home + ele2.get(0).attr("href");
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //下载歌词
    public static MusicLrc downFile(String artist,String title) {
        String url = getLrcUrl(artist,title);
        if (TextUtils.isEmpty(url)){return null;}
        final String path = LRC_DIRECTORY + artist + " - " + title + ".lrc";
        final MusicLrc musicLrc = new MusicLrc();
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        final Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                musicLrc.setSuccess(false);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                FileOutputStream fos = null;
                InputStream is = response.body().byteStream();
                try {
                    musicLrc.setLrc(response.body().string());
                    byte[] bytes = new byte[1024];
                    int len;
                    File file = new File(path);
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    fos = new FileOutputStream(file);
                    while ((len = is.read(bytes)) != -1) {
                        fos.write(bytes, 0, len);
                    }
                    musicLrc.setSuccess(true);
                    musicLrc.setPath(path);
                } catch (Exception e) {
                    e.printStackTrace();
                    musicLrc.setSuccess(false);
                } finally {
                    EventBus.getDefault().post(musicLrc);
                    try {
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return musicLrc;
    }
}

























