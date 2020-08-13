package com.hgj.universal.pocket.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.hgj.universal.pocket.util.MiLanTingTypefaceManager;

import androidx.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
/**
 * 米兰亭字体
 */
public class MiLanTingTextView extends TextView {
    public MiLanTingTextView(Context context) {
        super(context);
        init(context);
    }

    public MiLanTingTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MiLanTingTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        Typeface miLanTing = MiLanTingTypefaceManager.getInstance().getMiLanTingTypeface();
        if (miLanTing == null) {
            miLanTing = Typeface.createFromAsset(context.getAssets(), "font/milanting.ttf");
        }
        setTypeface(miLanTing);
    }
}