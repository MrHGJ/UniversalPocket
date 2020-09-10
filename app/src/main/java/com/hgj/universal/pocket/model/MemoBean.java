package com.hgj.universal.pocket.model;

/**
 * 便签数据
 */
public class MemoBean {
    public int id;         //主键
    public  String title;  //便签标题
    public String content; //便签内容
    public String time;      //时间
    public int isTop;  //是否置顶   0 :否   1：是
}
