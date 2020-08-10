package com.hgj.universal.pocket.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class CommonUtil {
    /**
     * 打开第三方应用
     * @param pkg  第三方应用包名
     * @param cls  第三方应用的Activity
     */
    public static void openApp(String pkg, String cls, Context context){
        ComponentName componet = new ComponentName(pkg,cls);
        Intent intent  = new Intent();
        intent.setComponent(componet);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
