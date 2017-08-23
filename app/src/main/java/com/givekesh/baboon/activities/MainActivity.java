package com.givekesh.baboon.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.givekesh.baboon.CustomViews.recyclerView;
import com.givekesh.baboon.R;
import com.givekesh.baboon.Utils.FeedProvider;
import com.givekesh.baboon.Utils.Posts.POJOS.Feeds;
import com.givekesh.baboon.Utils.Posts.FeedsAdapter;
import com.givekesh.baboon.Utils.Interfaces;
import com.givekesh.baboon.Utils.MainMenu;
import com.givekesh.baboon.Utils.Utils;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;

import java.util.ArrayList;
import java.util.List;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;


public class MainActivity extends AppCompatActivity implements Interfaces.VolleyCallback, Interfaces.OnNavClickListener {

    private FlowingDrawer mLeftDrawerLayout;
    private Utils utils;
    private ArrayList<Feeds> mFeedsArrayList = new ArrayList<>();
    private WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;
    private FeedsAdapter mAdapter;
    private FeedProvider mFeedProvider;
    private recyclerView recyclerView;

    private boolean isLoadingMore = false;
    private boolean isSwipeRefresh = false;
    private boolean isMenuSelected = false;

    private String category = null;
    private String search = null;
    private int postsPerPage = 10;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        utils = new Utils(this);
        setTheme(utils.getSelectedTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFeedProvider = new FeedProvider(this);

        init();

        mFeedsArrayList = getIntent().getParcelableArrayListExtra("main_feed");
        category = getIntent().getStringExtra("category");
        if (mFeedsArrayList != null)
            refreshRecycler(mFeedsArrayList.size());
        else
            mFeedProvider.getFeedsArrayList(0, category, search, this);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (!utils.isNetworkAvailable())
            Toast.makeText(MainActivity.this, R.string.offline_mode, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("main_feed", mFeedsArrayList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mFeedsArrayList = savedInstanceState.getParcelableArrayList("main_feed");
        if (mFeedsArrayList != null)
            refreshRecycler(mFeedsArrayList.size());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search = query;
                getFeed();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.homePage) {
            category = null;
            search = null;
        }
        if (item.getItemId() != R.id.action_search) {
            isMenuSelected = true;
            getFeed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mLeftDrawerLayout.isMenuVisible())
            mLeftDrawerLayout.closeMenu(true);
        else if (mWaveSwipeRefreshLayout.isRefreshing())
            mWaveSwipeRefreshLayout.setRefreshing(false);
        else if (search != null || (category != null && !category.equalsIgnoreCase(""))) {
            search = null;
            category = null;
            getFeed();
        } else
            super.onBackPressed();
    }

    @Override
    public void onPreRequest() {
        recyclerView.setError(null);
    }

    @Override
    public void onSuccess(ArrayList<Feeds> result) {
        if (result != null) {
            if (isSwipeRefresh) {
                mFeedsArrayList.clear();
                mWaveSwipeRefreshLayout.setRefreshing(false);
                isSwipeRefresh = false;
            }

            mFeedsArrayList.addAll(result);
            refreshRecycler(result.size());
        }
    }

    @Override
    public void onFailure(String error) {
        if (error.equalsIgnoreCase("not_found")) {
            if (isLoadingMore) {
                recyclerView.disableLoadMore();
                Snackbar.make(recyclerView, R.string.no_more_post, Snackbar.LENGTH_LONG).show();
                isLoadingMore = false;
            } else
                recyclerView.setError(getString(R.string.not_found));
        } else {
            if (isSwipeRefresh) {
                mAdapter.clear();
                mWaveSwipeRefreshLayout.setRefreshing(false);
            } else {
                int errorString = utils.isNetworkAvailable() ? R.string.connection_failed : R.string.no_network;
                utils.showSnack(recyclerView, getString(errorString), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mFeedProvider.getFeedsArrayList(getPage(mFeedsArrayList.size()), category, search, MainActivity.this);
                    }
                });
            }
            recyclerView.setError(error);
        }
    }

    @Override
    public void onSelect(MenuItem item) {
        mLeftDrawerLayout.closeMenu(true);

        switch (item.getItemId()) {
            case R.id.instagram:
                utils.openInstagram();
                return;

            case R.id.telegram:
                utils.openTelegram();
                return;

            case R.id.contact:
                utils.openMailChooser();
                return;

            case R.id.settings:
                startActivityForResult(new Intent(this, SettingsActivity.class), 10002);
                return;

            case R.id.html:
                category = "html-css";
                break;

            case R.id.js:
                category = "javascript";
                break;

            case R.id.node:
                category = "node-js";
                break;

            case R.id.angular:
                category = "angularjs";
                break;

            case R.id.vagrant:
                category = "vagrant-tutorials";
                break;

            case R.id.laravel:
                category = "laravel-tutorials";
                break;

            case R.id.jq:
                category = "jquery";
                break;

            case R.id.php:
                category = "php";
                break;

            case R.id.bootstrap:
                category = "bootstrap";
                break;

            case R.id.ruby_on_rails:
                category = "ruby-on-rails";
                break;

            case R.id.express:
                category = "expressjs";
                break;

            case R.id.symfony:
                category = "symfony";
                break;
        }
        loadBasedOnCategory();
    }

    private void init() {
        postsPerPage = utils.getPostsPerPage();
        checkToken();
        setupToolbar();
        setupMenu();
        setupContent();
        setupNotificationChannels();
    }

    private void checkToken() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String token = FirebaseInstanceId.getInstance().getToken();
        if (!pref.getBoolean("token_refreshed", false))
            utils.sendRegistrationToServer(token);
    }

    private void setupContent() {
        mAdapter = new FeedsAdapter(mFeedsArrayList, this);
        mWaveSwipeRefreshLayout = (WaveSwipeRefreshLayout) findViewById(R.id.main_swipe);
        mWaveSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isSwipeRefresh = true;
                mFeedProvider.getFeedsArrayList(1, category, search, MainActivity.this);
            }
        });
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        mWaveSwipeRefreshLayout.setWaveColor(typedValue.data);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        final View empty = findViewById(R.id.emptyView);
        recyclerView = (recyclerView) findViewById(R.id.RecyclerView);
        recyclerView.setEmptyView(empty);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = getResources().getDimensionPixelOffset(R.dimen.cards_margin);
            }
        });

        recyclerView.setmAdapter(mAdapter);

        recyclerView.setOnLoadMoreListener(new Interfaces.OnLoadMoreListener() {
            @Override
            public void loadMore(int itemsCount) {
                if ((category != null || search != null || isMenuSelected) && itemsCount <= postsPerPage) {
                    isMenuSelected = false;
                    return;
                }
                isLoadingMore = true;
                mFeedProvider.getFeedsArrayList(getPage(itemsCount), category, search, MainActivity.this);
            }
        });
    }

    private void setupMenu() {
        mLeftDrawerLayout = (FlowingDrawer) findViewById(R.id.drawer_layout);
        mLeftDrawerLayout.setTouchMode(ElasticDrawer.TOUCH_MODE_BEZEL);

        FragmentManager fm = getSupportFragmentManager();
        MainMenu mainMenu = (MainMenu) fm.findFragmentById(R.id.id_container_menu);

        fm.beginTransaction().add(R.id.id_container_menu,
                mainMenu != null ? mainMenu : new MainMenu())
                .commit();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(VectorDrawableCompat.create(getResources(), R.drawable.ic_menu, getTheme()));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLeftDrawerLayout.openMenu(true);
            }
        });
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    private int getPage(int itemCount) {
        return (itemCount / postsPerPage) + 1;
    }

    private void refreshRecycler(int size) {
        setLoadMore(size);
        mAdapter.refresh(mFeedsArrayList);
    }

    private void loadBasedOnCategory() {
        search = null;
        getFeed();
    }

    private void setLoadMore(int dataSize) {
        if (dataSize < postsPerPage) {
            recyclerView.disableLoadMore();
            isLoadingMore = false;
        } else
            recyclerView.enableLoadMore();
    }

    private void getFeed() {
        mAdapter.clear();
        mFeedsArrayList.clear();
        mFeedProvider.getFeedsArrayList(1, category, search, MainActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10001 && resultCode == 20001) {
            Bundle extras = data.getExtras();
            category = extras.getString("category");
            loadBasedOnCategory();
        }
        if (requestCode == 10002) {
            recyclerView.setAdapter(null);
            recyclerView.setLayoutManager(null);
            recyclerView.setAdapter(mAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            mAdapter.notifyDataSetChanged();
            if (isThemeChanged())
                applyTheme();
        }
    }

    private void applyTheme() {
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putParcelableArrayListExtra("main_feed", mFeedsArrayList);
        startActivity(intent);
    }

    private boolean isThemeChanged() {
        if (utils == null)
            utils = new Utils(this);
        return utils.getThemeId() != utils.getSelectedTheme();
    }

    private void setupNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            List<NotificationChannel> mChannels = new ArrayList<>();
            mChannels.add(getChannel(getString(R.string.channel_id_new_post),
                    getString(R.string.channel_new_post),
                    getString(R.string.notification_new_post)));
            mChannels.add(getChannel(getString(R.string.channel_id_new_version),
                    getString(R.string.channel_new_version),
                    getString(R.string.notifications_new_version)));
            mNotificationManager.createNotificationChannels(mChannels);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private NotificationChannel getChannel(String id, String name, String description) {
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(id, name, importance);
        mChannel.setDescription(description);
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.GREEN);
        mChannel.enableVibration(true);
        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        return mChannel;
    }

}