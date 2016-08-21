package com.weibo.toil.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.weibo.toil.R;
import com.weibo.toil.bean.itHome.ItHomeArticle;
import com.weibo.toil.bean.itHome.ItHomeItem;
import com.weibo.toil.presenter.IItHomeArticlePresenter;
import com.weibo.toil.presenter.impl.ItHomeArticlePresenterImpl;
import com.weibo.toil.ui.iView.IItHomeArticle;
import com.weibo.toil.utils.Config;
import com.weibo.toil.utils.WebUtil;

import butterknife.BindView;

public class ItHomeActivity extends BaseActivity implements IItHomeArticle {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.wv_it)
    WebView wvIt;
    @BindView(R.id.pb_web)
    ProgressBar pbWeb;
    @BindView(R.id.nest)
    NestedScrollView nest;
    @BindView(R.id.fabButton)
    FloatingActionButton fabButton;

    private ItHomeItem itHomeItem;
    private IItHomeArticlePresenter mIItHomeArticlePresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ithome;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initView();
        getData();
    }

    private void initData() {
        itHomeItem = getIntent().getParcelableExtra("item");
        mIItHomeArticlePresenter = new ItHomeArticlePresenterImpl(this);
    }

    private void initView() {
        toolbar.setTitle(itHomeItem.getTitle());
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setToolBar(fabButton, toolbar, true, true, null);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nest.smoothScrollTo(0, 0);
            }
        });
        setWebView();
    }

    private void getData() {
        mIItHomeArticlePresenter.getItHomeArticle(itHomeItem.getNewsid());
    }

    private void setWebView() {
        WebSettings settings = wvIt.getSettings();
        settings.setDomStorageEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setJavaScriptEnabled(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAppCachePath(getCacheDir().getAbsolutePath() + "/webViewCache");
        settings.setAppCacheEnabled(true);
        settings.setLoadWithOverviewMode(true);
        wvIt.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        wvIt.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (pbWeb != null) {//修复未加载完成，用户返回会崩溃
                    if (newProgress == 100) {
                        pbWeb.setVisibility(View.GONE);
                    } else {
                        if (pbWeb.getVisibility() == View.GONE) {
                            pbWeb.setVisibility(View.VISIBLE);
                        }
                        pbWeb.setProgress(newProgress);
                    }
                }
                super.onProgressChanged(view, newProgress);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && wvIt.canGoBack()) {
            wvIt.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, itHomeItem.getTitle() + " http://ithome.com" + itHomeItem.getUrl() + getString(R.string.share_tail));
            shareIntent.setType("text/plain");
            //设置分享列表的标题，并且每次都显示分享列表
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
        }
        if (item.getItemId() == R.id.action_use_browser) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://ithome.com" + itHomeItem.getUrl())));
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (wvIt != null) {
            ((ViewGroup) wvIt.getParent()).removeView(wvIt);
            wvIt.destroy();
            wvIt = null;
        }
        mIItHomeArticlePresenter.unsubcrible();
    }

    @Override
    public void showError(String error) {
        Snackbar.make(wvIt, getString(R.string.common_loading_error) + error, Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.comon_retry), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        }).show();
    }

    @Override
    public void showItHomeArticle(ItHomeArticle itHomeArticle) {
        if (TextUtils.isEmpty(itHomeArticle.getDetail())) {
            wvIt.loadUrl(itHomeItem.getUrl());
        } else {
            String data = WebUtil.buildHtmlForIt(itHomeArticle.getDetail(), Config.isNight);
            wvIt.loadDataWithBaseURL(WebUtil.BASE_URL, data, WebUtil.MIME_TYPE, WebUtil.ENCODING, itHomeItem.getUrl());
        }
    }
}
