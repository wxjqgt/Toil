package com.weibo.toil.presenter;

import com.weibo.toil.utils.Constant;

import java.util.ArrayList;

public interface IChangeChannelPresenter {

    void getChannel();

    void saveChannel(ArrayList<Constant.Channel> savedChannel);
}
