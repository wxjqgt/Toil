package com.weibo.toil.api.util;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

public class UtilRequest {

    private UtilRequest() {}

    private static UtilApi utilApi = null;
    private static final Object monitor = new Object();

    public static UtilApi getUtilApi() {
        synchronized (monitor) {
            if (utilApi == null) {
                utilApi = new Retrofit.Builder()
                        .baseUrl("http://www.baidu.com")
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .build().create(UtilApi.class);
            }
            return utilApi;
        }
    }
}
