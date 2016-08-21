package com.weibo.toil.presenter;

public interface IVideoPresenter extends BasePresenter{
    void getVideo(int page);

    void getVideoFromCache(int page);
}
