package com.givekesh.baboon.Utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.givekesh.baboon.R;
import com.spothero.volley.JacksonNetwork;
import com.spothero.volley.JacksonRequest;
import com.spothero.volley.JacksonRequestListener;

import java.util.ArrayList;
import java.util.List;


public class FeedProvider {

    private final Context mContext;
    private final Utils utils;
    private final RequestQueue mRequestQueue;

    public FeedProvider(Context context) {
        mContext = context;
        utils = new Utils(mContext);
        mRequestQueue = JacksonNetwork.newRequestQueue(mContext);
    }

    public void getFeedsArrayList(final int page, String category, String search, final Interfaces.VolleyCallback callBack) {

        if (!utils.isNetworkAvailable()) {
            callBack.onFailure(mContext.getString(R.string.no_network));
            return;
        }

        if (page == 1)
            callBack.onPreRequest();

        final ArrayList<Feeds> feedsArrayList = new ArrayList<>();

        String category_name = category != null ? "filter[category_name]=" + category + "&" : "";
        String Search = search != null ? "filter[s]=" + search + "&" : "";
        String url = "http://baboon.ir/wp-json/wp/v2/posts?per_page=5&" + category_name + Search + "fields=id,title,author_info,date,better_featured_image,excerpt,content&page=" + page;

        mRequestQueue.add(new JacksonRequest<>(Request.Method.GET, url, new JacksonRequestListener<List<Posts>>() {
            @Override
            public void onResponse(List<Posts> response, int statusCode, VolleyError error) {
                if (error != null) {
                    callBack.onFailure(error.toString());
                    return;
                }

                if (response.size() > 0) {
                    for (Posts post : response) {
                        final Feeds feeds = new Feeds();
                        feeds.setId(post.id);
                        feeds.setTitle(post.title.rendered);
                        feeds.setAuthor(post.author_info.display_name);
                        feeds.setPost(post.content.rendered);
                        feeds.setDate(utils.getPersianDate(post.date));
                        feeds.setContentImage(post.better_featured_image.source_url);
                        feeds.setExcerpt(post.excerpt.rendered);
                        feedsArrayList.add(feeds);
                    }
                    callBack.onSuccess(feedsArrayList);
                } else
                    callBack.onFailure("lastPage");
            }

            @Override
            public JavaType getReturnType() {
                return CollectionType.construct(ArrayList.class, SimpleType.construct(Posts.class));
            }
        }));

    }
}
