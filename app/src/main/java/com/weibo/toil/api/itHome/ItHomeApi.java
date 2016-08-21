package com.weibo.toil.api.itHome;

import com.weibo.toil.bean.itHome.ItHomeArticle;
import com.weibo.toil.bean.itHome.ItHomeResponse;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface ItHomeApi {

    @GET("/xml/newslist/news.xml")
    Observable<ItHomeResponse> getItHomeNews();

    @GET("/xml/newslist/news_{minNewsId}.xml")
    Observable<ItHomeResponse> getMoreItHomeNews(@Path("minNewsId") String minNewsId);

    @GET("/xml/newscontent/{id}.xml")
    Observable<ItHomeArticle> getItHomeArticle(@Path("id") String id);

}
