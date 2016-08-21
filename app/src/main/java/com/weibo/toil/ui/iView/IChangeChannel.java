package com.weibo.toil.ui.iView;

import com.weibo.toil.utils.Constant;

import java.util.ArrayList;

public interface IChangeChannel {

    void showChannel(ArrayList<Constant.Channel> savedChannel, ArrayList<Constant.Channel> otherChannel);
}
