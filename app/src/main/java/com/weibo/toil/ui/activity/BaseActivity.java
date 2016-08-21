package com.weibo.toil.ui.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.swipebackactivity.app.SwipeBackActivity;
import com.umeng.analytics.MobclickAgent;
import com.weibo.toil.utils.Config;
import com.weibo.toil.utils.MusicService;
import com.weibo.toil.utils.SharePreferenceUtil;
import com.weibo.toil.utils.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends SwipeBackActivity {

    private ArrayList<Activity> list = new ArrayList<>();
    private static ServiceConnection connection = null;
    public static MusicService ms;
    private boolean isbind = false;

    private Unbinder unbinder;

    protected abstract int getLayoutId();

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        unbinder = ButterKnife.bind(this);
        list.add(this);
        if (connection == null) {
            connection = new MainServiceConnection();
        }
    }
    //绑定服务
    public void bindMainService(){
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent,connection, Context.BIND_AUTO_CREATE);
        isbind = true;
    }

    //解绑服务
    public void UnbindMainService(){
        if (isbind){
            unbindService(connection);
            isbind = false;
        }
    }

    private class MainServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ms = ((MusicService.MainBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            ms = null;
            isbind = false;
        }
    }

    public void exit(){
        for (Activity a:list) {
            a.finish();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    public int setToolBar(FloatingActionButton floatingActionButton, Toolbar toolbar, boolean isChangeToolbar, boolean isChangeStatusBar, DrawerLayout drawerLayout) {
        int vibrantColor = getSharedPreferences(SharePreferenceUtil.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).getInt(SharePreferenceUtil.VIBRANT, 0);
        int mutedColor = getSharedPreferences(SharePreferenceUtil.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).getInt(SharePreferenceUtil.MUTED, 0);
        if (Config.isNight) {
            vibrantColor = Color.BLACK;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (SharePreferenceUtil.isChangeNavColor(this))
                getWindow().setNavigationBarColor(vibrantColor);
            else
                getWindow().setNavigationBarColor(Color.BLACK);
        }
        if (floatingActionButton != null)
            floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(mutedColor));
        if (isChangeToolbar)
            toolbar.setBackgroundColor(vibrantColor);
        if (isChangeStatusBar) {
            if (SharePreferenceUtil.isImmersiveMode(this))
                StatusBarUtil.setColorNoTranslucent(this, vibrantColor);
            else
                StatusBarUtil.setColor(this, vibrantColor);
        }
        if (drawerLayout != null) {
            if (SharePreferenceUtil.isImmersiveMode(this))
                StatusBarUtil.setColorNoTranslucentForDrawerLayout(this, drawerLayout, vibrantColor);
            else
                StatusBarUtil.setColorForDrawerLayout(this, drawerLayout, vibrantColor);
        }
        return vibrantColor;
    }
}
