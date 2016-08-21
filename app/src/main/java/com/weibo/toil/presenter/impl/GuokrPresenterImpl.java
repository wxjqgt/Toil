package com.weibo.toil.presenter.impl;

import android.content.Context;

import com.google.gson.Gson;
import com.weibo.toil.utils.Constant;
import com.weibo.toil.api.guokr.GuokrRequest;
import com.weibo.toil.bean.guokr.GuokrHot;
import com.weibo.toil.presenter.IGuokrPresenter;
import com.weibo.toil.ui.iView.IGuokrFragment;
import com.weibo.toil.utils.CacheUtil;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by 蔡小木 on 2016/4/22 0022.
 */
public class GuokrPresenterImpl extends BasePresenterImpl implements IGuokrPresenter {

    private IGuokrFragment mGuokrFragment;
    private CacheUtil mCacheUtil;

    public GuokrPresenterImpl(IGuokrFragment guokrFragment, Context context) {
        if (guokrFragment==null)
            throw new IllegalArgumentException("guokrFragment must not be null");
        this.mGuokrFragment = guokrFragment;
        mCacheUtil = CacheUtil.get(context);
    }

    @Override
    public void getGuokrHot(final int offset) {
        mGuokrFragment.showProgressDialog();
        Subscription s = GuokrRequest.getGuokrApi().getGuokrHot(offset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GuokrHot>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mGuokrFragment.hidProgressDialog();
                        mGuokrFragment.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(GuokrHot guokrHot) {
                        mGuokrFragment.hidProgressDialog();
                        mGuokrFragment.updateList(guokrHot.getResult());
                        mCacheUtil.put(Constant.GUOKR + offset, new Gson().toJson(guokrHot));
                    }
                });
        addSubscription(s);
    }

    @Override
    public void getGuokrHotFromCache(int offset) {
        if (mCacheUtil.getAsJSONObject(Constant.GUOKR + offset) != null) {
            GuokrHot guokrHot = new Gson().fromJson(mCacheUtil.getAsJSONObject(Constant.GUOKR + offset).toString(), GuokrHot.class);
            mGuokrFragment.updateList(guokrHot.getResult());
        }
    }
}
