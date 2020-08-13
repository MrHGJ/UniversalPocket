package com.hgj.universal.pocket.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.hgj.universal.pocket.R;
import com.hgj.universal.pocket.util.DeviceInfoUtil;

public class SystemInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_info);
        String info = DeviceInfoUtil.getDeviceAllInfo(this);
        Log.e("TAG", info);

    }
}
