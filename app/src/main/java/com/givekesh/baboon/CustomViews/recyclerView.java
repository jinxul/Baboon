package com.givekesh.baboon.CustomViews;


import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.givekesh.baboon.R;
import com.givekesh.baboon.Utils.Posts.FeedsAdapter;
import com.givekesh.baboon.Utils.Interfaces;


public class recyclerView extends RecyclerView {

    private Interfaces.OnLoadMoreListener onLoadMoreListener;
    private int mVisibleItemCount = 0;
    private int mTotalItemCount = 0;
    private boolean isLoadingMore = false;
    private int previousTotal = 0;
    private int mFirstVisibleItem;
    private RecyclerView.OnScrollListener mOnScrollListener;
    private FeedsAdapter mAdapter;
    private View mEmptyView;
    private View mParentView;
    private TextView mEmptyText;
    private View mEmptyProgress;

    private RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {

        @Override
        public void onItemRangeInserted(int positionStart, final int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            updateEmptyView();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            previousTotal = 0;
            updateEmptyView();
        }
    };

    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;
        mParentView = (ViewGroup) getParent();
        mEmptyText = (TextView) mEmptyView.findViewById(R.id.loading_text);
        mEmptyProgress = mEmptyView.findViewById(R.id.loading_progress);
    }

    public void setError(String error) {
        if (mEmptyView != null) {
            mEmptyProgress.setVisibility(error == null ? VISIBLE : INVISIBLE);
            mEmptyText.setText(error == null ? getContext().getString(R.string.first_load) : error);
        }
    }

    public void setmAdapter(FeedsAdapter mAdapter) {
        this.mAdapter = mAdapter;
        setAdapter(mAdapter);
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        if (getAdapter() != null) {
            getAdapter().unregisterAdapterDataObserver(mDataObserver);
        }
        if (adapter != null) {
            adapter.registerAdapterDataObserver(mDataObserver);
        }

        super.setAdapter(adapter);
        updateEmptyView();
    }

    private void updateEmptyView() {
        if (mEmptyView != null && getAdapter() != null) {
            boolean showEmptyView = getAdapter().getItemCount() <= 1;
            if (showEmptyView)
                setError(null);
            mEmptyView.setVisibility(showEmptyView ? VISIBLE : GONE);
            mParentView.setVisibility(showEmptyView ? GONE : VISIBLE);
        }
    }

    public recyclerView(Context context) {
        super(context);
    }

    public recyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public recyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void disableLoadMore() {
        removeOnScrollListener(mOnScrollListener);
        if (mAdapter != null)
            mAdapter.disableLoading();
    }

    public void enableLoadMore() {
        removeOnScrollListener(mOnScrollListener);

        mOnScrollListener = new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) getLayoutManager();
                mVisibleItemCount = layoutManager.getChildCount();
                mTotalItemCount = layoutManager.getItemCount();
                mFirstVisibleItem = layoutManager.findFirstVisibleItemPosition();

                if (isLoadingMore) {
                    if (mTotalItemCount > previousTotal) {
                        isLoadingMore = false;
                        previousTotal = mTotalItemCount;
                    }
                }

                if (!isLoadingMore && (mTotalItemCount - mVisibleItemCount) <= mFirstVisibleItem) {
                    onLoadMoreListener.loadMore(recyclerView.getAdapter().getItemCount());
                    isLoadingMore = true;
                    previousTotal = mTotalItemCount;
                }
            }

        };

        addOnScrollListener(mOnScrollListener);

        if (mAdapter != null && !mAdapter.isLoading())
            mAdapter.setLoading(true);
    }

    public void setOnLoadMoreListener(Interfaces.OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }
}
