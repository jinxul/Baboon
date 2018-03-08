package com.givekesh.baboon.Utils.Comments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.givekesh.baboon.R;
import com.givekesh.baboon.Utils.Comments.POJOS.Comment;

import java.util.ArrayList;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.holder> {
    private ArrayList<Comment> mData;
    private final int padding;
    private Context mContext;

    public CommentsAdapter(ArrayList<Comment> data, Context context) {
        mData = data;
        mContext = context;
        padding = (int) context.getResources().getDimension(R.dimen.activity_horizontal_margin);
    }

    @Override
    public holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_row, parent, false));
    }

    @Override
    public void onBindViewHolder(holder holder, int position) {
        final Comment comment = getItem(position);
        comment.setPadding(getPadding(position));
        holder.itemView.setPadding(padding, padding, getPadding(position), padding);
        holder.name.setText(comment.getDisplay_name());
        holder.content.setText(comment.getContent());
        holder.date.setText(comment.getDate());

        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        Glide.with(mContext)
                .load(comment.getAuthor_avatar())
                .apply(requestOptions)
                .into(holder.avatar);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    private int getPadding(int position) {
        if (position > 0) {
            final Comment comment = getItem(position);
            if (comment.getParent_id() == 0)
                return padding / 4;
            else if (comment.getParent_id() == getItem(position - 1).getId())
                return getItem(position - 1).getPadding() + (padding * 2);
            else
                return padding * 2;
        }
        return padding / 4;
    }

    private Comment getItem(int position) {
        return mData.get(position);
    }

    protected class holder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView content;
        private TextView date;
        private ImageView avatar;

        public holder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            content = itemView.findViewById(R.id.content);
            avatar = itemView.findViewById(R.id.avatar);
            date = itemView.findViewById(R.id.date);

        }
    }
}
