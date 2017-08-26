package com.givekesh.baboon.CustomViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.givekesh.baboon.R;

public class textView extends android.support.v7.widget.AppCompatTextView {
    private int mType = 0;

    public textView(Context context) {
        super(context);
        setTypeFace();
    }

    public textView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public textView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.textView, defStyleAttr, 0);
        if (getId() == R.id.post_title || getId() == R.id.post_excerpt ||
                getId() == R.id.author_name || getId() == R.id.post_date)
            mType = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("pref_font", "0"));
        else
            mType = styledAttributes.getInt(R.styleable.textView_type, 0);
        styledAttributes.recycle();
        setTypeFace();
    }

    private void setTypeFace() {
        setTypeface(Typeface.createFromAsset(getContext().getAssets(), getFont()));
        float textSize = getTextSize() / getResources().getDisplayMetrics().scaledDensity;
        if (mType == 0)
            textSize -= 3.5;
        else if (mType == 2)
            textSize -= 1.5;
        else if (mType == 3)
            textSize -= 1.2;
        setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }

    private String getFont() {
        switch (mType) {
            case 1:
                return "Fonts/Pacifico.ttf";
            case 2:
                return "Fonts/RobotoCondensed-Regular.ttf";
            case 3:
                return "Fonts/BNazanin.ttf";
            default:
                return "Fonts/Tanha.ttf";
        }
    }
}
