package com.hgj.universal.pocket;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.hgj.universal.pocket.activity.FullScreenTextActivity;
import com.hgj.universal.pocket.activity.HotListActivity;
import com.hgj.universal.pocket.activity.PaintActivity;
import com.hgj.universal.pocket.activity.PianoActivity;
import com.hgj.universal.pocket.activity.RulerActivity;
import com.hgj.universal.pocket.activity.SystemInfoActivity;
import com.hgj.universal.pocket.util.CommonUtil;
import com.hgj.universal.pocket.webview.ShowMovieActivity;
import com.hgj.universal.pocket.webview.ShowWebViewActivity;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.bt_ruler, R.id.bt_hand_text, R.id.bt_piano,R.id.bt_paint,R.id.bt_hot_list,R.id.bt_movie,R.id.bt_system_info})
    public void onBtnClick(View v) {
        switch (v.getId()) {
            case R.id.bt_ruler:
                jumpTo(RulerActivity.class);
                break;
            case R.id.bt_hand_text:
                jumpTo(FullScreenTextActivity.class);
                break;
            case R.id.bt_piano:
                jumpTo(PianoActivity.class);
                break;
            case R.id.bt_paint:
                jumpTo(PaintActivity.class);
                break;
            case R.id.bt_hot_list:
                jumpTo(HotListActivity.class);
               // CommonUtil.openApp("com.sina.weibo","com.sina.weibo.SplashActivity",this);
                // CommonUtil.openApp("com.zhihu.android","com.zhihu.android.app.ui.activity.LauncherActivity",this);
                break;
            case R.id.bt_system_info:
                jumpTo(SystemInfoActivity.class);
                break;
            case R.id.bt_movie:
                Intent intent = new Intent(this, ShowMovieActivity.class);
                intent.putExtra("url","http://by.male27.live/tiao/vgo.php?4j1dsx.126543css=");
                startActivity(intent);
                break;
        }
    }

    private void jumpTo(Class activity) {
        startActivity(new Intent(MainActivity.this, activity));
    }
}
