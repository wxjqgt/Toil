package com.weibo.toil.ui.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.weibo.toil.R;
import com.weibo.toil.bean.music.Mp3Info;
import com.weibo.toil.ui.activity.MainActivity;
import com.weibo.toil.ui.event.MusicCover;
import com.weibo.toil.ui.event.MusicPosition;
import com.weibo.toil.ui.event.MusicProgress;
import com.weibo.toil.ui.event.MusicResume;
import com.weibo.toil.ui.view.CoverView;
import com.weibo.toil.ui.view.ProgressView;
import com.weibo.toil.utils.DLog;
import com.weibo.toil.utils.ImageLoader;
import com.weibo.toil.utils.MusicService;
import com.weibo.toil.utils.MediaUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class MusicPlayFragment extends BaseFragmentWithEventBus {

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void UpdateProgress(MusicProgress progress) {
        mProgressView.setProgress((int) progress.getMusicPlayUpdateProgress());
        mTimeView.setText(progress.getMusicPlayUpdateProgressText());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void UpdatePosition(MusicPosition position) {
        Mp3Info mp3Info = position.getMp3Info();

        int max = (int) mp3Info.getDuration();
        mProgressView.setMax(max);

        songTitle.setText(mp3Info.getTitle());
        songSinger.setText(mp3Info.getArtist());
        mDurationView.setText(MediaUtils.formattime(mp3Info.getDuration()));

        playorpause.setImageResource(R.mipmap.btn_notification_player_stop_normal);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void updateMusicCover(MusicPosition position){
        Mp3Info mp3Info = position.getMp3Info();
        MusicCover musicCover = new MusicCover();
        File coverUrl = MediaUtils.getCover(mp3Info.getArtist(),mp3Info.getTitle());
        musicCover.setFile(coverUrl);
        if (coverUrl == null) {
            Uri uri = MediaUtils.getArtWorkUri(mp3Info.getId(), mp3Info.getAlbumId());
            musicCover.setUri(uri);
        }
        EventBus.getDefault().post(musicCover);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleMessage(MusicCover musicCover){
        if (musicCover.getFile() != null && musicCover.getFile().exists()) {
            ImageLoader.load(this,musicCover.getFile(),mCoverView);
        }else {
            ImageLoader.load(this, musicCover.getUri(), mCoverView);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void UpdatePlayState(MusicResume musicResume) {
        playorpause.setImageResource(R.mipmap.btn_notification_player_play_normal);
        mCoverView.stop();
    }

    @OnClick({R.id.repeat, R.id.shuffle, R.id.rewind, R.id.forward, R.id.playorpause})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.forward:
                musicService.playLastMusic();
                playorpause.setImageResource(R.mipmap.btn_notification_player_stop_normal);
                mCoverView.start();
                break;
            case R.id.rewind:
                musicService.playNextMusic();
                playorpause.setImageResource(R.mipmap.btn_notification_player_stop_normal);
                mCoverView.start();
                break;
            case R.id.playorpause:
                if (musicService.isPlaying()) {
                    musicService.pauseMusic();
                    playorpause.setImageResource(R.mipmap.btn_notification_player_play_normal);
                    mCoverView.stop();
                } else {
                    musicService.resumePlayMusic();
                    playorpause.setImageResource(R.mipmap.btn_notification_player_stop_normal);
                    mCoverView.start();
                }
                break;
            case R.id.repeat:
                musicService.setMode(MusicService.MODE_ORDER);
                break;
            case R.id.shuffle:
                musicService.setMode(MusicService.MODE_RANDOM);
                break;
            default:
                break;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        musicService.getInstance();
        musicService.updateMusic();
        musicService.resumeUpdateMusicUi();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
        musicService = mainActivity.ms;
    }

    @Override
    protected int setContentView() {
        return R.layout.musicplayframent;
    }

    public MusicPlayFragment() {
    }

    public static MusicPlayFragment newInstance() {
        MusicPlayFragment fragment = new MusicPlayFragment();
        return fragment;
    }


    @BindView(R.id.songTitle)
    TextView songTitle;
    @BindView(R.id.songSinger)
    TextView songSinger;
    @BindView(R.id.rewind)
    ImageView rewind;
    @BindView(R.id.forward)
    ImageView forward;
    @BindView(R.id.repeat)
    ImageView repeat;
    @BindView(R.id.shuffle)
    ImageView shuffle;
    @BindView(R.id.playorpause)
    ImageView playorpause;
    @BindView(R.id.time)
    TextView mTimeView;
    @BindView(R.id.duration)
    TextView mDurationView;
    @BindView(R.id.progress)
    ProgressView mProgressView;
    @BindView(R.id.cover)
    CoverView mCoverView;

    private MainActivity mainActivity;
    private MusicService musicService;

}
