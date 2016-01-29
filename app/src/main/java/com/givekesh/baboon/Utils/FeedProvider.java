package com.givekesh.baboon.Utils;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.android.volley.toolbox.JsonArrayRequest;
import com.givekesh.baboon.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class FeedProvider {

    private final Context mContext;
    private final Utils utils;
    private final MySingleton mySingleton;


    public FeedProvider(Context context) {
        mContext = context;
        utils = new Utils(mContext);
        mySingleton = MySingleton.getInstance(context.getApplicationContext());
    }

    public void getFeedsArrayList(int page, String category, final Interfaces.VolleyCallback callBack) {


        if (!utils.isNetworkAvailable()) {
            callBack.onFailure(mContext.getString(R.string.no_network));
            return;
        }

        mySingleton.cancelAll();

        if (page == 1)
            callBack.onPreRequest();

        final ArrayList<Feeds> feedsArrayList = new ArrayList<>();

        String category_name = category != null ? "filter[category_name]=" + category + "&" : "";
        String url = "http://baboon.ir/wp-json/wp/v2/posts?" + category_name + "fields=id,title,author,date,better_featured_image,excerpt,content&page=" + page;

        JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET, url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                if (response.length() != 0) {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            final Feeds feed = new Feeds();
                            final JSONObject object = (JSONObject) response.get(i);

                            feed.setId(object.getInt("id"));
                            feed.setDate(utils.getPersianDate(object.getString("date")));
                            feed.setAuthor(getAuthor(object.getInt("author")));
                            feed.setTitle(object.getJSONObject("title").getString("rendered"));
                            feed.setExcerpt(object.getJSONObject("excerpt").getString("rendered"));
                            feed.setContentImage(object.getJSONObject("better_featured_image").getString("source_url"));
                            feed.setPost(object.getJSONObject("content").getString("rendered"));

                            feedsArrayList.add(feed);
                        }
                        callBack.onSuccess(feedsArrayList);
                    } catch (JSONException e) {
                        callBack.onFailure(e.getMessage());
                    }
                } else {
                    callBack.onFailure("lastPage");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callBack.onFailure(error.toString());
            }
        });


        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        mySingleton.addToRequestQueue(jsonRequest);
    }


    private String getAuthor(int id) {
        return mContext.getString(id == 1 ? R.string.author_1 : R.string.author_5);
    }
}
