package com.hgj.universal.pocket.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.hgj.universal.pocket.R;
import com.hgj.universal.pocket.util.DeviceInfoUtil;

public class SystemInfoActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSION_STORAGE = 101;  //请求读取手机数据权限的code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_info);
        String info = DeviceInfoUtil.getDeviceAllInfo(this);
        Log.e("TAG", info);

    }
}
