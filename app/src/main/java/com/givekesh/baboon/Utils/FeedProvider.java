package com.givekesh.baboon.Utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.givekesh.baboon.R;
import com.givekesh.baboon.Utils.Comments.POJOS.Comment;
import com.givekesh.baboon.Utils.Comments.POJOS.Comments;
import com.givekesh.baboon.Utils.Posts.POJOS.Feeds;
import com.givekesh.baboon.Utils.Posts.POJOS.Posts;
import com.spothero.volley.JacksonNetwork;
import com.spothero.volley.JacksonRequest;
import com.spothero.volley.JacksonRequestListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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

    public void getFeedsArrayList(final int page, final String category, String search, final Interfaces.VolleyCallback callBack) {
        if (!utils.isNetworkAvailable()) {
            if (category == null || category.equalsIgnoreCase("")) {
                List<Posts> local = readFromLocal(page);
                if (local != null && local.size() > 0)
                    callBack.onSuccess(parseJson(local));
            } else {
                callBack.onFailure(mContext.getString(R.string.no_network));
                return;
            }
        }

        if (page == 1)
            callBack.onPreRequest();

        String category_name = category != null ? "category_name=" + category + "&" : "";
        String Search = search != null ? "search=" + search + "&" : "";
        String url = "http://baboon.ir/wp-json/givekesh/posts?" + category_name + Search + "per_page=" + utils.getPostsPerPage() + "&page=" + page;

        mRequestQueue.add(new JacksonRequest<>(Request.Method.GET, url, new JacksonRequestListener<List<Posts>>() {
            @Override
            public void onResponse(List<Posts> response, int statusCode, VolleyError error) {
                if (error != null) {
                    callBack.onFailure(error.toString());
                    return;
                }

                if (response.size() > 0) {
                    if (category == null || category.equalsIgnoreCase(""))
                        writeToLocal(response, page);
                    callBack.onSuccess(parseJson(response));
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
                    feed.setPost(post.content.rendered);
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

    public void getFeedsArrayList(final int post_id, final Interfaces.CommentsCallBack callBack) {

        if (!utils.isNetworkAvailable()) {
            callBack.onFailure(mContext.getString(R.string.no_network));
            return;
        }

        final ArrayList<Comment> feedsArrayList = new ArrayList<>();

        String url = "http://baboon.ir/wp-json/givekesh/comments/" + post_id;

        mRequestQueue.add(new JacksonRequest<>(Request.Method.GET, url, new JacksonRequestListener<List<Comments>>() {
            @Override
            public void onResponse(List<Comments> response, int statusCode, VolleyError error) {
                if (response != null && response.size() > 0) {
                    for (Comments comments : response) {
                        final Comment comment = new Comment();
                        comment.setId(comments.id);
                        comment.setParent_id(comments.parent_id);
                        comment.setDate(utils.getPersianDate(comments.date));
                        comment.setContent(comments.content);
                        comment.setDisplay_name(comments.author_info.display_name);
                        comment.setAuthor_avatar(comments.author_info.author_avatar);
                        feedsArrayList.add(comment);
                    }
                    callBack.onSuccess(utils.sortComments(feedsArrayList));
                } else
                    callBack.onFailure(mContext.getString(R.string.no_comment));
            }

            @Override
            public JavaType getReturnType() {
                return CollectionType.construct(ArrayList.class, SimpleType.construct(Comments.class));
            }
        }));
    }

    private void writeToLocal(List<Posts> response, int page) {
        try {
            File file = new File(mContext.getFilesDir().getPath(), "json_page_" + page + ".json");
            FileWriter fileWriter = new FileWriter(file, false);
            ObjectMapper mapper = new ObjectMapper();
            fileWriter.write(mapper.writeValueAsString(response));
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception ignored) {
        }
    }

    private List<Posts> readFromLocal(int page) {
        try {
            String json = getJsonString(page);
            ObjectMapper mapper = new ObjectMapper();
            JavaType javaType = CollectionType.construct(ArrayList.class, SimpleType.construct(Posts.class));
            return mapper.readValue(json, javaType);
        } catch (Exception ignored) {
        }
        return null;
    }

    private String getJsonString(int page) {
        try {
            File file = new File(mContext.getFilesDir().getPath(), "json_page_" + page + ".json");
            StringBuilder jsonString = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                jsonString.append(line);
                jsonString.append('\n');
            }
            bufferedReader.close();
            return jsonString.substring(jsonString.indexOf("["));
        } catch (Exception ignored) {
        }
        return null;
    }

    private ArrayList<Feeds> parseJson(List<Posts> response) {
        final ArrayList<Feeds> feedsArrayList = new ArrayList<>();
        for (Posts post : response) {
            final Feeds feeds = new Feeds();
            feeds.setId(post.id);
            feeds.setTitle(post.title.rendered);
            feeds.setAuthor(post.author_info.display_name);
            feeds.setPost(post.content.rendered);
            feeds.setDate(utils.getPersianDate(post.date));
            feeds.setContentImage(post.image.source_url);
            feeds.setExcerpt(post.excerpt.rendered);
            feeds.setAuthor_avatar(post.author_info.author_avatar);
            feedsArrayList.add(feeds);
        }
        return feedsArrayList;
    }
}
