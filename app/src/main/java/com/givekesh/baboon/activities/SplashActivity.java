package com.givekesh.baboon.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.givekesh.baboon.R;
import com.givekesh.baboon.Utils.FeedProvider;
import com.givekesh.baboon.Utils.Posts.POJOS.Feeds;
import com.givekesh.baboon.Utils.Interfaces;
import com.givekesh.baboon.Utils.Utils;

import java.util.ArrayList;


public class SplashActivity extends AppCompatActivity implements Interfaces.VolleyCallback, Interfaces.SinglePostCallback {

    private View loading;
    private String path = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        loading = findViewById(R.id.loading_layout);
        final Intent intent = getIntent();

        if (Intent.ACTION_VIEW.equals(intent.getAction()))
            path = intent.getData().getPath();

        getData();
    }

    @Override
    public void onPreRequest() {

    }

    @Override
    public void onSuccess(ArrayList<Feeds> result) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putParcelableArrayListExtra("main_feed", result);
        intent.putExtra("category", path.replace("/tutorials/", ""));
        startActivity(intent);
        finish();
    }

    @Override
    public void onSuccess(Feeds post) {
        Intent intent = new Intent(this, SelectedPostActivity.class);
        intent.putExtra("post_parcelable", post);
        startActivity(intent);
        finish();
    }

    @Override
    public void onFailure(String error) {
        showSnack(getString(R.string.connection_failed));
    }


    private void showSnack(String string) {
        loading.setVisibility(View.GONE);
        final View view = findViewById(R.id.splash_coordinator);
        new Utils(this).showSnack(view, string, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData();
            }
        });
    }

    private void getData() {
        loading.setVisibility(View.VISIBLE);
        FeedProvider mFeedProvider = new FeedProvider(this);
        if (path.equalsIgnoreCase("") || path.matches("/tutorials/.*")) {
            String category = path.replace("/tutorials/", "");
            mFeedProvider.getFeedsArrayList(1, category, null, this);
        } else {
            mFeedProvider.getSinglePost("http://baboon.ir" + path, this);
        }
    }
}
