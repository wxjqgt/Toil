package com.weibo.toil.presenter.impl;

import android.content.Context;

import com.google.gson.Gson;
import com.weibo.toil.utils.Constant;
import com.weibo.toil.api.weixin.TxRequest;
import com.weibo.toil.bean.weixin.TxWeixinResponse;
import com.weibo.toil.presenter.IWeixinPresenter;
import com.weibo.toil.ui.iView.IWeixinFragment;
import com.weibo.toil.utils.CacheUtil;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class WeiXinPresenterImpl extends BasePresenterImpl implements IWeixinPresenter {

    private CacheUtil mCacheUtil;
    private IWeixinFragment mWeixinFragment;
    private Gson mGson = new Gson();

    public WeiXinPresenterImpl(IWeixinFragment weixinFragment, Context context) {
        if (weixinFragment==null)
            throw new IllegalArgumentException("weixinFragment must not be null");
        this.mWeixinFragment = weixinFragment;
        mCacheUtil = CacheUtil.get(context);
    }

    @Override
    public void getWeixinNews(final int page) {
        mWeixinFragment.showProgressDialog();
        Subscription subscription = TxRequest.getTxApi().getWeixin(page).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TxWeixinResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mWeixinFragment.hidProgressDialog();
                        mWeixinFragment.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(TxWeixinResponse txWeixinResponse) {
                        mWeixinFragment.hidProgressDialog();
                        if (txWeixinResponse.getCode() == 200) {
                            mWeixinFragment.updateList(txWeixinResponse.getNewslist());
                            mCacheUtil.put(Constant.WEIXIN + page, mGson.toJson(txWeixinResponse));
                        } else {
                            mWeixinFragment.showError("服务器内部错误！");
                        }
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void getWeixinNewsFromCache(int page) {
        if (mCacheUtil.getAsJSONObject(Constant.WEIXIN + page) != null) {
            TxWeixinResponse txWeixinResponse = mGson.fromJson(mCacheUtil.getAsJSONObject(Constant.WEIXIN + page).toString(), TxWeixinResponse.class);
            mWeixinFragment.updateList(txWeixinResponse.getNewslist());
        }
    }
}
