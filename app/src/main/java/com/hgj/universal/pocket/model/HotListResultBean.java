package com.hgj.universal.pocket.model;

import java.util.List;

public class HotListResultBean {
    public int Code;
    public String Message;
    public DataBean Data;
    public static class DataBean {
        public int page;
        public List<HotListItemBean> data;
    }
}
