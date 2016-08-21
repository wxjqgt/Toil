package com.weibo.toil.presenter;

public interface IWeixinPresenter extends BasePresenter{
    void getWeixinNews(int page);
    void getWeixinNewsFromCache(int page);
}
