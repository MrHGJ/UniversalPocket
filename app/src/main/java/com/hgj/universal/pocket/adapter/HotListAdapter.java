package com.hgj.universal.pocket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hgj.universal.pocket.R;
import com.hgj.universal.pocket.model.HotListItemBean;
import com.hgj.universal.pocket.util.DateUtil;

import org.jsoup.internal.StringUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HotListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<HotListItemBean> mData;
    private int tag; //0：微博     1：知乎      2：抽屉

    public HotListAdapter(Context context, int tag) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.tag = tag;
    }

    public void setData(ArrayList<HotListItemBean> list) {
        mData = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (tag == 0) {
            return new WeiboViewholder(mLayoutInflater.inflate(R.layout.item_hot_list, parent, false));
        } else {
            return new ZhiHuViewholder(mLayoutInflater.inflate(R.layout.item_zhihu_list, parent, false));
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof WeiboViewholder) {
            WeiboViewholder mHolder = (WeiboViewholder) holder;
            mHolder.tvRank.setText("" + (position + 1));
            mHolder.tvTitle.setText(mData.get(position).Title);
            String time = DateUtil.getTimePass(mData.get(position).CreateTime);
            mHolder.tvTime.setText(time);
        }
        if (holder instanceof ZhiHuViewholder) {
            ZhiHuViewholder mHolder = (ZhiHuViewholder) holder;
            mHolder.tvRank.setText("" + (position + 1));
            mHolder.tvTitle.setText(mData.get(position).Title);
            String hotDesc = mData.get(position).hotDesc;
            if (tag == 1) {
                if (StringUtil.isBlank(hotDesc)) {
                    mHolder.tvHot.setVisibility(View.GONE);
                } else {
                    mHolder.tvHot.setVisibility(View.VISIBLE);
                    mHolder.tvHot.setText(hotDesc);
                }
            }
            if (tag == 2) {
                String time = DateUtil.getTimePass(mData.get(position).CreateTime);
                mHolder.tvHot.setText(time);
            }

        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class WeiboViewholder extends RecyclerView.ViewHolder {
        TextView tvRank;
        TextView tvTitle;
        TextView tvTime;

        WeiboViewholder(View view) {
            super(view);
            tvRank = view.findViewById(R.id.tv_rank);
            tvTitle = view.findViewById(R.id.tv_title);
            tvTime = view.findViewById(R.id.tv_time);
        }
    }

    public class ZhiHuViewholder extends RecyclerView.ViewHolder {
        TextView tvRank;
        TextView tvTitle;
        TextView tvHot;

        ZhiHuViewholder(View view) {
            super(view);
            tvRank = view.findViewById(R.id.tv_rank);
            tvTitle = view.findViewById(R.id.tv_title);
            tvHot = view.findViewById(R.id.tv_hot_desc);
        }
    }
}
