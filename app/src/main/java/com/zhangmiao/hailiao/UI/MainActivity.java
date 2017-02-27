package com.zhangmiao.hailiao.UI;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zhangmiao.hailiao.ConversationFragment;
import com.zhangmiao.hailiao.MailListFragment;
import com.zhangmiao.hailiao.R;
import com.zhangmiao.hailiao.SetUpFragment;
import com.zhangmiao.hailiao.SharedPreferencesDBManager;
import com.zhangmiao.hailiao.User;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public SharedPreferencesDBManager mSPMnager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mSPMnager = new SharedPreferencesDBManager(this);
        User currentUser = mSPMnager.readData();
        if (currentUser == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

        Fragment conversationFragment = new ConversationFragment();
        Fragment mailListFragment = new MailListFragment();
        Fragment setUpFragment = new SetUpFragment();

        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(conversationFragment, "会话");
        pagerAdapter.addFragment(mailListFragment, "通讯录");
        pagerAdapter.addFragment(setUpFragment, "设置");
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            viewPager.setAdapter(pagerAdapter);
        }
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        if (viewPager != null && tabs != null) {
            tabs.setupWithViewPager(viewPager);
        }

        TabLayout.Tab one;
        TabLayout.Tab two;
        TabLayout.Tab three;

        one = tabs.getTabAt(0);
        two = tabs.getTabAt(1);
        three = tabs.getTabAt(2);

        View mailListTab = View.inflate(this, R.layout.tab, null);
        TextView mailListTextView = (TextView) mailListTab.findViewById(R.id.tab_text);
        mailListTextView.setText("通讯录");

        View conversationTab = View.inflate(this, R.layout.tab, null);
        TextView conversationTextView = (TextView) conversationTab.findViewById(R.id.tab_text);
        conversationTextView.setText("会话");

        View setUpTab = View.inflate(this, R.layout.tab, null);
        TextView setUpTextView = (TextView) setUpTab.findViewById(R.id.tab_text);
        setUpTextView.setText("设置");

        one.setCustomView(conversationTab);
        two.setCustomView(mailListTab);
        three.setCustomView(setUpTab);
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public List<Fragment> fragments = new ArrayList<>();
        private List<String> titles = new ArrayList<>();

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
