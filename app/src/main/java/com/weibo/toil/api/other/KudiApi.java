package com.weibo.toil.api.other;

import com.weibo.toil.bean.other.Kuaidi;

import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by Administrator on 2016/7/19.
 */
public interface KudiApi {
    //query?type=快递公司代号&postid=快递单号"
    @GET("query?")
    Observable<Kuaidi> searchKudi(@Query("type") String type,@Query("postid") String postid);
}
