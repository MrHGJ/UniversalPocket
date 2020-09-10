package com.hgj.universal.pocket.activity.memo;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.hgj.universal.pocket.R;
import com.hgj.universal.pocket.db.DBManager;
import com.hgj.universal.pocket.model.MemoBean;
import com.hgj.universal.pocket.util.DateUtil;
import com.hgj.universal.pocket.util.DeviceUtil;
import com.hgj.universal.pocket.view.MiLanTingTextView;

import org.jsoup.internal.StringUtil;

import java.util.Date;

public class EditMemoActivity extends AppCompatActivity {
    @BindView(R.id.tv_time)
    MiLanTingTextView tvTime;
    @BindView(R.id.et_new_title)
    EditText etTitle;
    @BindView(R.id.et_new_content)
    EditText etContent;
    @BindView(R.id.menu_save)
    ImageView menuSave;

    private MemoBean data;
    private DBManager dbManager;


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
        setContentView(R.layout.activity_edit_memo);
        //设置状态栏字体颜色为深色
        DeviceUtil.setAndroidNativeLightStatusBar(this, false);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        dbManager = dbManager.getInstance(this);
        data = new MemoBean();
        data.time = DateUtil.getTimeString(new Date());// 获取当前时间
        Intent intent = getIntent();
        int id = intent.getIntExtra("memoId", -1);
        if (id != -1) {
            data = dbManager.queryMemoDetail(id);
            if (!StringUtil.isBlank(data.title)) {
                etTitle.setText(data.title);
            }
            if (!StringUtil.isBlank(data.content)) {
                etContent.setText(data.content);
            }
        }
        tvTime.setText(data.time);
        etTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    menuSave.setVisibility(View.VISIBLE);
                } else {
                    if (!etContent.isFocused()) {
                        menuSave.setVisibility(View.GONE);
                    }
                }
            }
        });
        etContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    menuSave.setVisibility(View.VISIBLE);
                } else {
                    if (!etTitle.isFocused()) {
                        menuSave.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(0, R.anim.activity_close);
        saveData();
    }

    private void saveData() {
        String oldTitle = data.title;
        String oldContent = data.content;
        data.title = etTitle.getText().toString();
        data.content = etContent.getText().toString();
        Integer mId = data.id;
        if (!StringUtil.isBlank(data.title) || !StringUtil.isBlank(data.content)) {
            if (StringUtil.isBlank(data.title)) {
                data.title = data.content.length() > 25 ? data.content.substring(0, 25) : data.content;
            }
            if (mId == null || mId <= 0) {
                data.time = DateUtil.getTimeString(new Date());// 获取当前时间
                dbManager.insert(data);
            } else {
                if (!data.title.equals(oldTitle) || !data.content.equals(oldContent)) {  //如果标题或内容发生更改
                    data.time = DateUtil.getTimeString(new Date());// 获取当前时间
                }
                dbManager.update(data);
            }
        } else {
            if (!(mId == null || mId <= 0)) {
                dbManager.delete(data);
            }
        }
    }

    @OnClick({R.id.menu_back, R.id.menu_save})
    public void onBtnClick(View v) {
        switch (v.getId()) {
            case R.id.menu_back:
                finish();
            case R.id.menu_save:
                //使EditText失去焦点
                etTitle.clearFocus();
                etContent.clearFocus();
                //关闭软件盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(menuSave.getWindowToken(), 0);
                break;
        }
    }
}
