package com.jiajia.mvp.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;


/**
 * Toast工具类
 * Created by jiajia on 2016/6/23.
 */
public class ToastUtils {
    public static void toastShort(Context context,String content) {
        if(context==null|| TextUtils.isEmpty(content)){
            return;
        }
        Toast toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void toastLong(Context context,String content) {
        if(context==null|| TextUtils.isEmpty(content)){
            return;
        }
        Toast toast = Toast.makeText(context, content, Toast.LENGTH_LONG);
        toast.show();
    }

    public static void toastShort(Context context,int content) {
        if(context==null){
            return;
        }
        toastShort(context,context.getString(content));
//        IToast toast = ToastCompat.makeText(BaseApplication.getInstance(), BaseApplication.getInstance().getString(content), Toast.LENGTH_SHORT);
//        toast.setGravity(Gravity.CENTER,0,0);
//        toast.show();
    }

    public static void toastLong(Context context,int content) {
        if(context==null){
            return;
        }
        toastLong(context,context.getString(content));
    }
}
