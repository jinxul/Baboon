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

        String category_name = category != null ? "category_name=" + category + "&" : "";
        String Search = search != null ? "search=" + search + "&" : "";
        String url = "http://baboon.ir/wp-json/givekesh/posts?" + category_name + Search + "page=" + page;

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
                        feeds.setPost(parsContent(post.content.rendered));
                        feeds.setDate(utils.getPersianDate(post.date));
                        feeds.setContentImage(post.image.source_url);
                        feeds.setExcerpt(post.excerpt.rendered);
                        feeds.setAuthor_avatar(post.author_info.author_avatar);
                        feedsArrayList.add(feeds);
                    }
                    callBack.onSuccess(feedsArrayList);
                } else
                    callBack.onFailure("not_found");
            }

            @Override
            public JavaType getReturnType() {
                return CollectionType.construct(ArrayList.class, SimpleType.construct(Posts.class));
            }
        }));

    }

    public void getSinglePost(String requestedUrl, final Interfaces.SinglePostCallback callBack) {
        if (!utils.isNetworkAvailable()) {
            callBack.onFailure(mContext.getString(R.string.no_network));
            return;
        }

        String url = "http://baboon.ir/wp-json/givekesh/post_by_url/" + requestedUrl;

        mRequestQueue.add(new JacksonRequest<>(Request.Method.GET, url, new JacksonRequestListener<Posts>() {
            @Override
            public void onResponse(Posts post, int statusCode, VolleyError error) {
                if (error != null) {
                    callBack.onFailure(error.toString());
                    return;
                }

                if (post != null) {
                    final Feeds feed = new Feeds();
                    feed.setId(post.id);
                    feed.setTitle(post.title.rendered);
                    feed.setAuthor(post.author_info.display_name);
                    feed.setPost(parsContent(post.content.rendered));
                    feed.setDate(utils.getPersianDate(post.date));
                    feed.setContentImage(post.image.source_url);
                    feed.setExcerpt(post.excerpt.rendered);
                    feed.setAuthor_avatar(post.author_info.author_avatar);

                    callBack.onSuccess(feed);
                } else
                    callBack.onFailure("not_found");
            }

            @Override
            public JavaType getReturnType() {
                return SimpleType.construct(Posts.class);
            }
        }));
    }

    private String removeYoutubeVideos(String input) {
        return input.contains("[su_youtube") ?
                input.replace(input.substring(input.indexOf("[su_youtube")), "") : input;
    }

    private String setCodeBox(String input) {
        return input.replaceAll("<pre class=\"lang:.* decode:true.*\">", "<pre dir=\"ltr\" class=\"prettyprint linenums\">");
    }

    private String parsContent(String input) {
        return setCodeBox(removeYoutubeVideos(input));
    }
}
