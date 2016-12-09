package com.givekesh.baboon.CustomViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import com.givekesh.baboon.R;

public class textView extends TextView {
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
        mType = styledAttributes.getInt(R.styleable.textView_type, 0);
        styledAttributes.recycle();
        setTypeFace();
    }

    private void setTypeFace() {
        setTypeface(Typeface.createFromAsset(getContext().getAssets(), getFont()));
        float textSize = getTextSize() / getResources().getDisplayMetrics().scaledDensity;
        if (mType == 0)
            textSize -= 3.5;
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
