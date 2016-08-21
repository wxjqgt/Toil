package com.weibo.toil.ui.iView;

import com.weibo.toil.bean.weiboVideo.WeiboVideoBlog;

import java.util.ArrayList;

public interface IVideoFragment extends IBaseFragment{
    void updateList(ArrayList<WeiboVideoBlog> weiboVideoBlogs);
}
