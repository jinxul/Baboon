package com.givekesh.baboon;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ObservableWebView;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.givekesh.baboon.Utils.FeedProvider;
import com.givekesh.baboon.Utils.Feeds;
import com.givekesh.baboon.Utils.Interfaces;
import com.givekesh.baboon.Utils.Utils;
import com.nineoldandroids.view.ViewHelper;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

public class SelectedPostActivity extends AppCompatActivity implements ObservableScrollViewCallbacks {

    private ImageView mImageView;
    private Toolbar mToolbarView;
    private ObservableScrollView mScrollView;
    private ObservableWebView content;
    private int mParallaxImageHeight;
    private Feeds feed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_post);

        feed = getIntent().getParcelableExtra("post_parcelable");
        mImageView = (ImageView) findViewById(R.id.post_image);
        setupToolbar();

        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);

        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);

        content = (ObservableWebView) findViewById(R.id.post_content);
        setUpWebViewDefaults();
        setUseTextAutoSize();
        content.loadDataWithBaseURL("file:///android_asset/", getHtmlData(), "text/html", "UTF-8", null);

        Glide.with(this)
                .load(feed.getContentImage())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .error(R.mipmap.ic_launcher)
                .crossFade()
                .into(mImageView);
    }

    private void setupToolbar() {
        Utils utils = new Utils(this);
        mToolbarView = (Toolbar) findViewById(R.id.toolbar);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, ContextCompat.getColor(this, R.color.colorPrimary)));
        mToolbarView.setTitle(Html.fromHtml(feed.getTitle()));
        mToolbarView.setNavigationIcon(utils.getMaterialIcon(MaterialDrawableBuilder.IconValue.KEYBOARD_BACKSPACE, Color.WHITE));
        mToolbarView.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                supportFinishAfterTransition();
            }
        });
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onScrollChanged(mScrollView.getCurrentScrollY(), false, false);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        int baseColor = ContextCompat.getColor(this, R.color.colorPrimary);
        float alpha = Math.min(1, (float) scrollY / mParallaxImageHeight);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
        ViewHelper.setTranslationY(mImageView, scrollY / 2);
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }


    private void setUpWebViewDefaults() {
        WebSettings settings = content.getSettings();

        //settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        settings.setJavaScriptEnabled(true);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB)
            settings.setDisplayZoomControls(false);

        content.setWebChromeClient(new WebChromeClient());
        content.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.matches("http?://(www\\.)?baboon.ir/([-a-zA-Z0-9@:%_\\+.~#?&/=]*)")) {
                    final ProgressDialog dialog = ProgressDialog.show(SelectedPostActivity.this, null, getString(R.string.first_load), true);
                    new FeedProvider(SelectedPostActivity.this).getSinglePost(url, new Interfaces.SinglePostCallback() {

                        @Override
                        public void onSuccess(Feeds post) {
                            dialog.dismiss();
                            Intent intent = new Intent(SelectedPostActivity.this, SelectedPostActivity.class);
                            intent.putExtra("post_parcelable", post);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(String error) {
                            Toast.makeText(SelectedPostActivity.this, R.string.not_found, Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    });
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                }
                return true;
            }
        });
    }

    private void setUseTextAutoSize() {
        WebSettings settings = content.getSettings();

        WebSettings.LayoutAlgorithm layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING;

        settings.setLayoutAlgorithm(layoutAlgorithm);
    }

    private String getHtmlData() {
        return "<HTML><head>" +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"baboon-main.css\" />" +
                "<script src=\"https://cdn.rawgit.com/google/code-prettify/master/loader/run_prettify.js?skin=sons-of-obsidian\" defer=\"defer\"></script>" +
                "</head>" +
                "<body>" +
                feed.getPost() +
                "</body></HTML>";
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
        super.onBackPressed();
    }
}
