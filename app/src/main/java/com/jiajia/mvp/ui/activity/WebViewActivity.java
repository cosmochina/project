package com.jiajia.mvp.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;

import com.jiajia.mvp.R;
import com.jiajia.mvp.annotation.ActivityFragmentInject;
import com.jiajia.mvp.base.BaseActivity;
import com.jiajia.mvp.common.AppConstant;
import com.jiajia.mvp.ui.fragment.WebViewFragment;

@ActivityFragmentInject(contentViewId = R.layout.activity_webview,
        handleBackClick = true
)
public class WebViewActivity extends BaseActivity {
    private WebViewFragment mWebViewFragment;
    private boolean canGoBack;

    @Override
    protected void initView() {
        //必传 url
        Bundle bundle = getIntent().getExtras();
        if (bundle == null || TextUtils.isEmpty(bundle.getString(AppConstant.URL))) {
            throw new IllegalArgumentException(getString(R.string.url_empty_error));
        }
        canGoBack = bundle.getBoolean(AppConstant.CAN_GOBACK, true);
        replaceFragment(R.id.content, mWebViewFragment = WebViewFragment.newInstance(bundle));
    }

    @Override
    protected void onClickBack() {
        if (canGoBack && mWebViewFragment != null) {
            mWebViewFragment.onBack();
        } else {
            onBackPressed();
        }
    }
}
