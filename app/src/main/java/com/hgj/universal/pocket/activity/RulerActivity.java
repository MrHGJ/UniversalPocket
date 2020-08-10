package com.hgj.universal.pocket.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Window;
import android.view.WindowManager;

import com.hgj.universal.pocket.view.RulerView;

public class RulerActivity extends AppCompatActivity {
    private RulerView rulerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏ActionBar
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(rulerView = new RulerView(this));
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (rulerView != null) {
            rulerView.setLineX(savedInstanceState.getFloat("lineX"));
            rulerView.setKedu(savedInstanceState.getInt("kedu"));
        }
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        if (rulerView != null) {
            outState.putFloat("lineX", rulerView.getLineX());
            outState.putInt("kedu", rulerView.getKedu());
        }
        super.onSaveInstanceState(outState, outPersistentState);
    }
}
