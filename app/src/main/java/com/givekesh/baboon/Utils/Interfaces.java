package com.givekesh.baboon.Utils;

import android.view.MenuItem;

import com.givekesh.baboon.Utils.Comments.POJOS.Comment;
import com.givekesh.baboon.Utils.Posts.POJOS.Feeds;

import java.util.ArrayList;


public class Interfaces {

    public interface OnLoadMoreListener{
        void loadMore(int itemsCount);
    }

    public interface VolleyCallback {
        void onPreRequest();
        void onSuccess(ArrayList<Feeds> result);
        void onFailure(String error);
    }

    public interface SinglePostCallback{
        void onSuccess(Feeds post);
        void onFailure(String error);
    }

    public interface CommentsCallBack{
        void onSuccess(ArrayList<Comment> result);
        void onFailure(String error);
    }

    public interface OnNavClickListener{
        void onSelect(MenuItem item);
    }
}
