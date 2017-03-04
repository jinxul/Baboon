package com.givekesh.baboon.Utils;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.view.View;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.givekesh.baboon.R;
import com.givekesh.baboon.Utils.Comments.POJOS.Comment;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;


public class Utils {

    private static final long MILLIS_JULIAN_EPOCH = -210866803200000L;
    private static final long MILLIS_OF_A_DAY = 86400000L;
    private final Context mContext;
    private SharedPreferences pref;

    public Utils(Context context) {
        mContext = context;
        pref = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    String getPersianDate(String input) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss", Locale.ENGLISH).parse(input);
            long julianDate = ((long) Math.floor((date.getTime() - MILLIS_JULIAN_EPOCH)) / MILLIS_OF_A_DAY);
            long PersianRowDate = PersianCalendarUtils.julianToPersian(julianDate);
            long year = PersianRowDate >> 16;
            int month = (int) (PersianRowDate & 0xff00) >> 8;
            int day = (int) (PersianRowDate & 0xff);
            int persianYear = (int) (year > 0 ? year : year - 1);
            String[] persianMonth = mContext.getResources().getStringArray(R.array.month);

            return String.valueOf(day + " " + persianMonth[month] + " " + persianYear);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return input;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void openTelegram() {
        mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://telegram.me/baboon_ir")));
    }

    public void openMailChooser() {
        String[] mails = new String[]{"info@baboon.ir"};
        String PACKAGE_GMAIL = "android.gm";
        String PACKAGE_EMAIL = "android.email";
        String INTENT_TYPE_MSG = "message/rfc822";
        String INTENT_TYPE_TEXT = "text/plain";

        Intent mailIntent = new Intent();
        mailIntent.setAction(Intent.ACTION_SEND);
        mailIntent.putExtra(Intent.EXTRA_EMAIL, mails);
        mailIntent.setType(INTENT_TYPE_MSG);

        PackageManager pm = mContext.getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType(INTENT_TYPE_TEXT);

        Intent openInChooser = Intent.createChooser(mailIntent, mContext.getString(R.string.email_chooser));

        List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
        List<LabeledIntent> intentList = new ArrayList<>();
        for (ResolveInfo ri : resInfo) {
            String packageName = ri.activityInfo.packageName;
            if (packageName.contains(PACKAGE_EMAIL)) {
                mailIntent.setPackage(packageName);
            } else if (packageName.contains(PACKAGE_GMAIL)) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType(INTENT_TYPE_TEXT);

                intent.putExtra(Intent.EXTRA_EMAIL, mails);
                intent.setType(INTENT_TYPE_MSG);

                intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
            }
        }

        LabeledIntent[] extraIntents = intentList.toArray(new LabeledIntent[intentList.size()]);

        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
        mContext.startActivity(openInChooser);
    }

    public void openInstagram() {
        Uri uri = Uri.parse(mContext.getString(R.string.instagram_intent_url));
        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

        likeIng.setPackage(mContext.getString(R.string.instagram_package));

        try {
            mContext.startActivity(likeIng);
        } catch (ActivityNotFoundException e) {
            mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(mContext.getString(R.string.instagram_url))));
        }
    }

    public Intent getMarketIntent(int market) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (isMarketInstalled(getMarketPackageName(market))) {
            intent.setData(Uri.parse(getMarketUri(market)));
        } else
            intent.setData(Uri.parse(getMarketUrl(market)));
        return intent;
    }

    private String getMarketUri(int market) {
        switch (market) {
            case 0:
                return "myket://details?id=com.givekesh.baboon";
            case 1:
                return "jhoobin://search?q=com.givekesh.baboon";
            default:
                return "bazaar://details?id=com.givekesh.baboon";
        }
    }

    private String getMarketPackageName(int market) {
        switch (market) {
            case 0:
                return "ir.mservices.market";
            case 1:
                return "net.jhoobin.jhub";
            default:
                return "com.farsitel.bazaar";
        }
    }

    private String getMarketUrl(int market) {
        switch (market) {
            case 0:
                return "https://myket.ir/app/com.givekesh.baboon/?l=fa";
            case 1:
                return "http://www.parshub.com/push/APP/930501662";
            default:
                return "https://cafebazaar.ir/app/com.givekesh.baboon/?l=fa";
        }
    }

    public String getMarketName(int market) {
        switch (market) {
            case 0:
                return mContext.getString(R.string.market_myket);
            case 1:
                return mContext.getString(R.string.market_jhoobin);
            default:
                return mContext.getString(R.string.market_bazaar);
        }
    }

    private boolean isMarketInstalled(String packageName) {
        PackageManager packageManager = mContext.getPackageManager();
        try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    ArrayList<Comment> sortComments(ArrayList<Comment> data) {
        ArrayList<Comment> result = new ArrayList<>();
        HashSet<Integer> map = new HashSet<>();

        for (int i = 0; i < data.size(); i++) {
            if (result.isEmpty() || !map.contains(data.get(i).getId())) {
                result.add(data.get(i));
                map.add(data.get(i).getId());
            }
            for (Comment comment : data)
                if (data.get(i).getId() == comment.getParent_id() && !map.contains(comment.getId())) {
                    result.add(comment);
                    map.add(comment.getId());
                }
        }
        return result;
    }

    public int getPostsPerPage() {
        int value = Integer.parseInt(pref.getString("pref_posts_per_page", "1"));
        switch (value) {
            case 0:
                return 5;
            case 1:
                return 10;
            case 2:
                return 15;
            default:
                return 20;
        }
    }

    public boolean shouldNotify(String key) {
        return pref.getBoolean(key, true);
    }

    public void sendRegistrationToServer(final String token) {
        String url = "http://baboon.ir/app/tokenHandler.php";
        final SharedPreferences manager = PreferenceManager.getDefaultSharedPreferences(mContext);
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        manager.edit().putBoolean("token_refreshed", true).apply();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        manager.edit().putBoolean("token_refreshed", false).apply();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", token);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    public void showSnack(View view, String string, View.OnClickListener listener) {
        Snackbar snackbar = Snackbar.make(view, string, Snackbar.LENGTH_INDEFINITE)
                .setAction(mContext.getString(R.string.try_again), listener);

        View snackBarView = snackbar.getView();
        ((TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.RED);
        ((TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text)).setGravity(GravityCompat.END);
        ((TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_action)).setTextColor(Color.WHITE);
        snackbar.show();
    }

    public boolean isUpdate(String version) {
        try {
            String versionName = mContext.getPackageManager()
                    .getPackageInfo(mContext.getPackageName(), 0)
                    .versionName;

            List<String> currentVersion = new ArrayList<>(Arrays.asList(Pattern.compile("\\.").split(versionName)));
            List<String> newVersion = new ArrayList<>(Arrays.asList(Pattern.compile("\\.").split(version)));

            currentVersion.add("0");
            newVersion.add("0");

            for (int i = 0; i < Math.min(currentVersion.size(), newVersion.size()); i++)
                if (Integer.parseInt(newVersion.get(i)) > Integer.parseInt(currentVersion.get(i)))
                    return true;
        } catch (Exception ignored) {
        }
        return false;
    }
}
