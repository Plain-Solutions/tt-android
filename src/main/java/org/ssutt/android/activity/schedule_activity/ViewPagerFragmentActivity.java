package org.ssutt.android.activity.schedule_activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import org.ssutt.android.R;
import org.ssutt.android.activity.schedule_activity.tabs.Tab2Fragment;
import org.ssutt.android.activity.schedule_activity.tabs.Tab3Fragment;
import org.ssutt.android.activity.schedule_activity.tabs.TabMonday;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerFragmentActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {
    private PagerAdapter mPagerAdapter;
    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.viewpager_layout);
        this.initialisePaging();
    }

    private void initialisePaging() {
        List<Fragment> fragments = new ArrayList<Fragment>();
        //fragments.add(Fragment.instantiate(this, Tab3Fragment.class.getName()));

        fragments.add(Fragment.instantiate(this, TabMonday.class.getName()));
        fragments.add(Fragment.instantiate(this, Tab2Fragment.class.getName()));
        fragments.add(Fragment.instantiate(this, Tab3Fragment.class.getName()));

        //fragments.add(Fragment.instantiate(this, TabMonday.class.getName()));

        this.mPagerAdapter = new PagerAdapter(super.getSupportFragmentManager(), fragments);
        pager = (ViewPager) super.findViewById(R.id.viewpager);
        pager.setAdapter(this.mPagerAdapter);
        pager.setOnPageChangeListener(this);
        pager.setCurrentItem(0, false);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        /*if (state == ViewPager.SCROLL_STATE_IDLE) {
            int current = pager.getCurrentItem();
            int lastReal = pager.getAdapter().getCount() - 2;

            if (current == 0) {
                pager.setCurrentItem(lastReal, false);
            } else if (current > lastReal) {
                pager.setCurrentItem(1, false);
            }
        }*/
    }
}