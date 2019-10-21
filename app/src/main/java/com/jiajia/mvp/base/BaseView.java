package com.jiajia.mvp.base;


import androidx.annotation.StringRes;

/**
 * ClassName: BaseView<p>
 * Author:jiajia<p>
 * Fuction: 视图基类<p>
 * CreateDate:2016/2/14 1:41<p>
 * UpdateUser:<p>
 * UpdateDate:<p>
 */
public interface BaseView extends LifecycleProvider {

    void toast(String msg);

    void toast(@StringRes int msg);

    void snake(String msg);

    void showProgress(String content);

    void hideProgress();

    void showLoadingView();

    void hideLoadingView();

    String getStr(@StringRes int res);

}
