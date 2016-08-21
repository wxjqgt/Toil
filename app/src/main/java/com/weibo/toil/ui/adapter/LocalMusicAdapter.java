package com.weibo.toil.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.weibo.toil.ui.event.MusicCover;
import com.weibo.toil.utils.DLog;
import com.weibo.toil.utils.MainApplication;
import com.weibo.toil.R;
import com.weibo.toil.bean.music.Mp3Info;
import com.weibo.toil.ui.view.recyclerView.CommonAdapter;
import com.weibo.toil.ui.view.recyclerView.ViewHolder;
import com.weibo.toil.utils.MediaUtils;

import java.io.File;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by 巴巴 on 2016/8/19.
 */
public class LocalMusicAdapter extends CommonAdapter<Mp3Info> {

    public LocalMusicAdapter(Context context, int LayoutId, List<Mp3Info> datas) {
        super(context, LayoutId, datas);
    }

    @Override
    public void convert(Context context, final ViewHolder holder, Mp3Info mp3Info, int position) {

        holder.setText(R.id.songname, mp3Info.getTitle());
        holder.setText(R.id.singer, mp3Info.getArtist());

        Subscription subscription = Observable.just(mp3Info.getDuration())
                .map(new Func1<Long, String>() {
                         @Override
                         public String call(Long aLong) {
                             return MediaUtils.formattime(aLong);
                         }
                     }
                ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        holder.setText(R.id.duration, s);
                    }
                });
        Subscription subscription1 = Observable.just(mp3Info)
                .map(new Func1<Mp3Info, MusicCover>() {
                         @Override
                         public MusicCover call(Mp3Info m) {
                             MusicCover mCover = new MusicCover();
                             File file = MediaUtils.getCover(m.getArtist(), m.getTitle());
                             mCover.setFile(file);
                             if (file == null) {
                                 Uri uri = MediaUtils.getArtWorkUri(m.getId(), m.getAlbumId());
                                 mCover.setUri(uri);
                             }
                             return mCover;
                         }
                     }
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<MusicCover>() {
                    @Override
                    public void call(MusicCover m) {
                        if (m.getFile() != null && m.getFile().exists()) {
                            holder.setImageSrc(R.id.album, m.getFile());
                        } else if (m.getUri() == null) {
                            holder.setImageSrc(R.id.album, R.mipmap.ic_custom);
                        } else {
                            holder.setImageSrc(R.id.album, m.getUri());
                        }
                    }
                });
        MainApplication.addToList(subscription, subscription1);

    }
}
