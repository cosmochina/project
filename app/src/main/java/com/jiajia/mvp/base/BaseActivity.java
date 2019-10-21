package com.jiajia.mvp.base;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.snackbar.Snackbar;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.jiajia.mvp.R;
import com.jiajia.mvp.annotation.ActivityFragmentInject;
import com.jiajia.mvp.app.App;
import com.jiajia.mvp.common.AppConstant;
import com.jiajia.mvp.slideback.SlideBackHelper;
import com.jiajia.mvp.slideback.SlideConfig;
import com.jiajia.mvp.slideback.widget.SlideBackLayout;
import com.jiajia.mvp.utils.ActivityUtils;
import com.jiajia.mvp.utils.MeasureUtil;
import com.jiajia.mvp.utils.SpUtil;
import com.jiajia.mvp.utils.ToastUtils;
import com.jiajia.mvp.ui.widget.ThreePointLoadingView;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * ClassName: BaseActivity<p>
 * Author:jiajia<p>
 * Fuction: Activity的基类<p>
 * CreateDate:2016/2/14 18:42<p>
 * UpdateUser:<p>
 * UpdateDate:<p>
 */
public abstract class BaseActivity<T extends BasePresenter> extends RxAppCompatActivity implements View.OnClickListener, BaseView {

    /**
     * 将代理类通用行为抽出来
     */
    protected T mPresenter;

    /**
     * 标示该activity是否可滑动退出,默认false
     */
    protected boolean mEnableSlidr;

    /**
     * 是否显示返回键
     */
    protected boolean mIsShowLeftBtn;

    /**
     * 布局的id
     */
    protected int mContentViewId;
    /**
     * 是否自己处理返回键点击
     */
    protected boolean mHandleBackClick;

    /**
     * 菜单的id
     */
    private int mMenuId;

    /**
     * Toolbar标题
     */
    private int mToolbarTitle;

    /**
     * Toolbar背景图片
     */
    private int mToolbarBackgroundDrawableRes;

    /**
     * Toolbar左侧按钮的样式
     */
    private int mToolbarIndicator;
    /**
     * 状态栏背景色
     */
    private int mStatusBackground;
    /**
     * 是否显示状态栏
     */
    private boolean mIsShowStatusBar;
    /**
     * 控制滑动与否的接口
     */
    protected SlideBackLayout mSlideBackLayout;
    protected MaterialDialog mProgressDialog;
    protected View mContentView;
    protected ThreePointLoadingView mLoadingView;
    private boolean mIsShowDarkStatusBarIcon;
    private boolean mIsShowDarkNavigationBarIcon;
    protected Unbinder unbinder;
    private int mNavigationColor;
    private int mTitleTextColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getClass().isAnnotationPresent(ActivityFragmentInject.class)) {
            ActivityFragmentInject annotation = getClass().getAnnotation(ActivityFragmentInject.class);
            mContentViewId = annotation.contentViewId();
            mEnableSlidr = annotation.enableSlidr();
            mMenuId = annotation.menuId();
            mToolbarTitle = annotation.toolbarTitle();
            mToolbarIndicator = annotation.toolbarIndicator();
            mHandleBackClick = annotation.handleBackClick();
            mStatusBackground = annotation.statusBackground();
            mToolbarBackgroundDrawableRes = annotation.toolbarBackgroundDrawableRes();
            mIsShowLeftBtn = annotation.isShowLeftBtn();
            mIsShowStatusBar = annotation.isShowStatusBar();
            mIsShowDarkStatusBarIcon = annotation.isShowDarkStatusBarIcon();
            mIsShowDarkNavigationBarIcon = annotation.isShowDarkNavigationBarIcon();
            mNavigationColor = annotation.navigationColor();
            mTitleTextColor = annotation.titleTextColor();
        } else {
            throw new RuntimeException("Class must add annotations of ActivityFragmentInitParams.class");
        }
        //强制竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActivityUtils.getInstance().pushActivity(this);
        //初始化Presenter
        mPresenter = initPresenter();
        // 设置侧滑关闭页面，默认不开启侧滑
        initSlidr();
