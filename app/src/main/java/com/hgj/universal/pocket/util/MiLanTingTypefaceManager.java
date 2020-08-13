package com.hgj.universal.pocket.util;

import android.app.Activity;
import android.graphics.Typeface;

/**
 * 因为读取字体文件很耗时，所以做成单例
 */
public class MiLanTingTypefaceManager {
    private Typeface mMiLanTingTypeface;
    private Typeface mMiLanTingBoldTypeface;

    private MiLanTingTypefaceManager() {

    }

    public void init(Activity activity) {
        mMiLanTingTypeface = Typeface.createFromAsset(activity.getAssets(), "font/milanting.ttf");
        mMiLanTingBoldTypeface = Typeface.createFromAsset(activity.getAssets(), "font/MI_LanTing_Bold.ttf");
    }

    public Typeface getMiLanTingTypeface() {
        return mMiLanTingTypeface;
    }
    public Typeface getMiLanTingBoldTypeface() {
        return mMiLanTingBoldTypeface;
    }

    public static MiLanTingTypefaceManager getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final MiLanTingTypefaceManager INSTANCE = new MiLanTingTypefaceManager();
    }

}
