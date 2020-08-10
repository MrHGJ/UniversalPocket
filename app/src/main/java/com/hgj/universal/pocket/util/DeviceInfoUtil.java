package com.hgj.universal.pocket.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.util.Locale;

public class DeviceInfoUtil {

    /**
     * 获取设备宽度（px）
     *
     */
    public static int getDeviceWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度，不包含刘海部分（px）
     */
    public static int getDeviceHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取屏幕实际高度，包含刘海部分（px）
     */
    public static int getDeviceRealHeight(Context context) {
        int screenHeight = 0;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getRealMetrics(dm);
        screenHeight = dm.heightPixels;
        return screenHeight;
    }

    /**
     * 获取厂商名
     * **/
    public static String getDeviceManufacturer() {
        return android.os.Build.MANUFACTURER;
    }

    /**
     * 获取产品名
     * **/
    public static String getDeviceProduct() {
        return android.os.Build.PRODUCT;
    }

    /**
     * 获取手机品牌
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 获取手机型号
     */
    public static String getDeviceModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取手机主板名
     */
    public static String getDeviceBoard() {
        return android.os.Build.BOARD;
    }

    /**
     * 设备名
     * **/
    public static String getDeviceDevice() {
        return android.os.Build.DEVICE;
    }

    /**
     *
     *
     * fingerprit 信息
     * **/
    public static String getDeviceFubgerprint() {
        return android.os.Build.FINGERPRINT;
    }

    /**
     * 硬件名
     *
     * **/
    public static String getDeviceHardware() {
        return android.os.Build.HARDWARE;
    }

    /**
     * 主机
     *
     * **/
    public static String getDeviceHost() {
        return android.os.Build.HOST;
    }

    /**
     *
     * 显示ID
     * **/
    public static String getDeviceDisplay() {
        return android.os.Build.DISPLAY;
    }

    /**
     * ID
     *
     * **/
    public static String getDeviceId() {
        return android.os.Build.ID;
    }

    /**
     * 获取手机用户名
     *
     * **/
    public static String getDeviceUser() {
        return android.os.Build.USER;
    }

    /**
     * 获取手机 硬件序列号
     * **/
    public static String getDeviceSerial() {
        return android.os.Build.SERIAL;
    }

    /**
     * 获取手机Android 系统SDK
     *
     * @return
     */
    public static int getDeviceSDK() {
        return android.os.Build.VERSION.SDK_INT;
    }

    /**
     * 获取手机Android 版本
     *
     * @return
     */
    public static String getDeviceAndroidVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取当前手机系统语言。
     */
    public static String getDeviceDefaultLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取当前系统上的语言列表(Locale列表)
     */
    public static String getDeviceSupportLanguage() {
        Log.e("wangjie", "Local:" + Locale.GERMAN);
        Log.e("wangjie", "Local:" + Locale.ENGLISH);
        Log.e("wangjie", "Local:" + Locale.US);
        Log.e("wangjie", "Local:" + Locale.CHINESE);
        Log.e("wangjie", "Local:" + Locale.TAIWAN);
        Log.e("wangjie", "Local:" + Locale.FRANCE);
        Log.e("wangjie", "Local:" + Locale.FRENCH);
        Log.e("wangjie", "Local:" + Locale.GERMANY);
        Log.e("wangjie", "Local:" + Locale.ITALIAN);
        Log.e("wangjie", "Local:" + Locale.JAPAN);
        Log.e("wangjie", "Local:" + Locale.JAPANESE);
        return Locale.getAvailableLocales().toString();
    }

    public static String getDeviceAllInfo(Context context) {

        return "\n\n"

                + "\n\n2. 设备宽度:\n\t\t" + getDeviceWidth(context)

                + "\n\n3. 设备高度:\n\t\t" + getDeviceHeight(context)

                + "\n\n3. 设备真实高度:\n\t\t" + getDeviceRealHeight(context)


                + "\n\n4. 是否有内置SD卡:\n\t\t" + SDCardUtil.isSDCardMount()

                + "\n\n5. RAM 信息:\n\t\t" + SDCardUtil.getRAMInfo(context)

                + "\n\n6. 内部存储信息\n\t\t" + SDCardUtil.getStorageInfo(context, 0)

                + "\n\n7. SD卡 信息:\n\t\t" + SDCardUtil.getStorageInfo(context, 1)

                + "\n\n10. 系统默认语言:\n\t\t" + getDeviceDefaultLanguage()

                + "\n\n11. 硬件序列号(设备名):\n\t\t" + android.os.Build.SERIAL

                + "\n\n12. 手机型号:\n\t\t" + android.os.Build.MODEL

                + "\n\n13. 生产厂商:\n\t\t" + android.os.Build.MANUFACTURER

                + "\n\n14. 手机Fingerprint标识:\n\t\t" + android.os.Build.FINGERPRINT

                + "\n\n15. Android 版本:\n\t\t" + android.os.Build.VERSION.RELEASE

                + "\n\n16. Android SDK版本:\n\t\t" + android.os.Build.VERSION.SDK_INT

                + "\n\n17. 安全patch 时间:\n\t\t" + android.os.Build.VERSION.SECURITY_PATCH

                + "\n\n19. 版本类型:\n\t\t" + android.os.Build.TYPE

                + "\n\n20. 用户名:\n\t\t" + android.os.Build.USER

                + "\n\n21. 产品名:\n\t\t" + android.os.Build.PRODUCT

                + "\n\n22. ID:\n\t\t" + android.os.Build.ID

                + "\n\n23. 显示ID:\n\t\t" + android.os.Build.DISPLAY

                + "\n\n24. 硬件名:\n\t\t" + android.os.Build.HARDWARE

                + "\n\n25. 产品名:\n\t\t" + android.os.Build.DEVICE

                + "\n\n26. Bootloader:\n\t\t" + android.os.Build.BOOTLOADER

                + "\n\n27. 主板名:\n\t\t" + android.os.Build.BOARD

                + "\n\n28. CodeName:\n\t\t" + android.os.Build.VERSION.CODENAME
                + "\n\n29. 语言支持:\n\t\t" + getDeviceSupportLanguage();

    }

}
