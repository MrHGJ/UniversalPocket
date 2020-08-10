package com.hgj.universal.pocket.webview;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.content.ContentProvider;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.hgj.universal.pocket.R;

public class ShowWebViewActivity extends AppCompatActivity {
    @BindView(R.id.web_view)
    WebView mWebView;
    @BindView(R.id.video_container)
    FrameLayout videoContainer;
    @BindView(R.id.loading_view)
    SpinKitView loadingView;
    @BindView(R.id.tv_title)
    TextView mtitle;
    @BindView(R.id.tv_beginLoading)
    TextView beginLoading;
    @BindView(R.id.tv_endLoading)
    TextView endLoading;
    @BindView(R.id.tv_Loading)
    TextView loading;
    @BindView(R.id.img_close_button)
    ImageView btClose;

    private WebSettings mWebSettings;
    private String url;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_show_web_view);
        mContext = this;
        ButterKnife.bind(this);
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        setTipViewGone();
        setWebView();
        initListener();
    }

    private void setTipViewGone() {
        mtitle.setVisibility(View.GONE);
        beginLoading.setVisibility(View.GONE);
        endLoading.setVisibility(View.GONE);
        loading.setVisibility(View.GONE);
    }

    private void setWebView() {
        loadingView.setVisibility(View.VISIBLE);
        mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        //允许加载视频
        mWebSettings.setBuiltInZoomControls(true);
        mWebSettings.setDisplayZoomControls(false);
        mWebSettings.setSupportZoom(true);
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setDatabaseEnabled(true);
        mWebSettings.setPluginState(WebSettings.PluginState.ON);
        mWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);//播放视频
        mWebView.loadUrl(url);
        //设置不用系统浏览器打开,直接显示在当前Webview
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 以"http","https"开头的url在本页用webview进行加载，其他链接进行跳转
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    view.loadUrl(url);
                    return true;
                } else {
//                    Intent intent = new Intent();
//                    intent.setData(Uri.parse(url));
//                    startActivity(intent);
                    //如果不需要其他对点击链接事件的处理返回true，否则返回false
                    return true;
                }

            }

            //设置加载前的函数
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                System.out.println("开始加载了");
                beginLoading.setText("开始加载了");

            }

            //设置结束加载函数
            @Override
            public void onPageFinished(WebView view, String url) {
                //防止onPageFinished中的方法被多次调用
                if(mWebView.getProgress()==100){
                    endLoading.setText("结束加载了");
                }
            }
        });

        //设置WebChromeClient类
        mWebView.setWebChromeClient(new WebChromeClient() {
            //获取网站标题
            @Override
            public void onReceivedTitle(WebView view, String title) {
                System.out.println("标题在这里");
                mtitle.setText(title);
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                super.onShowCustomView(view, callback);
                mWebView.setVisibility(View.GONE);
                btClose.setVisibility(View.GONE);
                videoContainer.setVisibility(View.VISIBLE);
                videoContainer.addView(view);
                //设置横屏
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }

            @Override
            public void onHideCustomView() {
                super.onHideCustomView();
                mWebView.setVisibility(View.VISIBLE);
                btClose.setVisibility(View.VISIBLE);
                videoContainer.removeAllViews();
                videoContainer.setVisibility(View.GONE);
                //设置竖屏
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }

            //获取加载进度
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100) {
                    String progress = newProgress + "%";
                    loading.setText(progress);
                } else if (newProgress == 100) {
                    String progress = newProgress + "%";
                    loading.setText(progress);
                    loadingView.setVisibility(View.GONE);
                }
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                return super.onJsConfirm(view, url, message, result);
            }
        });
    }

    //点击返回上一页面而不是退出浏览器
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                /** 回退键 事件处理 优先级:视频播放全屏-网页回退-关闭页面 */
                if (videoContainer.getVisibility() == View.VISIBLE) {
                    //设置竖屏
//                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                    mWebView.setVisibility(View.VISIBLE);
//                    btClose.setVisibility(View.VISIBLE);
//                    videoContainer.removeAllViews();
//                    videoContainer.setVisibility(View.GONE);
                    return true;
                } else if (mWebView.canGoBack()) {
                    mWebView.goBack();
                    return true;
                } else {
                    finish();
                    return true;
                }
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    //销毁Webview
    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();

            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    private void initListener() {
        btClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(mContext);
                builder.setTitle("确定关闭当前网页吗？");
                builder.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                builder.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //...To-do
                            }
                        });
                // 显示
                AlertDialog dialog = builder.create();
                Window window = dialog.getWindow();
                window.setGravity(Gravity.CENTER);
                window.setBackgroundDrawable(getResources().getDrawable(R.drawable.paint_dialog_background));
                dialog.show();
            }
        });
    }
}
