package com.hgj.universal.pocket.view;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.hgj.universal.pocket.util.MiLanTingTypefaceManager;

import androidx.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
public class MiLanTingBoldTextView extends TextView {
    public MiLanTingBoldTextView(Context context) {
        super(context);
        init(context);
    }

    public MiLanTingBoldTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MiLanTingBoldTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        Typeface miLanTing = MiLanTingTypefaceManager.getInstance().getMiLanTingBoldTypeface();
        if (miLanTing == null) {
            miLanTing = Typeface.createFromAsset(context.getAssets(), "font/MI_LanTing_Bold.ttf");
        }
        setTypeface(miLanTing);
    }
}