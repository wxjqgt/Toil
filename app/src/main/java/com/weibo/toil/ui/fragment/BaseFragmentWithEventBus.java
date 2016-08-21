package com.weibo.toil.ui.fragment;

import android.app.usage.UsageEvents;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/18.
 */
public abstract class BaseFragmentWithEventBus extends BaseFragment {

    protected abstract int setContentView();

    @Override
    protected int getLayoutId() {
        return setContentView();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
