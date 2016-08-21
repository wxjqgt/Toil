package com.weibo.toil.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.weibo.toil.R;
import com.weibo.toil.ui.adapter.MyFragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by 巴巴 on 2016/8/18.
 */

public class MusicFragment extends BaseFragment{

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(LocalMusicFragment.newInstance());
        fragments.add(MusicPlayFragment.newInstance());
        fragments.add(LrcFragment.newInstance());
        viewPager.setAdapter(new MyFragmentPagerAdapter(this.getFragmentManager(),fragments));
        viewPager.setCurrentItem(1);
    }

    @BindView(R.id.musicViewpager)
    ViewPager viewPager;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_music;
    }

    public static MusicFragment newInstance(){
        MusicFragment musicFragment = new MusicFragment();
        return musicFragment;
    }

}
