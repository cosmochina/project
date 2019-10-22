package com.jiajia.mvp.base;


public class BaseLifecycleModel {
    protected LifecycleProvider provider;
    public BaseLifecycleModel(LifecycleProvider provider) {
        this.provider = provider;
    }
}
