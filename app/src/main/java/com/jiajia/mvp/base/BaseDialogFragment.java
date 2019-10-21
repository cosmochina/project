package com.jiajia.mvp.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import com.jiajia.mvp.R;
import com.jiajia.mvp.annotation.ActivityFragmentInject;
import com.jiajia.mvp.ui.widget.ThreePointLoadingView;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.components.support.RxDialogFragment;

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
public abstract class BaseDialogFragment<T extends BasePresenter> extends RxDialogFragment
        implements BaseView, View.OnClickListener {
    // 将代理类通用行为抽出来
    private T mPresenter;

    private View mFragmentRootView;
    private int mContentViewId;
    private Unbinder unbinder;

    private ThreePointLoadingView mLoadingView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (null == mFragmentRootView) {
            if (getClass().isAnnotationPresent(ActivityFragmentInject.class)) {
                ActivityFragmentInject annotation = getClass()
                        .getAnnotation(ActivityFragmentInject.class);
                mContentViewId = annotation.contentViewId();
            } else {
                throw new RuntimeException(
                        "Class must add annotations of ActivityFragmentInitParams.class");
            }
            mPresenter = initPresenter();
            mFragmentRootView = inflater.inflate(mContentViewId, container, false);
            mLoadingView = mFragmentRootView.findViewById(R.id.loading);
            unbinder = ButterKnife.bind(this, mFragmentRootView);
        }

        return mFragmentRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    public T initPresenter() {
        return null;
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mFragmentRootView != null) {
            ViewGroup parent = (ViewGroup) mFragmentRootView.getParent();
            if (null != parent) {
                parent.removeView(mFragmentRootView);
            }
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

    public BaseDialogFragment() {
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
    public void toast(final String msg) {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).toast(msg);
        }
    }

    @Override
    public void toast(int msg) {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).toast(msg);
        }
    }

    @Override
    public void snake(String msg) {
        showSnackbar(msg);
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

    protected <T extends View> T findViewById(int id) {
        return mFragmentRootView.findViewById(id);
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

    @Override
    public <E> LifecycleTransformer<E> bindLifecycle() {
        return this.bindUntilEvent(FragmentEvent.DESTROY_VIEW);
    }
}
