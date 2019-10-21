package com.jiajia.mvp.base;

import com.trello.rxlifecycle2.LifecycleTransformer;

public interface LifecycleProvider {
    <E> LifecycleTransformer<E> bindLifecycle();
}
