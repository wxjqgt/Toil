package com.weibo.toil.ui.activity;

import android.os.Bundle;

import org.greenrobot.eventbus.EventBus;

public abstract class BaseActivityWithEventBus extends BaseActivity {

    protected abstract int setContentView();

    @Override
    protected int getLayoutId() {
        return setContentView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
