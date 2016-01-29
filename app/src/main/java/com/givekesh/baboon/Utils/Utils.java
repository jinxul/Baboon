package com.givekesh.baboon.Utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.givekesh.baboon.R;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class Utils {

    private static final long MILLIS_JULIAN_EPOCH = -210866803200000L;
    private static final long MILLIS_OF_A_DAY = 86400000L;

    private final Context mContext;

    public Utils(Context context) {
        mContext = context;
    }

    public Drawable getMaterialIcon(MaterialDrawableBuilder.IconValue iconValue, int color) {
        return MaterialDrawableBuilder.with(mContext)
                .setIcon(iconValue)
                .setColor(color)
                .build();
    }

    public String getPersianDate(String input) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH).parse(input);
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

}
