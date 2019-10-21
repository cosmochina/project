package com.jiajia.mvp.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ClassName: ActivityFragmentInject<p>
 * Author: oubowu<p>
 * Fuction: Activity、Fragment初始化的用到的注解<p>
 * CreateDate: 2016/2/15 23:30<p>
 * UpdateUser: <p>
 * UpdateDate: <p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ActivityFragmentInject {

    /**
     * 顶部局的id
     *
     * @return
     */
    int contentViewId() default -1;

    /**
     * 菜单id
     *
     * @return
     */
    int menuId() default -1;

    /**
     * 是否开启侧滑
     *
     * @return
     */
    boolean enableSlidr() default false;


    /**
     * toolbar菜单默认选中项
     *
     * @return
     */
    int menuDefaultCheckedItem() default -1;

    /**
     * 是否自己处理返回键
     */
    boolean handleBackClick() default false;

    /**
     * toolbar的标题id
     *
     * @return
     */
    int toolbarTitle() default -1;

    /**
     * toolbar的标题id
     *
     * @return
     */
    boolean isShowTitle() default false;

    /**
     * toolbar的背景
     *
     * @return
     */
    int toolbarBackgroundDrawableRes() default -1;

    /**
     * toolbar的菜单按钮
     *
     * @return
     */
    int toolbarIndicator() default -1;

    /**
     * 状态栏背景颜色
     *
     * @return
     */
    int statusBackground() default -1;

    /**
     * 文字颜色
     *
     * @return
     */
    int titleTextColor() default -1;

    /**
     * 是否自己处理返回键
     */
    boolean isShowLeftBtn() default true;

    /**
     * 底部导航栏背景颜色
     *
     * @return
     */
    int navigationColor() default -1;

    /**
     * 是否显示状态栏
     *
     * @return
     */
    boolean isShowStatusBar() default true;

    /**
     * 是否显示状态栏灰色图标
     *
     * @return
     */
    boolean isShowDarkStatusBarIcon() default false;

    /**
     * 底部导航栏背景颜色
     *
     * @return
     */
    boolean isShowDarkNavigationBarIcon() default true;
}
