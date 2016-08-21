package com.weibo.toil.presenter.impl;

import com.weibo.toil.api.guokr.GuokrRequest;
import com.weibo.toil.api.zhihu.ZhihuRequest;
import com.weibo.toil.bean.guokr.GuokrArticle;
import com.weibo.toil.bean.zhihu.ZhihuStory;
import com.weibo.toil.presenter.IZhihuStoryPresenter;
import com.weibo.toil.ui.iView.IZhihuStory;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ZhihuStoryPresenterImpl extends BasePresenterImpl implements IZhihuStoryPresenter {

    private IZhihuStory mIZhihuStory;

    public ZhihuStoryPresenterImpl(IZhihuStory zhihuStory) {
        if (zhihuStory == null)
            throw new IllegalArgumentException("zhihuStory must not be null");
        mIZhihuStory = zhihuStory;
    }

    @Override
    public void getZhihuStory(String id) {
        Subscription s = ZhihuRequest.getZhihuApi().getZhihuStory(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ZhihuStory>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mIZhihuStory.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(ZhihuStory zhihuStory) {
                        mIZhihuStory.showZhihuStory(zhihuStory);
                    }
                });
        addSubscription(s);
    }

    @Override
    public void getGuokrArticle(String id) {
        Subscription s = GuokrRequest.getGuokrApi().getGuokrArticle(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GuokrArticle>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mIZhihuStory.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(GuokrArticle guokrArticle) {
                        mIZhihuStory.showGuokrArticle(guokrArticle);
                    }
                });
        addSubscription(s);
    }
}
