package com.weibo.toil.api.zhihu;

import com.weibo.toil.utils.MainApplication;
import com.weibo.toil.utils.NetWorkUtil;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ZhihuRequest {

    private ZhihuRequest() {}

    private static final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            if (NetWorkUtil.isNetWorkAvailable(MainApplication.getContext())) {
                int maxAge = 60; // 在线缓存在1分钟内可读取
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            } else {
                int maxStale = 60 * 60 * 24 * 28; // 离线时缓存保存4周
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
        }
    };
    private static File httpCacheDirectory = new File(MainApplication.getContext().getCacheDir(), "zhihuCache");

    private static int cacheSize = 10 * 1024 * 1024; // 10 MiB
    private static Cache cache = new Cache(httpCacheDirectory, cacheSize);
    private static OkHttpClient client = new OkHttpClient.Builder()
            .addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
            .addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
            .cache(cache)
            .build();

    private static ZhihuApi zhihuApi = null;
    private static final Object monitor = new Object();
    public static ZhihuApi getZhihuApi() {
        synchronized (monitor){
            if (zhihuApi == null) {
                zhihuApi = new Retrofit.Builder()
                        .baseUrl("http://news-at.zhihu.com")
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build().create(ZhihuApi.class);
            }
            return zhihuApi;
        }
    }
}
