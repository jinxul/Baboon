package com.givekesh.baboon.CustomViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by ahmad on 6/28/16.
 */
public class textView extends TextView {
    public textView(Context context) {
        super(context);
        setTypeFace();
    }

    public textView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeFace();
    }

    public textView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeFace();
    }

    private void setTypeFace() {
        setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Pacifico.ttf"));
    }
}
