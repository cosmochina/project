package com.jiajia.mvp.base;


import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

/**
 * ClassName: BasePresenter<p>
 * Author:jiajia<p>
 * Fuction: 代理的基类<p>
 * CreateDate:2016/2/14 1:45<p>
 * UpdateUser:<p>
 * UpdateDate:<p>
 */
public interface BasePresenter extends LifecycleProvider {

    void onResume();

    void onDestroy();

    //Rxbus注册
    <E> void registerEvent(@NonNull Object tag, @NonNull Class<E> clazz, DisposableObserver<E> observer);

    //绑定事件
    void addDisposable(Disposable d);

}
