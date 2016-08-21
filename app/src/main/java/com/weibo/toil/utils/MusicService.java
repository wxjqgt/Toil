package com.weibo.toil.utils;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;

import com.weibo.toil.bean.music.Mp3Info;
import com.weibo.toil.ui.event.MusicLrc;
import com.weibo.toil.ui.event.MusicPosition;
import com.weibo.toil.ui.event.MusicProgress;
import com.weibo.toil.ui.event.MusicResume;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.vov.vitamio.MediaPlayer;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {

    public static MediaPlayer mp;
    public static final String KEY = "key", PROGRESS_KEY = "progress_key", MODE = "mode";
    public static final int MODE_ORDER = 0x0, MODE_RANDOM = 0x1,MODE_ORDER_NEXT = 0x3, MODE_ORDER_LAST = 0x4;
    public static boolean isPause = false, isResume = false;
    private int mode = MODE_ORDER,playMode = MODE_ORDER_NEXT,musicPosition;
    public static List<Mp3Info> mp3InfoList;

    private MainBinder mb = null;
    private ScheduledExecutorService updateService = null;
    private MusicProgress progress;
    private MusicPosition position;
    private MusicLrc musicLrc;

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public boolean isPlaying() {
        if (mp != null) {
            return mp.isPlaying();
        }
        return false;
    }

    public long getCurrentPosition() {
        if (mp != null && isPlaying()) {
            return mp.getCurrentPosition();
        }
        return 0;
    }

    public int getCurrentPlayPosition() {
        return musicPosition;
    }

    public void playMusic(int mposition) {
        if (mp == null) {
            return;
        }
        if (mp3InfoList != null && mp3InfoList.size() != 0) {
            if (mposition < 0) {
                mposition = mp3InfoList.size() - 1;
            }
            if (mposition >= mp3InfoList.size()) {
                mposition = 0;
            }
        } else {
            return;
        }
        musicPosition = mposition;
        final Mp3Info mp3Info = mp3InfoList.get(musicPosition);
        String url = mp3Info.getUrl();
        try {
            mp.reset();
            mp.setDataSource(url);
            mp.setOnPreparedListener(this);
            mp.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        position = MusicUpdateHelper.position;
        position.setMusicPlayUpdatePosition(musicPosition);
        position.setMp3Info(mp3Info);
        EventBus.getDefault().post(position);

       MainApplication.getService().submit(new Runnable() {
           @Override
           public void run() {
               String lrc = MediaUtils.getLrcFromLocal(mp3Info.getArtist(),mp3Info.getTitle());
               if (TextUtils.isEmpty(lrc)){
                   musicLrc = MediaUtils.downFile(mp3Info.getArtist(),mp3Info.getTitle());
               }else {
                   musicLrc = MusicUpdateHelper.musicLrc;
                   musicLrc.setLrc(lrc);
                   musicLrc.setSuccess(true);
                   musicLrc.setPath(MediaUtils.LRC_DIRECTORY + mp3Info.getArtist() + "-" + mp3Info.getTitle() + ".lrc");
               }
               EventBus.getDefault().post(musicLrc);
           }
       });

        isPause = false;
    }

    private static class MusicUpdateHelper {
        public static final MusicPosition position = new MusicPosition();
        public static final MusicProgress progress = new MusicProgress();
        public static final MusicLrc musicLrc = new MusicLrc();
    }

    public void seekTo(int progress) {
        if (mp != null) {
            mp.seekTo(progress);
        }
    }

    public void playNextMusic() {
        playMode = MODE_ORDER_NEXT;
        playMusicByMode();
    }

    public void playLastMusic() {
        playMode = MODE_ORDER_LAST;
        playMusicByMode();
    }

    private static Random random = new Random();

    private void playMusicByMode() {
        switch (mode) {
            case MODE_ORDER:
                if (playMode == MODE_ORDER_NEXT) {
                    musicPosition += 1;
                } else if (playMode == MODE_ORDER_LAST) {
                    musicPosition -= 1;
                }
                playMusic(musicPosition);
                break;
            case MODE_RANDOM:
                if (random == null) {
                    random = new Random();
                }
                musicPosition = random.nextInt(mp3InfoList.size());
                playMusic(musicPosition);
                break;
            default:
                musicPosition = random.nextInt(mp3InfoList.size());
                playMusic(musicPosition);
                break;
        }
    }

    public void resumePlayMusic() {
        if (mp != null) {
            if (isPause) {
                mp.start();
            } else {
                playMusic(SPUtil.getInt(KEY));
                isResume = true;
            }
        }
    }

    public void resumeUpdateMusicUi() {
        int p = SPUtil.getInt(KEY);
        long time = SPUtil.getLong(PROGRESS_KEY);

        position = MusicUpdateHelper.position;
        position.setMusicPlayUpdatePosition(p);
        position.setMp3Info(mp3InfoList.get(p));
        EventBus.getDefault().post(position);

        progress = MusicUpdateHelper.progress;
        String text = MediaUtils.formattime(time);
        progress.setMusicPlayUpdateProgressText(text);
        progress.setMusicPlayUpdateProgress(time);

        EventBus.getDefault().post(progress);
        EventBus.getDefault().post(new MusicResume());

    }

    public void pauseMusic() {
        if (mp != null && mp.isPlaying()) {
            SPUtil.putLong(PROGRESS_KEY, getCurrentPosition());
            SPUtil.putInt(KEY, musicPosition);
            SPUtil.putInt(MODE, mode);
            mp.pause();
            isPause = true;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playMusicByMode();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        playMusicByMode();
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        musicPosition = SPUtil.getInt(KEY);
        mode = SPUtil.getInt(MODE);
        setMode(mode);
        updateService = Executors.newScheduledThreadPool(2);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SPUtil.putInt(KEY, musicPosition);
        SPUtil.putLong(PROGRESS_KEY, getCurrentPosition());
        SPUtil.putInt(MODE, mode);
        if (mp != null) {
            mp.release();
            mp = null;
        }

        if (updateService != null) {
            if (!updateService.isShutdown()) {
                updateService.shutdown();
                updateService = null;
            }
        }
        random = null;
    }

    public void getInstance() {
        mp = SingleMpHolder.mediaPlayer;
        mp.setOnCompletionListener(this);
        mp.setOnErrorListener(this);
    }

    private static class SingleMpHolder {
        public static final MediaPlayer mediaPlayer = new MediaPlayer(MainApplication.getContext());
    }

    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        mb = new MainBinder();
        return mb;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        if (isResume) {
            mp.seekTo((int) SPUtil.getLong(PROGRESS_KEY));
            isResume = false;
        }
    }

    public class MainBinder extends Binder implements IBinder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public void updateMusic() {
        updateService.scheduleAtFixedRate(updateProgress, 1, 1, TimeUnit.SECONDS);
    }

    Runnable updateProgress = new Runnable() {
        @Override
        public void run() {
            if (isPlaying()) {
                progress = MusicUpdateHelper.progress;
                long time = getCurrentPosition();
                String text = MediaUtils.formattime(time);
                progress.setMusicPlayUpdateProgress(time);
                progress.setMusicPlayUpdateProgressText(text);
                EventBus.getDefault().post(progress);
            }
        }
    };

    Runnable updateMp3List = new Runnable() {
        @Override
        public void run() {
            mp3InfoList = MediaUtils.getMp3InfoList(MainApplication.getContext());
            EventBus.getDefault().post(mp3InfoList);
        }
    };

    public void updateMusicList(){
        updateService.execute(updateMp3List);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateMusicList();
        return super.onStartCommand(intent, flags, startId);
    }
}
