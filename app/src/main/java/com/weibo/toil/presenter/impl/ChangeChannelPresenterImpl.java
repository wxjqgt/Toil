package com.weibo.toil.presenter.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.weibo.toil.utils.Constant;
import com.weibo.toil.presenter.IChangeChannelPresenter;
import com.weibo.toil.ui.iView.IChangeChannel;
import com.weibo.toil.utils.SharePreferenceUtil;

import java.util.ArrayList;
import java.util.Collections;

public class ChangeChannelPresenterImpl implements IChangeChannelPresenter {

    private IChangeChannel mIChangeChannel;
    private SharedPreferences mSharedPreferences;
    private ArrayList<Constant.Channel> savedChannelList;
    private ArrayList<Constant.Channel> dismissChannelList;

    public ChangeChannelPresenterImpl(IChangeChannel changeChannel, Context context) {
        mIChangeChannel = changeChannel;
        mSharedPreferences = context.getSharedPreferences(SharePreferenceUtil.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        savedChannelList = new ArrayList<>();
        dismissChannelList = new ArrayList<>();
    }

    @Override
    public void getChannel() {
        String savedChannel = mSharedPreferences.getString(SharePreferenceUtil.SAVED_CHANNEL, null);
        if (TextUtils.isEmpty(savedChannel)) {
            Collections.addAll(savedChannelList, Constant.Channel.values());
        } else {
            for (String s : savedChannel.split(",")) {
                savedChannelList.add(Constant.Channel.valueOf(s));
            }
        }
        for (Constant.Channel channel : Constant.Channel.values()) {
            if (!savedChannelList.contains(channel)) {
                dismissChannelList.add(channel);
            }
        }
        mIChangeChannel.showChannel(savedChannelList, dismissChannelList);
    }

    @Override
    public void saveChannel(ArrayList<Constant.Channel> savedChannel) {
        StringBuilder stringBuffer = new StringBuilder();
        for (Constant.Channel channel : savedChannel) {
            stringBuffer.append(channel.name()).append(",");
        }
        mSharedPreferences.edit().putString(SharePreferenceUtil.SAVED_CHANNEL, stringBuffer.toString()).apply();
    }

}

