package com.weibo.toil.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.weibo.toil.utils.Constant;
import com.weibo.toil.utils.MainApplication;
import com.weibo.toil.R;
import com.weibo.toil.ui.fragment.GuokrFragment;
import com.weibo.toil.ui.fragment.ItHomeFragment;
import com.weibo.toil.ui.fragment.MusicFragment;
import com.weibo.toil.ui.fragment.OtherFragment;
import com.weibo.toil.ui.fragment.VideoFragment;
import com.weibo.toil.ui.fragment.WeixinFragment;
import com.weibo.toil.ui.fragment.ZhihuFragment;
import com.weibo.toil.utils.Config;
import com.weibo.toil.utils.MediaUtils;
import com.weibo.toil.utils.SharePreferenceUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            if (Config.isNight) {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            // 调用 recreate() 使设置生效
            recreate();
        }

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        mSharedPreferences = getSharedPreferences(SharePreferenceUtil.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);

        setSupportActionBar(toolbar);

        boolean isKitKat = Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT;
        if (isKitKat) {
            ctlMain.setFitsSystemWindows(false);
        }
        setToolBar(null, toolbar, true, false, drawerLayout);
        //改变statusBar颜色而DrawerLayout依然可以显示在StatusBar
        //ctlMain.setStatusBarBackgroundColor(setToolBar(toolbar,true,false,null));
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        View headerLayout = navView.getHeaderView(0);
        LinearLayout llImage = (LinearLayout) headerLayout.findViewById(R.id.side_image);
        TextView imageDescription = (TextView) headerLayout.findViewById(R.id.image_description);

        assert navView != null;
        navView.setNavigationItemSelectedListener(this);
        int[][] state = new int[][]{
                new int[]{-android.R.attr.state_checked}, // unchecked
                new int[]{android.R.attr.state_checked}  // pressed
        };

        getSwipeBackLayout().setEnableGesture(false);

        int[] color = new int[]{
                Color.BLACK,
                getSharedPreferences(SharePreferenceUtil.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).getInt(SharePreferenceUtil.MUTED, ContextCompat.getColor(this, R.color.colorAccent))
        };
        if (Config.isNight) {
            llImage.setAlpha(0.2f);
            color = new int[]{
                    Color.DKGRAY,
                    getSharedPreferences(SharePreferenceUtil.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).getInt(SharePreferenceUtil.MUTED, ContextCompat.getColor(this, R.color.colorAccent))
            };
        }
        navView.setItemTextColor(new ColorStateList(state, color));
        navView.setItemIconTintList(new ColorStateList(state, color));

        if (new File(getFilesDir().getPath() + "/bg.jpg").exists()) {
            BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), getFilesDir().getPath() + "/bg.jpg");
            llImage.setBackground(bitmapDrawable);
            imageDescription.setText(getSharedPreferences(SharePreferenceUtil.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).getString(SharePreferenceUtil.IMAGE_DESCRIPTION, "我的愿望，就是希望你的愿望里，也有我"));
        }
        initMenu();

    }

    @Override
    public void onBackPressed() {
        closeDrawer();
    }

    private void closeDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    private void switchFragment(String title, String tag) {
        if (fm == null) {
            fm = getSupportFragmentManager();
        }
        if (lastFragment != null) {
            fm.beginTransaction().hide(lastFragment).commit();
        }
        Fragment fragment = fm.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = addFragment(tag);
            fm.beginTransaction().add(R.id.replace, fragment, tag).commit();
        } else {
            fm.beginTransaction().show(fragment).commit();
        }
        lastFragment = fragment;

        setTransiton(fragment);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(title);
    }

    private void setTransiton(Fragment fragment) {
        Slide slideTransition;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //Gravity.START部分机型崩溃java.lang.IllegalArgumentException: Invalid slide direction
            slideTransition = new Slide(Gravity.LEFT);
            slideTransition.setDuration(700);
            fragment.setEnterTransition(slideTransition);
            fragment.setExitTransition(slideTransition);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id < mTitles.size()) {
            switchFragment(getString(mTitles.get(id)), titles.get(id));
        }
        switch (id) {
            case R.id.nav_night:
                Config.isNight = !Config.isNight;
                finish();
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.nav_setting:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.nav_change:
                startActivity(new Intent(this, ChangeChannelActivity.class));
                break;
            case R.id.nav_exit:
                ms.stopSelf();
                MainApplication.clearList();
                MainApplication.shutDownService();
                EventBus.clearCaches();
                exit();
                System.exit(0);
                break;
        }
        closeDrawer();
        return true;
    }

    private void initMenu() {
        ArrayList<Constant.Channel> savedChannelList = new ArrayList<>();
        mTitles = new ArrayList<>();
        titles = new ArrayList<>();
        Menu menu = navView.getMenu();
        menu.clear();
        String savedChannel = mSharedPreferences.getString(SharePreferenceUtil.SAVED_CHANNEL, null);
        if (TextUtils.isEmpty(savedChannel)) {
            Collections.addAll(savedChannelList, Constant.Channel.values());
        } else {
            for (String s : savedChannel.split(",")) {
                savedChannelList.add(Constant.Channel.valueOf(s));
            }
        }
        int count = savedChannelList.size();
        for (int i = 0; i < count; i++) {
            Constant.Channel chan = savedChannelList.get(i);
            int title = chan.getTitle();
            MenuItem menuItem = menu.add(0, i, 0, title);
            mTitles.add(title);
            menuItem.setIcon(chan.getIcon());
            menuItem.setCheckable(true);
            titles.add(chan.name());
            if (i == 0) {
                menuItem.setChecked(true);
            }
        }
        navView.inflateMenu(R.menu.activity_main_drawer);
        switchFragment(getString(mTitles.get(0)), titles.get(0));
    }

    private Fragment addFragment(String name) {
        Fragment fragment = null;
        switch (name) {
            case "GUOKR":
                fragment = GuokrFragment.newInstance();
                break;
            case "WEIXIN":
                fragment = WeixinFragment.newInstance();
                break;
            case "ZHIHU":
                fragment = ZhihuFragment.newInstance();
                break;
            case "VIDEO":
                fragment = VideoFragment.newInstance();
                break;
            case "IT":
                fragment = ItHomeFragment.newInstance();
                break;
            case "MUSIC":
                fragment = MusicFragment.newInstance();
                break;
            case "OTHER":
                fragment = OtherFragment.newInstance();
                break;
        }
        return fragment;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
        bindMainService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
        UnbindMainService();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.ctl_main)
    CoordinatorLayout ctlMain;

    private FragmentManager fm = null;
    private Fragment lastFragment;
    private ArrayList<Integer> mTitles;
    private ArrayList<String> titles;
    private SharedPreferences mSharedPreferences;

}
