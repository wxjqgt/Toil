package com.weibo.toil.utils;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public class RxBus {

    private final Subject<Object, Object> bus;

    private RxBus() {
        bus = new SerializedSubject<>(PublishSubject.create());
    }

    // 单例RxBus
    public static RxBus getDefault() {
        return RxBusHelper.rxBus;
    }

    private static class RxBusHelper{
        public static final RxBus rxBus = new RxBus();
    }

    public void send(Object o) {
        bus.onNext(o);
    }

    public <T> Observable<T> toObservable(Class<T> eventClass) {
        return bus.ofType(eventClass);
    }

    /**
     * 判断是否有订阅者
     */
    public boolean hasObservers() {
        return getDefault().hasObservers();
    }

}
