package com.jiajia.mvp.ui.fragment;

import android.annotation.SuppressLint;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jiajia.mvp.annotation.ActivityFragmentInject;
import com.jiajia.mvp.R;
import com.jiajia.mvp.app.App;
import com.jiajia.mvp.base.BaseFragment;
import com.jiajia.mvp.base.BasePresenter;
import com.jiajia.mvp.base.BasePresenterImpl;
import com.jiajia.mvp.callback.CustomWebChromeClient;
import com.jiajia.mvp.callback.CustomWebViewClient;
import com.jiajia.mvp.common.AppConstant;
import com.jiajia.mvp.ui.widget.jsbridge.BridgeWebView;
import com.tencent.smtt.sdk.WebSettings;
import butterknife.BindView;

@ActivityFragmentInject(contentViewId = R.layout.fragment_webview)
public class WebViewFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.webview)
    BridgeWebView mWebView;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;
    private WebViewCallBack mWebViewCallBack;
    private boolean firstLoad;

    public static WebViewFragment newInstance(String url) {
        return newInstance(url, "");
    }

    @Override
    protected BasePresenter initPresenter() {
        return new BasePresenterImpl<>(this);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof WebViewCallBack) {
            mWebViewCallBack = (WebViewCallBack) context;
        }
    }

    public static WebViewFragment newInstance(String url, String title) {
        Bundle args = new Bundle();
        args.putString(AppConstant.URL, url);
        args.putString(AppConstant.TITLE, title);
        WebViewFragment fragment = new WebViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static WebViewFragment newInstance(Bundle bundle) {
        WebViewFragment fragment = new WebViewFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initView() {
        if (getArguments() != null) {
            setToolbarTitle(getArguments().getString(AppConstant.TITLE));
        }
        mRefreshLayout.setOnRefreshListener(this);
        //初始化webview
        initWebView();
        //注入h5需要调用的相关方法
        loadUrl();
    }



    private void loadUrl() {
        String url = getArguments() == null ? null : getArguments().getString(AppConstant.URL);
        if (!TextUtils.isEmpty(url)) {
            mWebView.loadUrl(url);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //将图片调整到适合webview的大小
        webSettings.setUseWideViewPort(true);
        // 缩放至屏幕的大小
        webSettings.setLoadWithOverviewMode(true);
        //开启localstorage
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheMaxSize(1024 * 1024 * 8);
        String appCachePath = App.getContext().getCacheDir().getAbsolutePath();
        webSettings.setAppCachePath(appCachePath);
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);

        //禁止缩放操作
//        webSettings.setSupportZoom(false);
//        webSettings.setBuiltInZoomControls(false);
//        webSettings.setDisplayZoomControls(false);
        //关闭webview中缓存
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        //支持通过JS打开新窗口
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //支持自动加载图片
        webSettings.setLoadsImagesAutomatically(true);
        //设置编码格式
        webSettings.setDefaultTextEncodingName("utf-8");

        mWebView.setWebChromeClient(new CustomWebChromeClient(mWebViewCallBack));
        mWebView.setWebViewClient(new CustomWebViewClient(mWebView, new CustomWebViewClient.LoadCallBack() {
            @Override
            public void onPageStarted() {
                if (firstLoad) {
                    firstLoad = false;
                    showLoadingView();
                }
            }

            @Override
            public void onPageFinished() {
                hideLoadingView();
                mRefreshLayout.setRefreshing(false);
            }
        }));
    }

    public void onBack() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            finishActivity();
        }
    }

    @Override
    public void onRefresh() {
        //下拉刷新 重新加载
        mWebView.reload();
    }

    public interface WebViewCallBack {
        void onGetTitle(String title);
    }

}
