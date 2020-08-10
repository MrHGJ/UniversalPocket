package com.hgj.universal.pocket.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.hgj.universal.pocket.R;
import com.hgj.universal.pocket.event.ScrollStateChangeEvent;
import com.hgj.universal.pocket.fragment.hotlist.BaiduListFragment;
import com.hgj.universal.pocket.fragment.hotlist.ChoutiListFragment;
import com.hgj.universal.pocket.fragment.hotlist.WeiboListFragment;
import com.hgj.universal.pocket.fragment.hotlist.ZhihuListFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class HotListActivity extends AppCompatActivity {
    @BindView(R.id.tl_tabs)
    TabLayout tabLayout;
    @BindView(R.id.vp_content)
    ViewPager viewPager;
    @BindView(R.id.bt_down)
    ImageView btHideTab;

    List<Fragment> fragments = new ArrayList<>();
    final TranslateAnimation showAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f,
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f);

    final TranslateAnimation hideAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 1.0f,
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        //隐藏ActionBar
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_hot_list);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();
        initListener();
    }

    private void initView() {
        showAnim.setDuration(300);
        hideAnim.setDuration(300);
        fragments.add(new WeiboListFragment(this));
        fragments.add(new ZhihuListFragment(this));
        fragments.add(new BaiduListFragment(this));
        fragments.add(new ChoutiListFragment(this));
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
            //防止Fragment重建
            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            }
        });
        tabLayout.setupWithViewPager(viewPager);
        initTabs();
    }

    private void initTabs() {
        for (int i = 0; i < fragments.size(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            View view = LayoutInflater.from(this).inflate(R.layout.item_tabs_hot_list, null);
            ImageView icon = view.findViewById(R.id.img_tab_icon);
            switch (i) {
                case 0:
                    icon.setImageResource(R.drawable.ic_weibo);
                    icon.setSelected(true);
                    break;
                case 1:
                    icon.setImageResource(R.drawable.ic_zhihu);
                    break;
                case 2:
                    icon.setImageResource(R.drawable.ic_baidu);
                    break;
                case 3:
                    icon.setImageResource(R.drawable.ic_chouti);
                    break;
            }
            tab.setCustomView(view);
        }
    }

    private void initListener() {
        btHideTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tabLayout.getVisibility() == View.VISIBLE) {
                    tabLayout.startAnimation(hideAnim);
                    tabLayout.setVisibility(View.INVISIBLE);
                    btHideTab.setImageResource(R.drawable.ic_left);
                } else {
                    tabLayout.startAnimation(showAnim);
                    tabLayout.setVisibility(View.VISIBLE);
                    btHideTab.setImageResource(R.drawable.ic_right);
                }
            }
        });
    }

    @Subscribe
    public void onPullStateChangeEvent(ScrollStateChangeEvent event) {
        if (event.msg == "ScrollUp") {
            if (tabLayout.getVisibility() == View.VISIBLE) {
                tabLayout.startAnimation(hideAnim);
                tabLayout.setVisibility(View.INVISIBLE);
                btHideTab.setImageResource(R.drawable.ic_left);
            }
        }
        if (event.msg == "ScrollDown") {
            if (tabLayout.getVisibility() != View.VISIBLE) {
                tabLayout.startAnimation(showAnim);
                tabLayout.setVisibility(View.VISIBLE);
                btHideTab.setImageResource(R.drawable.ic_right);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
