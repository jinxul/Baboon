package com.givekesh.baboon.Utils;

import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.givekesh.baboon.R;
import com.givekesh.baboon.SelectedPostActivity;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import worldline.com.foldablelayout.FoldableLayout;


public class FeedsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Map<Integer, Boolean> mFoldStates = new HashMap<>();
    private List<Feeds> mFeeds;
    private final Utils utils;
    private final Context mContext;
    private boolean isLoading = false;
    private View loading = null;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;


    public FeedsAdapter(List<Feeds> feeds, Context context) {
        mFeeds = feeds;
        mContext = context;
        utils = new Utils(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM)
            return new mHolder(new FoldableLayout(parent.getContext()));
        return new mHolderFooter(loading = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_bar, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionFooter(position))
            return TYPE_FOOTER;
        return TYPE_ITEM;
    }

    private boolean isPositionFooter(int position) {
        return position == getItemCount() - 1 && isLoading;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public void disableLoading() {
        isLoading = false;
        loading.setVisibility(View.GONE);
    }

    public void clear() {
        int size = this.mFeeds.size();
        if (size > 0) {
            for (int i = 0; i < size; i++)
                mFeeds.remove(0);

            this.notifyItemRangeRemoved(0, size);
            this.mFoldStates.clear();
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof mHolder) {

            final Feeds feed = mFeeds.get(position);

            Glide.with(mContext)
                    .load(feed.getContentImage())
                    .error(R.mipmap.ic_launcher)
                    .crossFade()
                    .into(((mHolder) holder).contentImage);


            ((mHolder) holder).Title.setText(Html.fromHtml(feed.getTitle()));
            //((mHolder) holder).Author.setText(feed.getAuthor());
            ((mHolder) holder).Date.setText(feed.getDate());

            ((mHolder) holder).Title_Detail.setText(Html.fromHtml(feed.getTitle()));
            ((mHolder) holder).Author_Detail.setText(feed.getAuthor());
            ((mHolder) holder).Date_Detail.setText(feed.getDate());
            ((mHolder) holder).Excerpt.setText(Html.fromHtml(feed.getExcerpt().replace("<p>", "<p align=\"justify\">")));

            ((mHolder) holder).open.setImageDrawable(utils.getMaterialIcon(MaterialDrawableBuilder.IconValue.OPEN_IN_NEW, Color.BLACK));

            ((mHolder) holder).open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, SelectedPostActivity.class);
                    intent.putExtra("post_parcelable", feed);
                    mContext.startActivity(intent);
                }
            });


            if (mFoldStates.containsKey(position)) {
                if (mFoldStates.get(position) == Boolean.TRUE) {
                    if (!((mHolder) holder).mFoldableLayout.isFolded()) {
                        ((mHolder) holder).mFoldableLayout.foldWithoutAnimation();
                    }
                } else if (mFoldStates.get(position) == Boolean.FALSE) {
                    if (((mHolder) holder).mFoldableLayout.isFolded()) {
                        ((mHolder) holder).mFoldableLayout.unfoldWithoutAnimation();
                    }
                }
            } else {
                ((mHolder) holder).mFoldableLayout.foldWithoutAnimation();
            }


            ((mHolder) holder).mFoldableLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((mHolder) holder).mFoldableLayout.isFolded()) {
                        ((mHolder) holder).mFoldableLayout.unfoldWithAnimation();
                    } else {
                        ((mHolder) holder).mFoldableLayout.foldWithAnimation();
                    }
                }
            });


            ((mHolder) holder).mFoldableLayout.setFoldListener(new FoldableLayout.FoldListener() {
                @Override
                public void onUnFoldStart() {
                    ((mHolder) holder).cardView.setCardElevation(10);
                }

                @Override
                public void onUnFoldEnd() {
                    mFoldStates.put(holder.getAdapterPosition(), false);
                }

                @Override
                public void onFoldStart() {
                    ((mHolder) holder).cardView.setCardElevation(0);
                }

                @Override
                public void onFoldEnd() {
                    mFoldStates.put(holder.getAdapterPosition(), true);
                }
            });
        }
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

    class mHolder extends RecyclerView.ViewHolder {

        private final ImageView contentImage;
        //private final TextView Author;
        private final TextView Date;
        private final TextView Title;

        private final TextView Author_Detail;
        private final TextView Date_Detail;
        private final TextView Title_Detail;
        private final TextView Excerpt;
        private final ImageButton open;

        private final CardView cardView;
        private final FoldableLayout mFoldableLayout;


        public mHolder(FoldableLayout itemView) {
            super(itemView);
            mFoldableLayout = itemView;

            itemView.setupViews(R.layout.feed_cover, R.layout.feed_detail, R.dimen.card_cover_height, itemView.getContext());

            contentImage = (ImageView) itemView.findViewById(R.id.content_image);
            //Author = (TextView) itemView.findViewById(R.id.author);
            Date = (TextView) itemView.findViewById(R.id.date);
            Title = (TextView) itemView.findViewById(R.id.title);

            Author_Detail = (TextView) itemView.findViewById(R.id.author_detail);
            Date_Detail = (TextView) itemView.findViewById(R.id.date_detail);
            Title_Detail = (TextView) itemView.findViewById(R.id.title_detail);
            Excerpt = (TextView) itemView.findViewById(R.id.excerpt);
            open = (ImageButton) itemView.findViewById(R.id.open);

            cardView = (CardView) itemView.findViewById(R.id.card_detail);
        }
    }

    class mHolderFooter extends RecyclerView.ViewHolder {
        public mHolderFooter(View itemView) {
            super(itemView);
        }
    }
}
