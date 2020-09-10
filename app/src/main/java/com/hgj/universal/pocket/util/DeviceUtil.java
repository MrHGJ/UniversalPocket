package com.hgj.universal.pocket.util;

import android.app.Activity;
import android.view.View;

public class DeviceUtil {
    /**
     * 设置状态栏字体颜色
     */
    public static void setAndroidNativeLightStatusBar(Activity activity, boolean dark){
        View decor=activity.getWindow().getDecorView();
        int ui=decor.getSystemUiVisibility();
        //设置状态中的文字和颜色为深色or浅色
        if(dark){
            //深色背景白字
            ui&=~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }else{
            //浅色背景黑字
            ui|=View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        decor.setSystemUiVisibility(ui);
    }

}
