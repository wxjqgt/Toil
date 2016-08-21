package com.weibo.toil.ui.iView;

import com.weibo.toil.bean.weixin.WeixinNews;

import java.util.ArrayList;

public interface IWeixinFragment extends IBaseFragment{
    void updateList(ArrayList<WeixinNews> weixinNewses);
}
