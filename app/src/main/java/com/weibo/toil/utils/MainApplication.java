package com.weibo.toil.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rx.Subscription;

public class MainApplication extends Application {

    public static ExecutorService service = null;
    public static SharedPreferences sp = null;
    public static MainApplication microApplication = null;
    public static List<Subscription> subscriptions = null;

    @Override
    public void onCreate() {
        super.onCreate();
        service = Executors.newCachedThreadPool();
        sp = getSharedPreferences("Toil",MODE_PRIVATE);
        microApplication = this;
        subscriptions = new ArrayList<>();
    }

    public static ExecutorService getService(){
        return service;
    }

    public static void shutDownService(){
        if (service != null && !service.isShutdown()){
            service.shutdown();
            service = null;
        }
    }

    public static boolean addToList(Subscription...subscription){
        boolean isAdd = true;
        for (Subscription s:subscription) {
            isAdd = subscriptions.add(s);
        }
        return isAdd;
    }

    public static void clearList(){
        for (int i = subscriptions.size() - 1;i < subscriptions.size();i--){
            if (i < 0){return;}
            Subscription subscription = subscriptions.get(i);
            if (!subscription.isUnsubscribed()){
                subscription.unsubscribe();
            }
        }
        if (!subscriptions.isEmpty()){
            subscriptions.clear();
        }
    }

    public static Context getContext(){
        return microApplication;
    }
}
