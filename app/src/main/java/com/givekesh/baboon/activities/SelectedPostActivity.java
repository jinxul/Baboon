package com.givekesh.baboon.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dd.processbutton.iml.ActionProcessButton;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ObservableWebView;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.givekesh.baboon.R;
import com.givekesh.baboon.Utils.Comments.CommentsAdapter;
import com.givekesh.baboon.Utils.Comments.POJOS.Comment;
import com.givekesh.baboon.Utils.FeedProvider;
import com.givekesh.baboon.Utils.Posts.POJOS.Feeds;
import com.givekesh.baboon.Utils.Interfaces;
import com.nineoldandroids.view.ViewHelper;


import java.util.ArrayList;

public class SelectedPostActivity extends AppCompatActivity implements ObservableScrollViewCallbacks {

    private ImageView mImageView;
    private Toolbar mToolbarView;
    private ObservableScrollView mScrollView;
    private ObservableWebView content;
    private int mParallaxImageHeight;
    private Feeds feed;
    private ActionProcessButton loadComments;
    private FrameLayout customViewContainer;
    private View mCustomView;
    private WebChromeClient.CustomViewCallback customViewCallback;


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
        customViewContainer = (FrameLayout) findViewById(R.id.video_fullscreen);

        setUpWebViewDefaults();
        setUseTextAutoSize();
        content.loadDataWithBaseURL("file:///android_asset/", getHtmlData(), "text/html", "UTF-8", null);

        Glide.with(this)
                .load(feed.getContentImage())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .error(R.mipmap.ic_launcher)
                .crossFade()
                .into(mImageView);

        loadComments = (ActionProcessButton) findViewById(R.id.show_comments);
        final RecyclerView comments = (RecyclerView) findViewById(R.id.comment_list);
        comments.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        loadComments.setMode(ActionProcessButton.Mode.ENDLESS);
        loadComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadComments.setProgress(1);
                new FeedProvider(SelectedPostActivity.this).getFeedsArrayList(feed.getId(), new Interfaces.CommentsCallBack() {
                    @Override
                    public void onSuccess(ArrayList<Comment> result) {
                        loadComments.setProgress(0);
                        loadComments.setVisibility(View.GONE);
                        comments.setAdapter(new CommentsAdapter(result, SelectedPostActivity.this));

                    }

                    @Override
                    public void onFailure(String error) {
                        loadComments.setErrorText(error);
                        loadComments.setProgress(-1);
                    }
                });
            }
        });
    }

    private void setupToolbar() {
        mToolbarView = (Toolbar) findViewById(R.id.toolbar);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, ContextCompat.getColor(this, R.color.colorPrimary)));
        mToolbarView.setTitle(Html.fromHtml(feed.getTitle()));
        mToolbarView.setNavigationIcon(VectorDrawableCompat.create(getResources(), R.drawable.ic_keyboard_backspace, null));
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
        settings.setAppCacheEnabled(true);


        settings.setDisplayZoomControls(false);

        content.setWebChromeClient(new mWebChromeClient());
        content.setWebViewClient(new mWebViewClient());
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

    private class mWebChromeClient extends WebChromeClient {
        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            mCustomView = view;
            content.setVisibility(View.GONE);
            customViewContainer.setVisibility(View.VISIBLE);
            customViewContainer.addView(view);
            customViewCallback = callback;
        }

        @Override
        public void onHideCustomView() {
            super.onHideCustomView();
            if (mCustomView == null)
                return;

            content.setVisibility(View.VISIBLE);
            customViewContainer.setVisibility(View.GONE);

            mCustomView.setVisibility(View.GONE);

            customViewContainer.removeView(mCustomView);
            customViewCallback.onCustomViewHidden();

            mCustomView = null;
        }
    }

    private class mWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.matches("http?://(www\\.)?baboon.ir/([-a-zA-Z0-9@:%_+.~#?&/=]*)")) {
                final ProgressDialog dialog = ProgressDialog.show(SelectedPostActivity.this, null, getString(R.string.first_load), true);
                if (url.contains("/tutorials/")) {
                    String category = url.replaceAll("(http://)?(www\\.)?baboon.ir/tutorials/", "").replaceAll("/", "");
                    Intent intent = new Intent(SelectedPostActivity.this, MainActivity.class);
                    intent.putExtra("category", category);
                    setResult(10001, intent);
                    dialog.dismiss();
                    finish();
                } else {
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
                }
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            loadComments.setVisibility(View.VISIBLE);
        }
    }
}