//        adapterFontSize();
        //没有布局文件
        if (mContentViewId != -1) {
            mContentView = LayoutInflater.from(this).inflate(mContentViewId, null);
            setContentView(mContentView);
            unbinder = ButterKnife.bind(this);
            mLoadingView = findViewById(R.id.loading);
        }
        initToolbar();
        initImmersionBar();
        initView();
    }

//    private void adapterFontSize() {
//        Resources res = Resources.getSystem();
//        res.getConfiguration().fontScale = (float) 1.5;
//        res.updateConfiguration(res.getConfiguration(), res.getDisplayMetrics());
//    }

    //沉浸栏设置
    protected void initImmersionBar() {
        ImmersionBar immersionBar = ImmersionBar.with(this);
        if (ImmersionBar.isSupportStatusBarDarkFont()) {
            immersionBar.statusBarDarkFont(mIsShowDarkStatusBarIcon);
        }
        if (ImmersionBar.isSupportNavigationIconDark()) {
            immersionBar.navigationBarDarkIcon(mIsShowDarkNavigationBarIcon)
                    .navigationBarColor(mNavigationColor == -1 ? R.color.background_color : mNavigationColor);
        }
        if (this instanceof BaseFullScreenActivity) {
            //隐藏状态栏
            immersionBar.hideBar(BarHide.FLAG_HIDE_STATUS_BAR);
            //隐藏导航栏
            if (ImmersionBar.hasNavigationBar(this)) {
                immersionBar.hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR);
            }
        }
        immersionBar.init();
    }


    protected void initSlidr() {
        if (mEnableSlidr && !SpUtil.readBoolean(AppConstant.DISABLE_SLIDE)) {
            mSlideBackLayout = SlideBackHelper.attach(this, App.getContext().getActivityHelper(), new SlideConfig.Builder()
                    // 是否侧滑
                    .edgeOnly(true)
                    // 是否会屏幕旋转
                    .rotateScreen(false)
                    // 是否禁止侧滑
                    .lock(false)
                    // 侧滑的响应阈值，0~1，对应屏幕宽度*percent
                    .edgePercent(0.1f)
                    // 关闭页面的阈值，0~1，对应屏幕宽度*percent
                    .slideOutPercent(0.35f).create(), null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.onResume();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }


    protected T initPresenter() {
        return null;
    }

    private void initToolbar() {
        View statusView = findViewById(R.id.status_view);
        if (statusView != null) {
            if (mIsShowStatusBar) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    statusView.getLayoutParams().height = 0;
                } else {
                    statusView.getLayoutParams().height = MeasureUtil.getStatusBarHeight(this);
                    if (mStatusBackground != -1) {
                        statusView.setBackgroundResource(mStatusBackground);
                    }
                }
            } else {
                statusView.setVisibility(View.GONE);
            }
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            // 24.0.0版本后导航图标会有默认的与标题的距离，这里设置去掉
            toolbar.setContentInsetStartWithNavigation(0);
            setSupportActionBar(toolbar);
            //设置返回键是否显示
            setShowLeftBtn(mIsShowLeftBtn);
            //设置标题
            setToolbarTitle(mToolbarTitle == -1 ? "" : getString(mToolbarTitle));
            if (mTitleTextColor != -1) {
                setTitleTextColor(ContextCompat.getColor(this, mTitleTextColor));
            }
            //设置toobar背景颜色
            if (mToolbarBackgroundDrawableRes != -1) {
                setToolbarBackground(ContextCompat.getDrawable(this, mToolbarBackgroundDrawableRes));
            }
            if (mToolbarIndicator != -1) {
                setToolbarIndicator(mToolbarIndicator);
            } else {
                setToolbarIndicator(R.mipmap.ic_back_btn_black);
            }
        }
    }


    public void setShowLeftBtn(boolean isShowLeftBtn) {
        this.mIsShowLeftBtn = isShowLeftBtn;
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(mIsShowLeftBtn);
        }
    }

    protected void setToolbarIndicator(int resId) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(resId);
        }
    }

    protected void setToolbarTitle(String str) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            TextView title = findViewById(R.id.title);
            if (title != null) {
                title.setText(str);
            }
        }
    }

    protected void setTitleTextColor(int color) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            TextView title = findViewById(R.id.title);
            if (title != null) {
                title.setTextColor(color);
            }
        }
    }


    protected void setToolbarTitle(int strId) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }
        TextView title = findViewById(R.id.title);
        if (title != null) {
            title.setText(strId);
        }
    }

    protected void setToolbarBackground(Drawable backgroundDrawable) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(backgroundDrawable);
        }
    }

    protected ActionBar getToolbar() {
        return getSupportActionBar();
    }

    protected View getContentView() {
        return mContentView;
    }

    protected abstract void initView();

    protected void showSnackbar(String msg) {
        Snackbar.make(getContentView(), msg, Snackbar.LENGTH_SHORT).show();
    }

    protected void showSnackbar(int id) {
        Snackbar.make(getContentView(), id, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mMenuId != -1)
            getMenuInflater().inflate(mMenuId, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mHandleBackClick) {
                onClickBack();
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                } else {
                    finish();
                }
            }

        }
        return super.onOptionsItemSelected(item);
    }

    protected void onClickBack() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (this instanceof BaseSplashActivity) {
                return true;
            }
        }
        if (mHandleBackClick) {
            onClickBack();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.onBackPressed();
        } else {
            finish();
            overridePendingTransition(0, R.anim.anim_slide_out);
        }
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * 继承BaseView抽出显示信息通用行为
     *
     * @param msg 信息
     */
    @Override
    public void toast(String msg) {
        if (!TextUtils.isEmpty(msg))
            ToastUtils.toastShort(this, msg);
    }

    @Override
    public void toast(@StringRes int msg) {
        ToastUtils.toastShort(this, msg);
    }

    /**
     * 继承BaseView抽出显示信息通用行为
     *
     * @param msg 信息
     */
    @Override
    public void snake(String msg) {
        if (!TextUtils.isEmpty(msg))
            showSnackbar(msg);
    }

    @Override
    public void showProgress(final String content) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog == null) {
                    mProgressDialog = new MaterialDialog.Builder(BaseActivity.this)
                            .content(content)
                            .progress(true, 100, false)
                            .build();
                } else {
                    mProgressDialog.setContent(content);
                }
                if (!mProgressDialog.isShowing())
                    mProgressDialog.show();
            }
        });
    }

    @Override
    public void hideProgress() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.cancel();
                }
            }
        });
    }

    /**
     * 变换显示的fragment，并自动保存fragment之前的状态。
     *
     * @param fragment    即将要显示的fragment
     * @param fragmentOld 即将要隐藏的fragment
     */
    public void showFragmentAndHideOldFragment(Fragment fragment, Fragment fragmentOld) {
        if (fragment == fragmentOld) {
            return;
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (null != fragmentOld) {
            transaction.hide(fragmentOld);
        }
        transaction.show(fragment);
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void showLoadingView() {
        if (mLoadingView != null) {
            mLoadingView.play();
        }
    }

    @Override
    public void hideLoadingView() {
        if (mLoadingView != null) {
            mLoadingView.stop();
        }
    }

    @Override
    public String getStr(int res) {
        return getString(res);
    }


    public void replaceFragment(@IdRes int containerViewId, Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(containerViewId, fragment)
                .commitAllowingStateLoss();
    }

    public void addFragment(@IdRes int containerViewId, Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(containerViewId, fragment)
                .commitAllowingStateLoss();
    }

    @Override
    public <E> LifecycleTransformer<E> bindLifecycle() {
        return this.bindUntilEvent(ActivityEvent.DESTROY);
    }
}
