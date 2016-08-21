package com.weibo.toil.api.weixin;

import com.weibo.toil.bean.weixin.TxWeixinResponse;
import com.weibo.toil.utils.Constant;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface TxApi {
    @GET("/wxnew/?key=" + Constant.TX_APP_KEY + "&num=20")
    Observable<TxWeixinResponse> getWeixin(@Query("page") int page);
}