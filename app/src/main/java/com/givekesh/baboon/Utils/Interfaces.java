package com.givekesh.baboon.Utils;

import android.view.MenuItem;

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

    public interface OnNavClickListener{
        void onSelect(MenuItem item);
    }
}
