package com.weibo.toil.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.weibo.toil.R;
import com.weibo.toil.bean.music.Mp3Info;
import com.weibo.toil.ui.activity.MainActivity;
import com.weibo.toil.ui.event.MusicLrc;
import com.weibo.toil.ui.event.MusicProgress;
import com.weibo.toil.ui.view.LrcView;
import com.weibo.toil.utils.DLog;
import com.weibo.toil.utils.MainApplication;
import com.weibo.toil.utils.MediaUtils;
import com.weibo.toil.utils.MusicService;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import butterknife.BindView;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by 巴巴 on 2016/8/20.
 */
public class LrcFragment extends BaseFragmentWithEventBus{

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (MainActivity.ms.isPlaying()){
            int position = MainActivity.ms.getCurrentPlayPosition();
            final Mp3Info mp3Info = MusicService.mp3InfoList.get(position);
            Subscription subscription = Observable.just(mp3Info)
                    .map(new Func1<Mp3Info, MusicLrc>() {
                        @Override
                        public MusicLrc call(Mp3Info mp3Info) {
                            MusicLrc musicLrc;
                            String lrc = MediaUtils.getLrcFromLocal(mp3Info.getArtist(),mp3Info.getTitle());
                            if (TextUtils.isEmpty(lrc)){
                                musicLrc = MediaUtils.downFile(mp3Info.getArtist(),mp3Info.getTitle());
                            }else {
                                musicLrc = new MusicLrc();
                                musicLrc.setLrc(lrc);
                                musicLrc.setSuccess(true);
                                musicLrc.setPath(MediaUtils.LRC_DIRECTORY + mp3Info.getArtist() + " - " + mp3Info.getTitle() + ".lrc");
                            }
                            return musicLrc;
                        }
                    })
                    .subscribe(new Action1<MusicLrc>() {
                        @Override
                        public void call(MusicLrc musicLrc) {
                            if (musicLrc != null) {
                                updateLrc(musicLrc);
                            }
                        }
                    });
            MainApplication.addToList(subscription);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateTime(MusicProgress musicProgress){
        lrcView.updateTime(musicProgress.getMusicPlayUpdateProgress());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateLrc(MusicLrc musicLrc){
        if (musicLrc.isSuccess()){
            lrcView.loadLrcFromFile(musicLrc.getLrc());
            lrc_is.setVisibility(View.GONE);
        }
    }

    public static LrcFragment newInstance(){
        LrcFragment lrcFragment = new LrcFragment();
        return lrcFragment;
    }

    public LrcFragment() {}

    @Override
    protected int setContentView() {
        return R.layout.fragment_lrc;
    }

    @BindView(R.id.lrc)
    LrcView lrcView;
    @BindView(R.id.lrc_is)
    TextView lrc_is;

}
