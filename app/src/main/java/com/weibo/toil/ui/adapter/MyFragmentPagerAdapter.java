package com.weibo.toil.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by ChenXianglong on 2015/12/29.
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    //fragment集合
    private List<Fragment> al_fragment;

    public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> al_fragment) {
        super(fm);
        this.al_fragment = al_fragment;
    }

    @Override
    public Fragment getItem(int position) {
        return al_fragment.get(position);
    }

    @Override
    public int getCount() {
            return al_fragment.size();
    }
}
