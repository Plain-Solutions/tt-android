package org.ssutt.android.activity.schedule_activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;

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
import java.util.List;

import info.hoang8f.android.segmented.SegmentedGroup;

public class ScheduleActivity extends FragmentActivity {
    private static final String DEPARTMENT = "department";
    private static final String GROUP = "group";

    private PagerAdapter pagerAdapter;
    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.schedule_view);
        this.initialisePaging();
    }

    private void initialisePaging() {
        final String department = getIntent().getStringExtra(DEPARTMENT);
        final String group = getIntent().getStringExtra(GROUP);

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
        pager.setCurrentItem(Integer.MAX_VALUE / 2);

        Spinner daysSpinner = (Spinner) findViewById(R.id.daysSpinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.days_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daysSpinner.setAdapter(adapter);
        daysSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pager.setCurrentItem(position, false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


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
                System.out.println(department + " " + group + " " + dayType.name() + " " + getApplicationContext());
                item.refreshSchedule(getApplicationContext(), dayType, department, group);

                SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                editor.putBoolean("btnNumerator", checkedId == R.id.btnNumerator);
                editor.apply();

                System.out.println("UPDATED!");
            }
        });
    }
}