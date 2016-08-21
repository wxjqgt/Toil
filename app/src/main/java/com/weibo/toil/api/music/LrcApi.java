package com.weibo.toil.api.music;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by Administrator on 2016/7/18.
 */
public interface LrcApi {
    @GET
    Observable<String> getLrc();
}
