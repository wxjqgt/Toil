package com.weibo.toil.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.weibo.toil.utils.Constant;
import com.weibo.toil.R;
import com.weibo.toil.presenter.IChangeChannelPresenter;
import com.weibo.toil.presenter.impl.ChangeChannelPresenterImpl;
import com.weibo.toil.ui.adapter.ChannelAdapter;
import com.weibo.toil.ui.event.StatusBarEvent;
import com.weibo.toil.ui.helper.ItemDragHelperCallback;
import com.weibo.toil.ui.iView.IChangeChannel;
import com.weibo.toil.utils.RxBus;

import java.util.ArrayList;

import butterknife.BindView;

public class ChangeChannelActivity extends BaseActivity implements IChangeChannel {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rv_channel)
    RecyclerView mRv;

    private ArrayList<Constant.Channel> savedChannel = new ArrayList<>();
    private ArrayList<Constant.Channel> otherChannel = new ArrayList<>();
    private IChangeChannelPresenter mIChangeChannelPresenter;
    private ChannelAdapter mChannelAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_change_channel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initView();
    }

    private void initData() {
        mIChangeChannelPresenter = new ChangeChannelPresenterImpl(this, this);
    }

    private void initView() {
        mToolbar.setTitle(getString(R.string.activity_change_channel_title));
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setToolBar(null, mToolbar, true, true, null);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRv.setLayoutManager(linearLayoutManager);
        ItemDragHelperCallback callback = new ItemDragHelperCallback();
        final ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mRv);
        mRv.setHasFixedSize(true);
        mRv.setNestedScrollingEnabled(false);
        mChannelAdapter = new ChannelAdapter(this, helper, savedChannel, otherChannel);
        mRv.setAdapter(mChannelAdapter);
        mIChangeChannelPresenter.getChannel();
    }


    @Override
    public void showChannel(ArrayList<Constant.Channel> savedChannel, ArrayList<Constant.Channel> otherChannel) {
        this.savedChannel.addAll(savedChannel);
        this.otherChannel.addAll(otherChannel);
        mChannelAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        mIChangeChannelPresenter.saveChannel(savedChannel);
        RxBus.getDefault().send(new StatusBarEvent());
        super.onBackPressed();
    }
}
