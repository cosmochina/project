package com.jiajia.mvp.base;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.gyf.immersionbar.ImmersionBar;
import com.jiajia.mvp.R;
import com.jiajia.mvp.annotation.ActivityFragmentInject;
import com.jiajia.mvp.app.App;
import com.jiajia.mvp.utils.MeasureUtil;
import com.jiajia.mvp.utils.ToastUtils;
import com.jiajia.mvp.ui.widget.ThreePointLoadingView;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.components.support.RxFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * ClassName: BaseFragment<p>
 * Author:jiajia<p>
 * Fuction: Fragment基类<p>
 * CreateDate:2016/2/14 19:52<p>
 * UpdateUser:<p>
 * UpdateDate:<p>
 */
public abstract class BaseFragment<T extends BasePresenter> extends RxFragment
        implements BaseView, View.OnClickListener {

    // 将代理类通用行为抽出来
    protected T mPresenter;
    private Unbinder unbinder;

    /**
     * 菜单的id
     */
    private int mMenuId;

    private View mFragmentRootView;
    private int mToolbarTitle;
    private ThreePointLoadingView mLoadingView;
    /**
     * 是否显示返回键
     */
    private boolean mIsShowLeftBtn;
    /**
     * 布局的id
     */
    private int mContentViewId;
    /**
     * 是否自己处理返回键点击
     */
    private boolean mHandleBackClick;
    /**
     * Toolbar背景图片
     */
    private int mToolbarBackgroundDrawableRes;

    /**
     * 默认选中的菜单项
     */
    private int mMenuDefaultCheckedItem;

    /**
     * Toolbar左侧按钮的样式
     */
    private int mToolbarIndicator;
    /**
     * 状态栏背景色
     */
    private int mStatusBackground;
    private boolean mIsShowStatusBar;
    private boolean mIsShowDarkStatusBarIcon;
    private ActionBar actionBar = null;
    private int mNavigationColor;
    private boolean mIsShowDarkNavigationBarIcon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (null == mFragmentRootView) {
            if (getClass().isAnnotationPresent(ActivityFragmentInject.class)) {
                ActivityFragmentInject annotation = getClass()
                        .getAnnotation(ActivityFragmentInject.class);
                mContentViewId = annotation.contentViewId();
                mToolbarTitle = annotation.toolbarTitle();
                mMenuId = annotation.menuId();
                mToolbarIndicator = annotation.toolbarIndicator();
                mMenuDefaultCheckedItem = annotation.menuDefaultCheckedItem();
                mHandleBackClick = annotation.handleBackClick();
                mStatusBackground = annotation.statusBackground();
                mToolbarBackgroundDrawableRes = annotation.toolbarBackgroundDrawableRes();
                mIsShowLeftBtn = annotation.isShowLeftBtn();
                mIsShowStatusBar = annotation.isShowStatusBar();
                mNavigationColor = annotation.navigationColor();
                mIsShowDarkStatusBarIcon = annotation.isShowDarkStatusBarIcon();
                mIsShowDarkNavigationBarIcon = annotation.isShowDarkNavigationBarIcon();
            } else {
                throw new RuntimeException(
                        "Class must add annotations of ActivityFragmentInitParams.class");
            }
            mPresenter = initPresenter();
            if (mContentViewId != -1) {
                mFragmentRootView = inflater.inflate(mContentViewId, container, false);
                mLoadingView = mFragmentRootView.findViewById(R.id.loading);
            }
            unbinder = ButterKnife.bind(this, mFragmentRootView);
            initToolbar();
            initView();
            initImmersionBar();
        }

        return mFragmentRootView;
    }


    private void initToolbar() {
        // 针对父布局非DrawerLayout的状态栏处理方式
        // 设置toolbar上面的View实现类状态栏效果，这里是因为状态栏设置为透明的了，而默认背景是白色的，不设的话状态栏处就是白色
        View statusView = findViewById(R.id.status_view);
        if (statusView != null) {
            if (mIsShowStatusBar) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    statusView.getLayoutParams().height = 0;
                } else {
                    statusView.getLayoutParams().height = MeasureUtil.getStatusBarHeight(getActivity());
                    if (mStatusBackground != -1) {
                        statusView.setBackgroundResource(mStatusBackground);
                    }
                }
            } else {
                statusView.setVisibility(View.GONE);
            }
        }

        Toolbar toolbar = mFragmentRootView.findViewById(R.id.toolbar);
        if (toolbar != null) {
            setHasOptionsMenu(true);
            // 24.0.0版本后导航图标会有默认的与标题的距离，这里设置去掉
            toolbar.setContentInsetStartWithNavigation(0);
            if (getActivity() != null) {
                ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            }
            if (getActionBar() != null) {
                getActionBar().setDisplayHomeAsUpEnabled(mIsShowLeftBtn);
            }
            setToolbarTitle(mToolbarTitle == -1 ? "" : getString(mToolbarTitle));
            if (mToolbarBackgroundDrawableRes != -1) {
                setToolbarBackground(ContextCompat.getDrawable(App.getContext(), mToolbarBackgroundDrawableRes));
            }
            setToolbarIndicator(mToolbarIndicator != -1 ? mToolbarIndicator : R.mipmap.ic_back_btn_white);
        }
    }

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
        immersionBar.init();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mHandleBackClick) {
                onClickBack();
            } else {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onClickBack() {

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        if (mMenuId != -1) {
            menu.clear();
            inflater.inflate(mMenuId, menu);
        }
    }

    //设置返回键图标
    protected void setToolbarIndicator(int resId) {
        if (getActionBar() != null) {
            getActionBar().setHomeAsUpIndicator(resId);
        }
    }

    protected void setToolbarBackground(Drawable backgroundDrawable) {
        if (getActionBar() != null) {
            getActionBar().setBackgroundDrawable(backgroundDrawable);
        }
    }

    protected ActionBar getActionBar() {
        if (actionBar == null) {
            if (getActivity() != null && getActivity() instanceof AppCompatActivity) {
                actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            }
        }
        return actionBar;
    }

    public void setToolbarTitle(int res) {
        View toolbar = findViewById(R.id.toolbar);
        if (toolbar == null) {
            return;
        }
        if (res != -1) {
            TextView titleText = (TextView) findViewById(R.id.title);
            if (titleText != null) {
                titleText.setText(res);
            }
        }
    }

    public void setToolbarTitle(String title) {
        View toolbar = findViewById(R.id.toolbar);
        if (toolbar == null) {
            return;
        }
        if (title != null) {
            TextView titleText = (TextView) findViewById(R.id.title);
            if (titleText != null) {
                titleText.setText(title);
            }
        }
    }

    public void setTitleVisible(boolean visible) {
        View status = findViewById(R.id.status_view);
        View toolbar = findViewById(R.id.toolbar);
        if (status != null)
            status.setVisibility(visible ? View.VISIBLE : View.GONE);
        if (toolbar != null)
            toolbar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.onResume();
        }
    }

    //初始化Presenter
    protected T initPresenter() {
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ViewGroup parent = (ViewGroup) mFragmentRootView.getParent();
        if (null != parent) {
            parent.removeView(mFragmentRootView);
        }
    }

    @Override
    public void onDestroy() {
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }

    public BaseFragment() {
    }

    protected abstract void initView();


    protected void showSnackbar(String msg) {
        Snackbar.make(mFragmentRootView, msg, Snackbar.LENGTH_SHORT).show();
    }

    protected void showSnackbar(int id) {
        Snackbar.make(mFragmentRootView, id, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * 继承BaseView抽出显示信息通用行为
     *
     * @param msg
     */
    @Override
    public void toast(String msg) {
        ToastUtils.toastShort(getActivity(), msg);
    }


    @Override
    public void toast(@StringRes int msg) {
        ToastUtils.toastShort(getActivity(), msg);
    }

    @Override
    public void snake(String msg) {
        showSnackbar(msg);
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
    public void showProgress(String content) {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).showProgress(content);
        }
    }

    @Override
    public void hideProgress() {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).hideProgress();
        }
    }

    @Override
    public void onClick(View v) {

    }

    protected View findViewById(int id) {
        return mFragmentRootView.findViewById(id);
    }

    @Override
    public String getStr(int res) {
        return getString(res);
    }

    protected void finishActivity() {
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
    @Override
    public <E> LifecycleTransformer<E> bindLifecycle() {
        return this.bindUntilEvent(FragmentEvent.DESTROY_VIEW);
    }
}
