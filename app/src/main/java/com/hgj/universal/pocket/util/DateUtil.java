package com.hgj.universal.pocket.util;

import java.util.Date;

public class DateUtil {
    public static String getTimePass(int time) {
        long now = new Date().getTime();
        int diff = (int) (now / 1000) - time;
        int minute = diff / 60;
        int hour = minute / 60;
        int day = hour / 24;
        int month = day / 30;
        if (minute < 60) {
            return minute + "分钟前";
        } else if (hour < 24) {
            return hour + "小时前";
        } else if (day < 30) {
            return day + "天前";
        } else {
            return month + "个月前";
        }
    }
}
