package com.jiajia.mvp.app;

import android.app.Application;
import com.google.gson.Gson;
import com.jiajia.mvp.BuildConfig;
import com.jiajia.mvp.slideback.ActivityHelper;
import com.socks.library.KLog;

/**
 * Created by jiajia on 2018/10/1.
 */

public class App extends Application {
    public static Gson gson = new Gson() ;
    private static App sApplicationContext;
    private ActivityHelper mActivityHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        KLog.init(BuildConfig.DEBUG,"KLOG");
        sApplicationContext = this;
        mActivityHelper = new ActivityHelper();
        registerActivityLifecycleCallbacks(mActivityHelper);
    }


    public ActivityHelper getActivityHelper() {
        return mActivityHelper;
    }




    // 获取ApplicationContext
    public static App getContext() {
        return sApplicationContext;
    }

}
