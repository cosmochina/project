package com.jiajia.mvp.common;

public abstract class AppConstant {
    //应用名
    public static final String APP_NAME = "andall";
    //平台
    public static final String H5_CACHE_CONFIG = "H5_CACHE_CONFIG";
    public static final String USER_CACHE_CONFIG = "H5_CACHE_CONFIG";
    public static final String URL = "url";
    public static final String AUTHORIZATION = "Authorization";
    public static final String DISABLE_SLIDE = "disableSlide";
    public static final String TITLE = "title";
    public static final String CAN_GOBACK = "canGoback";

    public static final long CONNECT_TIME_OUT = 30000;
    public static final long READ_TIME_OUT = 30000;
    public abstract String getBaseUrl();
}
