package com.weibo.toil.presenter;

public interface IItHomePresenter extends BasePresenter{
    void getNewItHomeNews();

    void getMoreItHomeNews(String lastNewsId);

    void getNewsFromCache();
}
