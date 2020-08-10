package com.hgj.universal.pocket.http;
import com.hgj.universal.pocket.model.HotListResultBean;

import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import rx.Observable;

public interface ApiService {
    @GET("/v2/GetAllInfoGzip")
    Observable<HotListResultBean>getHotListData(@QueryMap Map<String,Integer> params);
}
