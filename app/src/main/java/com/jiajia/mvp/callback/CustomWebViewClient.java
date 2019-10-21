package com.jiajia.mvp.callback;

import android.graphics.Bitmap;

import com.jiajia.mvp.ui.widget.jsbridge.BridgeWebView;
import com.jiajia.mvp.ui.widget.jsbridge.BridgeWebViewClient;
import com.tencent.smtt.sdk.WebView;


public class CustomWebViewClient extends BridgeWebViewClient {
    private LoadCallBack mLoadCallBack;
    public CustomWebViewClient(BridgeWebView webView, LoadCallBack loadCallBack) {
        super(webView);
        this.mLoadCallBack = loadCallBack;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        mLoadCallBack.onPageStarted();
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        mLoadCallBack.onPageFinished();
    }

    public interface LoadCallBack {
        void onPageStarted();
        void onPageFinished();
    }
}
