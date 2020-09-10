package com.hgj.universal.pocket.adapter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hgj.universal.pocket.R;
import com.hgj.universal.pocket.activity.MemoActivity;
import com.hgj.universal.pocket.activity.memo.EditMemoActivity;
import com.hgj.universal.pocket.event.MemoLongClickEvent;
import com.hgj.universal.pocket.model.MemoBean;
import com.hgj.universal.pocket.view.MiLanTingBoldTextView;
import com.hgj.universal.pocket.view.MiLanTingTextView;

import org.greenrobot.eventbus.EventBus;
import org.jsoup.internal.StringUtil;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MemoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<MemoBean> mData;
    private Vibrator mVibrator;

    public MemoListAdapter(Context context, List<MemoBean> data) {
        mContext = context;
        mData = data;
        mLayoutInflater = LayoutInflater.from(context);
        mVibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new mViewHolder(mLayoutInflater.inflate(R.layout.memo_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final mViewHolder mHolder = (mViewHolder) holder;
        mHolder.tvTitle.setText(mData.get(position).title);
        if(StringUtil.isBlank(mData.get(position).content)){
            mHolder.tvContent.setVisibility(View.GONE);
        }else {
            mHolder.tvContent.setVisibility(View.VISIBLE);
            mHolder.tvContent.setText(mData.get(position).content);
        }
        mHolder.tvTime.setText(mData.get(position).time);
        mHolder.memoItem.setSelected(false);
        mHolder.memoItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, EditMemoActivity.class);
                intent.putExtra("memoId",mData.get(position).id);
                mContext.startActivity(intent);
            }
        });
        mHolder.memoItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mHolder.memoItem.setSelected(true);
                mVibrator.vibrate(20);
                EventBus.getDefault().post(new MemoLongClickEvent(position));
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class mViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.memo_item)
        LinearLayout memoItem;
        @BindView(R.id.tv_title)
        MiLanTingBoldTextView tvTitle;
        @BindView(R.id.tv_content)
        MiLanTingTextView tvContent;
        @BindView(R.id.tv_time)
        MiLanTingTextView tvTime;

        mViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
