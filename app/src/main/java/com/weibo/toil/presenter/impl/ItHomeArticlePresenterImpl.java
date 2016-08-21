package com.weibo.toil.presenter.impl;

import com.weibo.toil.api.itHome.ItHomeRequest;
import com.weibo.toil.bean.itHome.ItHomeArticle;
import com.weibo.toil.presenter.IItHomeArticlePresenter;
import com.weibo.toil.ui.iView.IItHomeArticle;
import com.weibo.toil.utils.ItHomeUtil;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ItHomeArticlePresenterImpl extends BasePresenterImpl implements IItHomeArticlePresenter {

    private IItHomeArticle mIItHomeArticle;

    public ItHomeArticlePresenterImpl(IItHomeArticle iItHomeArticle) {
        if (iItHomeArticle == null)
            throw new IllegalArgumentException("iItHomeArticle must not be null");
        mIItHomeArticle = iItHomeArticle;
    }

    @Override
    public void getItHomeArticle(String id) {
        Subscription subscription = ItHomeRequest.getItHomeApi().getItHomeArticle(ItHomeUtil.getSplitNewsId(id))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ItHomeArticle>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mIItHomeArticle.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(ItHomeArticle itHomeArticle) {
                        mIItHomeArticle.showItHomeArticle(itHomeArticle);
                    }
                });
        addSubscription(subscription);
    }
}
