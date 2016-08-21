package com.weibo.toil.api.guokr;

import com.weibo.toil.bean.guokr.GuokrArticle;
import com.weibo.toil.bean.guokr.GuokrHot;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface GuokrApi {

    @GET("http://apis.guokr.com/minisite/article.json?retrieve_type=by_minisite")
    Observable<GuokrHot> getGuokrHot(@Query("offset") int offset);

    @GET("http://apis.guokr.com/minisite/article/{id}.json")
    Observable<GuokrArticle> getGuokrArticle(@Path("id") String id);
}
