package com.hgj.universal.pocket.model;

public class HotListItemBean {
    public int id;
    public int CreateTime;
    public int commentNum;
    public int approvalNum;
    public String Title;
    public String hotDesc;
    public String Url;
    public String imgUrl;
    public String isRss;
    public int is_agree;
    public String TypeName;
    @Override
    public String toString() {
        return "HotListDataBean{" +
                "id=" + id +
                ", CreateTime=" + CreateTime +
                ", commentNum=" + commentNum +
                ", approvalNum=" + approvalNum +
                ", Title='" + Title + '\'' +
                ", hotDesc='" + hotDesc + '\'' +
                ", Url='" + Url + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", isRss='" + isRss + '\'' +
                ", is_agree=" + is_agree +
                ", TypeName='" + TypeName + '\'' +
                '}';
    }
}
