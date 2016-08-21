package com.weibo.toil.presenter.impl;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.weibo.toil.utils.Constant;
import com.weibo.toil.api.itHome.ItHomeRequest;
import com.weibo.toil.bean.itHome.ItHomeItem;
import com.weibo.toil.bean.itHome.ItHomeResponse;
import com.weibo.toil.presenter.IItHomePresenter;
import com.weibo.toil.ui.iView.IItHomeFragment;
import com.weibo.toil.utils.CacheUtil;
import com.weibo.toil.utils.ItHomeUtil;

import java.util.ArrayList;
import java.util.Iterator;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ItHomePresenterImpl extends BasePresenterImpl implements IItHomePresenter {

    private Gson gson = new Gson();

    private IItHomeFragment mItHomeFragment;

    private CacheUtil mCacheUtil;

    public ItHomePresenterImpl(IItHomeFragment iItHomeFragment, Context context) {
        if (iItHomeFragment == null)
            throw new IllegalArgumentException("iItHomeFragment must not be null");
        this.mItHomeFragment = iItHomeFragment;
        mCacheUtil = CacheUtil.get(context);
    }

    @Override
    public void getNewItHomeNews() {
        mItHomeFragment.showProgressDialog();
        Subscription subscription = ItHomeRequest.getItHomeApi().getItHomeNews()
                .subscribeOn(Schedulers.io())
                .map(new Func1<ItHomeResponse, ArrayList<ItHomeItem>>() {
                    @Override
                    public ArrayList<ItHomeItem> call(ItHomeResponse itHomeResponse) {
                        //过滤广告新闻
                        ArrayList<ItHomeItem> itHomeItems1 = itHomeResponse.getChannel().getItems();
                        Iterator<ItHomeItem> iter = itHomeItems1.iterator();
                        while (iter.hasNext()) {
                            ItHomeItem item = iter.next();
                            if (item.getUrl().contains("digi"))
                                iter.remove();
                        }
                        return itHomeItems1;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<ItHomeItem>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mItHomeFragment.hidProgressDialog();
                        mItHomeFragment.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(ArrayList<ItHomeItem> it) {
                        mItHomeFragment.hidProgressDialog();
                        mItHomeFragment.updateList(it);
                        mCacheUtil.put(Constant.IT, gson.toJson(it));
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void getMoreItHomeNews(String lastNewsId) {
        Subscription subscription = ItHomeRequest.getItHomeApi().getMoreItHomeNews(ItHomeUtil.getMinNewsId(lastNewsId))
                .map(new Func1<ItHomeResponse, ArrayList<ItHomeItem>>() {
                    @Override
                    public ArrayList<ItHomeItem> call(ItHomeResponse itHomeResponse) {
                        //过滤广告新闻
                        ArrayList<ItHomeItem> itHomeItems1 = itHomeResponse.getChannel().getItems();
                        Iterator<ItHomeItem> iter = itHomeItems1.iterator();
                        while (iter.hasNext()) {
                            ItHomeItem item = iter.next();
                            if (item.getUrl().contains("digi"))
                                iter.remove();
                        }
                        return itHomeItems1;
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<ItHomeItem>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mItHomeFragment.hidProgressDialog();
                        mItHomeFragment.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(ArrayList<ItHomeItem> it) {
                        mItHomeFragment.hidProgressDialog();
                        mItHomeFragment.updateList(it);
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void getNewsFromCache() {
        if (mCacheUtil.getAsJSONArray(Constant.IT) != null && mCacheUtil.getAsJSONArray(Constant.IT).length() != 0) {
            ArrayList<ItHomeItem> it = gson.fromJson(mCacheUtil.getAsJSONArray(Constant.IT).toString(), new TypeToken<ArrayList<ItHomeItem>>() {
            }.getType());
            mItHomeFragment.updateList(it);
        }
    }
}
