package com.hgj.universal.pocket.fragment.hotlist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.ybq.android.spinkit.SpinKitView;
import com.hgj.universal.pocket.R;
import com.hgj.universal.pocket.adapter.HotListAdapter;
import com.hgj.universal.pocket.event.ScrollStateChangeEvent;
import com.hgj.universal.pocket.http.ApiService;
import com.hgj.universal.pocket.model.HotListItemBean;
import com.hgj.universal.pocket.model.HotListResultBean;
import com.hgj.universal.pocket.webview.ShowWebViewActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChoutiListFragment extends Fragment {
    @BindView(R.id.loading_view)
    SpinKitView loadingView;
    @BindView(R.id.lrv_weibo_list)
    LRecyclerView mLRecyclerView;

    private Context mContext;
    private HotListAdapter mAdapter;
    private LRecyclerViewAdapter mLAdapter;
    private Unbinder mUnbinder;
    private ArrayList<HotListItemBean> dataList;
    private int id = 110;
    private int page = 0;
    private int totalPage = 0;

    public ChoutiListFragment(Context context) {
        mContext = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weibo_list, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dataList = new ArrayList<>();
        initAdapter();
        initListener();
        getHotListData(true);
    }

    @Override
    public void onDestroy() {
        mUnbinder.unbind();
        super.onDestroy();
    }

    private void initAdapter() {
        mAdapter = new HotListAdapter(mContext,2);
        mAdapter.setData(dataList);
        mLAdapter = new LRecyclerViewAdapter(mAdapter);
        View headerView = LayoutInflater.from(mContext).inflate(R.layout.header_hot_list,null);
        ImageView backImg = headerView.findViewById(R.id.img_header);
        backImg.setImageResource(R.drawable.header_chouti);
        mLAdapter.addHeaderView(headerView);
        mLRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mLRecyclerView.setAdapter(mLAdapter);
    }

    private void initListener() {
        //监听下拉刷新
        mLRecyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 0;
                dataList.clear();
                getHotListData(false);
            }
        });
        //上拉加载更多
        mLRecyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (page < totalPage - 1) {
                    page++;
                    getHotListData(false);
                } else {
                    mLRecyclerView.setNoMore(true);
                }
            }
        });
        mLAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.e("TAG", dataList.get(position).Url );
                Intent intent = new Intent(mContext, ShowWebViewActivity.class);
                intent.putExtra("url",dataList.get(position).Url );
                startActivity(intent);
            }
        });
        //监听列表滑动
        mLRecyclerView.setLScrollListener(new LRecyclerView.LScrollListener() {
            @Override
            public void onScrollUp() {
                EventBus.getDefault().post(new ScrollStateChangeEvent("ScrollUp"));
            }

            @Override
            public void onScrollDown() {
                EventBus.getDefault().post(new ScrollStateChangeEvent("ScrollDown"));
            }

            @Override
            public void onScrolled(int distanceX, int distanceY) {

            }

            @Override
            public void onScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 从网络获取数据
     *
     * @param isFirst
     */
    public void getHotListData(final boolean isFirst) {
        if (isFirst) {
            loadingView.setVisibility(View.VISIBLE);
        }
        String url = "https://www.tophub.fun:8888";
        Map<String, Integer> params = new HashMap<>();
        params.put("id", id);
        params.put("page", page);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        Subscription subscription = apiService.getHotListData(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Subscriber<HotListResultBean>() {
                            @Override
                            public void onCompleted() {
                                loadingView.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(HotListResultBean hotListResultBean) {
                                if (isFirst) {
                                    totalPage = hotListResultBean.Data.page;
                                }
                                dataList.addAll(hotListResultBean.Data.data);
                                //还未到最后一页
                                if (page < totalPage - 1) {
                                    mLRecyclerView.setNoMore(false);
                                } else {
                                    mLRecyclerView.setNoMore(true);
                                }
                                mLRecyclerView.refreshComplete(50);
                                mLAdapter.notifyDataSetChanged();
                            }
                        }
                );
    }
}
