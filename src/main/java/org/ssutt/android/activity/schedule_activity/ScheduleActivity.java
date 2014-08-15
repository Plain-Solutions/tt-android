package org.ssutt.android.activity.schedule_activity;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import org.ssutt.android.R;
import org.ssutt.android.activity.schedule_activity.tabs.AbstractTab;
import org.ssutt.android.activity.schedule_activity.tabs.DayType;
import org.ssutt.android.activity.schedule_activity.tabs.TabFriday;
import org.ssutt.android.activity.schedule_activity.tabs.TabMonday;
import org.ssutt.android.activity.schedule_activity.tabs.TabSaturday;
import org.ssutt.android.activity.schedule_activity.tabs.TabThursday;
import org.ssutt.android.activity.schedule_activity.tabs.TabTuesday;
import org.ssutt.android.activity.schedule_activity.tabs.TabWednesday;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import info.hoang8f.android.segmented.SegmentedGroup;

public class ScheduleActivity extends FragmentActivity {
    private static final String DEPARTMENT = "department";
    private static final String GROUP = "group";
    private String department;
    private String group;

    private PagerAdapter pagerAdapter;
    private ViewPager pager;
    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.schedule_view);
        this.initialisePaging();
    }

    private void initialisePaging() {
        final View actionBarLayout = getLayoutInflater().inflate(R.layout.action_bar, null);
        ImageButton btnShowMenu = (ImageButton) actionBarLayout.findViewById(R.id.imageButton);
        final TextView settingsTextView = (TextView) actionBarLayout.findViewById(R.id.settingsTextView);
        settingsTextView.setVisibility(View.GONE);

        final ActionBar actionBar = getActionBar();
        actionBar.setCustomView(actionBarLayout);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);

        department = getIntent().getStringExtra(DEPARTMENT);
        group = getIntent().getStringExtra(GROUP);

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK) - 2;
        if(day < 0) {
            day = 0;
        }

        List<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(new TabMonday());
        fragments.add(new TabTuesday());
        fragments.add(new TabWednesday());
        fragments.add(new TabThursday());
        fragments.add(new TabFriday());
        fragments.add(new TabSaturday());

        this.pagerAdapter = new PagerAdapter(super.getSupportFragmentManager(), fragments);
        pager = (ViewPager) super.findViewById(R.id.viewpager);
        pager.setAdapter(this.pagerAdapter);


        final String[] data = this.getResources().getStringArray(R.array.days_array);
        ArrayAdapter<String> daysSpinnerAdapter = new ArrayAdapter<String>(this, R.layout.spinner_test, data);
        daysSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final Spinner daysSpinner = (Spinner) actionBarLayout.findViewById(R.id.daysSpinner);
        daysSpinner.setAdapter(daysSpinnerAdapter);
        daysSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                pager.setCurrentItem(position, false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        daysSpinner.setSelection(day);

        final SegmentedGroup segmentedGroup = (SegmentedGroup) findViewById(R.id.segmentGroup);
        segmentedGroup.check(R.id.btnNumerator);
        SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean("btnNumerator", true);
        editor.apply();

        segmentedGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup rg, int checkedId) {
                DayType dayType;
                if (checkedId == R.id.btnNumerator) {
                    dayType = DayType.NUMERATOR;
                } else {
                    dayType = DayType.DENOMINATOR;
                }

                AbstractTab item = (AbstractTab) pagerAdapter.getCurrentFragment();
                item.refreshSchedule(getApplicationContext(), dayType, department, group);

                SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("btnNumerator", checkedId == R.id.btnNumerator);
                editor.apply();
            }
        });

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                AbstractTab tab = (AbstractTab) pagerAdapter.getItem(position);
                tab.refreshSchedule(department, group);
                daysSpinner.setSelection(position, true);
            }
        });

        mPlanetTitles = getResources().getStringArray(R.array.days_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item, mPlanetTitles));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                System.out.println(mPlanetTitles[position]);
            }
        });

        mDrawerLayout.setDrawerListener(new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_launcher, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                daysSpinner.setVisibility(View.GONE);
                settingsTextView.setVisibility(View.VISIBLE);
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                daysSpinner.setVisibility(View.VISIBLE);
                settingsTextView.setVisibility(View.GONE);
                super.onDrawerClosed(drawerView);
            }
        });

        btnShowMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mDrawerLayout.isDrawerOpen(mDrawerList)) {
                    mDrawerLayout.openDrawer(mDrawerList);
                    daysSpinner.setVisibility(View.GONE);
                    settingsTextView.setVisibility(View.VISIBLE);
                } else {
                    mDrawerLayout.closeDrawer(mDrawerList);
                    daysSpinner.setVisibility(View.VISIBLE);
                    settingsTextView.setVisibility(View.GONE);
                }

                actionBar.setCustomView(actionBarLayout);
            }
        });
    }
}