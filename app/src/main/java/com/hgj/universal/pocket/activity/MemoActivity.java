package com.hgj.universal.pocket.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.app.Dialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.hgj.universal.pocket.MainActivity;
import com.hgj.universal.pocket.R;
import com.hgj.universal.pocket.activity.memo.EditMemoActivity;
import com.hgj.universal.pocket.adapter.MemoListAdapter;
import com.hgj.universal.pocket.db.DBManager;
import com.hgj.universal.pocket.event.MemoLongClickEvent;
import com.hgj.universal.pocket.event.ScrollStateChangeEvent;
import com.hgj.universal.pocket.model.MemoBean;
import com.hgj.universal.pocket.util.DeviceUtil;
import com.hgj.universal.pocket.view.MiLanTingTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.LinkedList;
import java.util.List;

public class MemoActivity extends AppCompatActivity {
    @BindView(R.id.rv_memo_list)
    RecyclerView mRecyclerView;

    private DBManager dbManager;
    private List<MemoBean> memoList;
    private MemoListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        //隐藏ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_memo);
        //设置状态栏字体颜色为深色
        DeviceUtil.setAndroidNativeLightStatusBar(this, false);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        init();
    }

    private void init() {
        dbManager = dbManager.getInstance(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        memoList = new LinkedList<>();
        adapter = new MemoListAdapter(this, memoList);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMemoList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void updateMemoList() {
        memoList.clear();
        memoList.addAll(dbManager.queryAll());
        adapter.notifyDataSetChanged();
    }

    @OnClick({R.id.bt_new_memo})
    public void onBtnClick(View v) {
        switch (v.getId()) {
            case R.id.bt_new_memo:
                startActivity(new Intent(MemoActivity.this, EditMemoActivity.class));
                this.overridePendingTransition(R.anim.activity_open, R.anim.activity_stay);
                break;
        }
    }

    /**
     * 处理EventBus发送的事件
     *
     * @param event
     */
    @Subscribe
    public void onMemoItemLongClick(MemoLongClickEvent event) {
        final int position = event.position;
        final Dialog dialog = new Dialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.memo_delete_dialog, null);
        MiLanTingTextView btDelete = dialogView.findViewById(R.id.btn_memo_delete);
        MiLanTingTextView btCancel = dialogView.findViewById(R.id.btn_memo_cancel);
        btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbManager.delete(memoList.get(position));
                updateMemoList();
                dialog.dismiss();
            }
        });
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(dialogView);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                adapter.notifyDataSetChanged();
            }
        });
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setBackgroundDrawable(getResources().getDrawable(R.drawable.bottom_dialog_background));
        window.setWindowAnimations(R.style.DialogWindowAnim);
        dialog.show();
        //设置对话框大小，和底部margin。设置宽高要放在show()方法后面才生效
        WindowManager.LayoutParams lps = window.getAttributes();
        WindowManager windowManager = window.getWindowManager();
        Display defaultDisplay = windowManager.getDefaultDisplay();
        lps.width = (int) (defaultDisplay.getWidth() * 1);
        lps.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lps.verticalMargin = 0.0f;
        window.setAttributes(lps);
    }

}
