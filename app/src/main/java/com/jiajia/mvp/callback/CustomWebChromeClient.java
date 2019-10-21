package com.jiajia.mvp.callback;


import com.jiajia.mvp.ui.fragment.WebViewFragment;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;

public class CustomWebChromeClient extends WebChromeClient {
    private WebViewFragment.WebViewCallBack callBack;

    public CustomWebChromeClient(WebViewFragment.WebViewCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        //获取标题
        if (callBack != null) {
            callBack.onGetTitle(title);
        }
    }
}
