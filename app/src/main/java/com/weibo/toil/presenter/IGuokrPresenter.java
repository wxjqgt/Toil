package com.weibo.toil.presenter;

public interface IGuokrPresenter extends BasePresenter {
    void getGuokrHot(int offset);
    void getGuokrHotFromCache(int offset);
}
