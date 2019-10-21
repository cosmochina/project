package com.jiajia.mvp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.jiajia.mvp.app.App;
import com.jiajia.mvp.common.AppConstant;

public class SpUtil {

    public static String readString(String key) {
        return getSharedPreferences().getString(key, "");
    }

    public static void writeString(String key, String value) {
        getSharedPreferences().edit().putString(key, value).apply();
    }

    public static boolean readBoolean(String key) {
        return getSharedPreferences().getBoolean(key, false);
    }

    public static void writeBoolean(String key, boolean value) {
        getSharedPreferences().edit().putBoolean(key, value).apply();
    }

    public static int readInt(String key) {
        return getSharedPreferences().getInt(key, 0);
    }

    public static int readInt(String key, int value) {
        return getSharedPreferences().getInt(key, value);
    }

    public static void writeInt(String key, int value) {
        getSharedPreferences().edit().putInt(key, value).apply();
    }

    public static long readLong(String key) {
        return getSharedPreferences().getLong(key, 0);
    }

    public static void writeLong(String key, long value) {
        getSharedPreferences().edit().putLong(key, value).apply();
    }

    public static void remove(String key) {
        getSharedPreferences().edit().remove(key).apply();
    }

    public static void removeAll() {
        getSharedPreferences().edit().clear().apply();
    }

    public static SharedPreferences getSharedPreferences() {
        return App.getContext()
                .getSharedPreferences(AppConstant.APP_NAME, Context.MODE_PRIVATE);
    }
    public static SharedPreferences getH5SharedPreferences() {
        return App.getContext()
                .getSharedPreferences(AppConstant.H5_CACHE_CONFIG, Context.MODE_PRIVATE);
    }
    public static SharedPreferences getUserSharedPreferences() {
        return App.getContext()
                .getSharedPreferences(AppConstant.USER_CACHE_CONFIG, Context.MODE_PRIVATE);
    }

    public static void putH5String(String key,String value){
        getH5SharedPreferences().edit().putString(key,value).apply();
    }
    public static String getH5String(String key){
        return getH5SharedPreferences().getString(key,"");
    }
    public static void removeH5Value(String key){
        getH5SharedPreferences().edit().remove(key).apply();
    }
    public static void clearH5Cache(){
        getH5SharedPreferences().edit().clear().apply();
    }

    public static void putUserString(String key,String value){
        getUserSharedPreferences().edit().putString(key,TextUtils.isEmpty(value)?"":value).apply();
    }
    public static String getUserString(String key){
        return getUserSharedPreferences().getString(key,"");
    }
    public static void putUserBoolean(String key,boolean value){
        getUserSharedPreferences().edit().putBoolean(key,value).apply();
    }
    public static boolean getUserBoolean(String key){
        return getUserSharedPreferences().getBoolean(key,true);
    }
    public static void clearUser(){
        getUserSharedPreferences().edit().clear().apply();
    }

}
