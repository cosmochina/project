package com.jiajia.mvp.callback;


/**
 * ClassName: BaseRequestCallback<p>
 * Author:jiajia<p>
 * Fuction: 网络请求监听基类<p>
 * CreateDate:2016/2/14 1:48<p>
 * UpdateUser:<p>
 * UpdateDate:<p>
 */
public interface RequestCallback<T,E> {

    /**
     * 请求之前调用
     */
    void beforeRequest();

    /**
     * 请求错误调用
     *
     * @param error 错误信息
     */
    void requestError(E error);

    /**
     * 请求完成调用
     */
    void requestComplete();

    /**
     * 请求成功调用
     *
     * @param data 数据
     */
    void requestSuccess(T data);
}
