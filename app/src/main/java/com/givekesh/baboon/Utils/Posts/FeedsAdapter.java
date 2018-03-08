package com.givekesh.baboon.Utils.Posts;

import android.app.Activity;
import android.content.Intent;

import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.transition.AutoTransition;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.givekesh.baboon.R;
import com.givekesh.baboon.activities.SelectedPostActivity;
import com.givekesh.baboon.Utils.Posts.POJOS.Feeds;

import java.util.ArrayList;
import java.util.List;


public class FeedsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Feeds> mFeeds;
    private final Activity mActivity;
    private boolean isLoading = false;
    private View loading = null;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    public FeedsAdapter(List<Feeds> feeds, Activity activity) {
        mFeeds = feeds;
        mActivity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM)
            return new newHolder(LayoutInflater.from(parent.getContext()).inflate(getLayout(), parent, false));
        return new mHolderFooter(loading = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_bar, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionFooter(position))
            return TYPE_FOOTER;
        return TYPE_ITEM;
    }

    private int getLayout() {
        int value = Integer.parseInt(
                PreferenceManager.getDefaultSharedPreferences(mActivity).getString("pref_ui", "0"));
        switch (value) {
            case 0:
                return R.layout.updated_layout;
            case 1:
                return R.layout.new_ui;
            default:
                return R.layout.card_ui;
        }
    }

    private boolean isPositionFooter(int position) {
        return position == getItemCount() - 1 && isLoading;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
        if (loading && this.loading != null)
            this.loading.setVisibility(View.VISIBLE);
    }

    public void disableLoading() {
        if (loading != null) {
            isLoading = false;
            loading.setVisibility(View.GONE);
        }
    }

    public void clear() {
        int size = this.mFeeds.size();
        if (size > 0) {
            for (int i = 0; i < size; i++)
                mFeeds.remove(0);

            this.notifyItemRangeRemoved(0, size);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);

        if (holder instanceof newHolder) {
            final Feeds feed = mFeeds.get(position);
            if (getLayout() != R.layout.updated_layout)
                ((newHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showPost(feed, ((newHolder) holder).post_image);
                    }
                });
            if (((newHolder) holder).author_avatar != null) {
                Glide.with(mActivity)
                        .load(feed.getAuthor_avatar())
                        .apply(requestOptions)
                        .into(((newHolder) holder).author_avatar);
            } else {
                if (!((newHolder) holder).isCardUi)
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final int visibility = ((newHolder) holder).post_excerpt.getVisibility() == View.GONE ? View.VISIBLE : View.INVISIBLE;
                            Transition transition = new AutoTransition().setDuration(500).addListener(new Transition.TransitionListener() {
                                @Override
                                public void onTransitionStart(@NonNull Transition transition) {

                                }

                                @Override
                                public void onTransitionEnd(@NonNull Transition transition) {
                                    if (visibility == View.INVISIBLE)
                                        ((newHolder) holder).post_excerpt.setVisibility(View.GONE);
                                }

                                @Override
                                public void onTransitionCancel(@NonNull Transition transition) {

                                }

                                @Override
                                public void onTransitionPause(@NonNull Transition transition) {

                                }

                                @Override
                                public void onTransitionResume(@NonNull Transition transition) {

                                }
                            });
                            TransitionManager.beginDelayedTransition((ViewGroup) ((newHolder) holder).itemView, transition);
                            ((newHolder) holder).post_excerpt.setVisibility(visibility);
                        }
                    });
            }
            Glide.with(mActivity)
                    .load(feed.getContentImage())
                    .apply(requestOptions)
                    .into(((newHolder) holder).post_image);

            ((newHolder) holder).author_name.setText(feed.getAuthor());
            if (!((newHolder) holder).isCardUi)
                ((newHolder) holder).post_date.setText(feed.getDate());
            ((newHolder) holder).post_title.setText(Html.fromHtml(feed.getTitle()));
            ((newHolder) holder).post_excerpt.setText(Html.fromHtml(feed.getExcerpt().replace("<p>", "<p align=\"justify\">")));
            ((newHolder) holder).full_article.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPost(feed, ((newHolder) holder).post_image);
                }
            });
            ((newHolder) holder).post_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPost(feed, ((newHolder) holder).post_image);
                }
            });
            if (((newHolder) holder).isCardUi) {
                ((newHolder) holder).Comments_count.setText(String.valueOf(" " + feed.getComments_count()));
                ((newHolder) holder).Comments_count.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showPost(feed, ((newHolder) holder).post_image);
                    }
                });
            }
        }
    }

    private void showPost(Feeds feed, View view) {
        Intent intent = new Intent(mActivity, SelectedPostActivity.class);
        intent.putExtra("post_parcelable", feed);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(mActivity, view, "post_image");
        mActivity.startActivityForResult(intent, 10001, options.toBundle());
    }

    @Override
    public int getItemCount() {
        int footer = 0;
        if (isLoading) {
            footer++;
        }

        return mFeeds.size() + footer;
    }

    public void refresh(ArrayList<Feeds> feeds) {
        mFeeds = feeds;
        notifyItemRangeInserted(getItemCount() + 1, feeds.size());
    }


    private class mHolderFooter extends RecyclerView.ViewHolder {
        mHolderFooter(View itemView) {
            super(itemView);
        }
    }

    private class newHolder extends RecyclerView.ViewHolder {

        private ImageView author_avatar;
        private ImageView post_image;
        private TextView author_name;
        private TextView post_date;
        private TextView post_title;
        private TextView post_excerpt;
        private TextView full_article;
        private TextView Comments_count;
        private boolean isCardUi;

        newHolder(View itemView) {
            super(itemView);
            isCardUi = getLayout() == R.layout.card_ui;
            post_image = itemView.findViewById(R.id.post_image);
            author_name = itemView.findViewById(R.id.author_name);
            if (!isCardUi) {
                post_date = itemView.findViewById(R.id.post_date);
                author_avatar = itemView.findViewById(R.id.author_avatar);
            }
            post_title = itemView.findViewById(R.id.post_title);
            post_excerpt = itemView.findViewById(R.id.post_excerpt);
            full_article = itemView.findViewById(R.id.full_article);
            if (isCardUi)
                Comments_count = itemView.findViewById(R.id.comments_count);
        }
    }
}
