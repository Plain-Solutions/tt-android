package org.ssutt.android.activity.schedule_activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.ssutt.android.R;
import org.ssutt.android.Typefaces;
import org.ssutt.android.activity.DepartmentActivity;
import org.ssutt.android.activity.SettingsActivity;
import org.ssutt.android.activity.StaredGroupsActivity;
import org.ssutt.android.activity.schedule_activity.tabs.AbstractTab;
import org.ssutt.android.activity.schedule_activity.tabs.DayType;
import org.ssutt.android.activity.schedule_activity.tabs.TabFriday;
import org.ssutt.android.activity.schedule_activity.tabs.TabMonday;
import org.ssutt.android.activity.schedule_activity.tabs.TabSaturday;
import org.ssutt.android.activity.schedule_activity.tabs.TabThursday;
import org.ssutt.android.activity.schedule_activity.tabs.TabTuesday;
import org.ssutt.android.activity.schedule_activity.tabs.TabWednesday;
import org.ssutt.android.adapter.DrawerAdapter;
import org.ssutt.android.adapter.SpinnerDayAdapter;
import org.ssutt.android.api.ApiConnector;
import org.ssutt.android.api.ApiRequests;
import org.ssutt.android.deserializer.MessageDeserializer;
import org.ssutt.android.domain.Message;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import info.hoang8f.android.segmented.SegmentedGroup;

import static org.ssutt.android.activity.Constants.CACHED_MSG;
import static org.ssutt.android.activity.Constants.DEPARTMENT;
import static org.ssutt.android.activity.Constants.DEPARTMENT_FULL_NAME;
import static org.ssutt.android.activity.Constants.FOR_SEARCH;
import static org.ssutt.android.activity.Constants.GROUP;
import static org.ssutt.android.activity.Constants.MY_DEPARTMENT;
import static org.ssutt.android.activity.Constants.MY_DEPARTMENT_FULL_NAME;
import static org.ssutt.android.activity.Constants.MY_GROUP;
import static org.ssutt.android.activity.Constants.PREF;
import static org.ssutt.android.activity.Constants.STAR;
import static org.ssutt.android.api.ApiConnector.isInternetAvailable;

public class ScheduleActivity extends ActionBarActivity {
    private static ScheduleActivity instance;

    private String department;

    private String departmentFullName;
    private String group;
    private PagerAdapter pagerAdapter;

    private ViewPager pager;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    public static ScheduleActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_view);
        instance = this;

        final View actionBarLayout = getLayoutInflater().inflate(R.layout.action_bar, null);
        ImageButton btnShowMenu = (ImageButton) actionBarLayout.findViewById(R.id.btnNavigationDrawer);
        final ImageButton btnStar = (ImageButton) actionBarLayout.findViewById(R.id.btnStar);
        final TextView settingsTextView = (TextView) actionBarLayout.findViewById(R.id.settingsTextView);
        settingsTextView.setVisibility(View.GONE);
        settingsTextView.setTypeface(Typefaces.get(this, "fonts/helvetica-bold"));

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(actionBarLayout);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);

        department = getIntent().getStringExtra(DEPARTMENT);
        departmentFullName = getIntent().getStringExtra(DEPARTMENT_FULL_NAME);
        group = getIntent().getStringExtra(GROUP);

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK) - 2;
        if (day < 0) {
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


        final String[] data = getResources().getStringArray(R.array.days_array);
        SpinnerDayAdapter daysSpinnerAdapter = new SpinnerDayAdapter(this, data);
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
        SharedPreferences preferences = getSharedPreferences(PREF, MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();

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

                SharedPreferences pref = getSharedPreferences(PREF, MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("btnNumerator", checkedId == R.id.btnNumerator);
                editor.apply();
            }
        });

        RadioButton btnNumerator = (RadioButton) findViewById(R.id.btnNumerator);
        RadioButton btnDenominator = (RadioButton) findViewById(R.id.btnDenominator);

        Typeface typeface = Typefaces.get(this, "fonts/helvetica-light.otf");
        btnNumerator.setTypeface(typeface);
        btnDenominator.setTypeface(typeface);

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

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new DrawerAdapter(this));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                switch (position) {
                    case 0:
                        SharedPreferences pref = getApplicationContext().getSharedPreferences("pref", MODE_PRIVATE);
                        Bundle extras = getIntent().getExtras();

                        String department1 = extras.getString(DEPARTMENT);
                        String group1 = extras.getString(GROUP);
                        String myDepartment = pref.getString(MY_DEPARTMENT, "empty");
                        String myGroup = pref.getString(MY_GROUP, "empty");
                        String myDepartmentFullName = pref.getString(MY_DEPARTMENT_FULL_NAME, "empty");

                        if (department1.equals(myDepartment) && group1.equals(myGroup)) {
                            break;
                        }

                        Intent intent = new Intent(getApplicationContext(), ScheduleActivity.class);
                        intent.putExtra(DEPARTMENT, myDepartment);
                        intent.putExtra(GROUP, myGroup);
                        intent.putExtra(DEPARTMENT_FULL_NAME, myDepartmentFullName);
                        startActivity(intent);
                        ScheduleActivity.this.finish();
                        break;
                    case 1:
                        extras = getIntent().getExtras();
                        String departmentTag = extras.getString(DEPARTMENT);
                        String request = ApiRequests.getDepartmentMsg(departmentTag);

                        DepartmentMessageTask departmentMessageTask = new DepartmentMessageTask();
                        departmentMessageTask.execute(request);
                        break;

                    case 2:
                        pref = getApplicationContext().getSharedPreferences(PREF, MODE_PRIVATE);
                        myDepartment = pref.getString(MY_DEPARTMENT, "empty");
                        myGroup = pref.getString(MY_GROUP, "empty");

                        intent = new Intent(getApplicationContext(), StaredGroupsActivity.class);
                        intent.putExtra(MY_DEPARTMENT, myDepartment);
                        intent.putExtra(MY_GROUP, myGroup);

                        startActivity(intent);
                        break;
                    case 3:
                        intent = new Intent(getApplicationContext(), DepartmentActivity.class);
                        intent.putExtra(FOR_SEARCH, true);
                        startActivity(intent);
                        break;
                    case 4:
                        intent = new Intent(getApplicationContext(), SettingsActivity.class);
                        startActivity(intent);
                        break;
                }

                mDrawerLayout.closeDrawers();
            }
        });

        mDrawerLayout.setDrawerListener(new ActionBarDrawerToggle(ScheduleActivity.this, mDrawerLayout, R.drawable.ic_launcher, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                daysSpinner.setVisibility(View.GONE);
                btnStar.setVisibility(View.GONE);
                settingsTextView.setVisibility(View.VISIBLE);
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                daysSpinner.setVisibility(View.VISIBLE);
                btnStar.setVisibility(View.VISIBLE);
                settingsTextView.setVisibility(View.GONE);
                supportInvalidateOptionsMenu();
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
                supportInvalidateOptionsMenu();
            }
        });

        final SharedPreferences starGroups = getApplicationContext().getSharedPreferences(STAR, MODE_PRIVATE);
        String tag = department + "&" + group + "&" + departmentFullName;
        boolean isStared = starGroups.getBoolean(tag, false);

        if (isStared) {
            btnStar.setBackgroundResource(android.R.drawable.btn_star_big_on);
        } else {
            btnStar.setBackgroundResource(android.R.drawable.btn_star_big_off);
        }

        btnStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getSharedPreferences(PREF, MODE_PRIVATE);
                String myDepartment = pref.getString(MY_DEPARTMENT, "empty");
                String myGroup = pref.getString(MY_GROUP, "empty");

                SharedPreferences.Editor editor = starGroups.edit();
                String tag = department + "&" + group + "&" + departmentFullName;
                boolean curIsStared = starGroups.getBoolean(tag, false);

                if (curIsStared) {
                    if (!(myDepartment.equals(department) && myGroup.equals(group))) {
                        editor.putBoolean(tag, false);
                        btnStar.setBackgroundResource(android.R.drawable.btn_star_big_off);
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.unstarMyGroup), Toast.LENGTH_LONG).show();
                    }
                } else {
                    editor.putBoolean(tag, true);
                    btnStar.setBackgroundResource(android.R.drawable.btn_star_big_on);
                }
                editor.apply();
            }
        });
    }


    class DepartmentMessageTask extends ApiConnector {
        AlertDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new AlertDialog.Builder(ScheduleActivity.this)
                    .setTitle(getString(R.string.departmentInfoTitle))
                    .setMessage(getString(R.string.loadingMessage))
                    .setPositiveButton(getString(R.string.close), null)
                    .show();

            Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/helvetica-light.otf");

            int textViewId = dialog.getContext().getResources().getIdentifier("android:id/alertTitle", null, null);
            TextView titleTextView = (TextView) dialog.findViewById(textViewId);
            TextView messageTextView = (TextView) dialog.findViewById(android.R.id.message);
            Button btnPositive = (Button) dialog.findViewById(android.R.id.button1);

            titleTextView.setTypeface(typeface);
            messageTextView.setTypeface(typeface);
            btnPositive.setTypeface(typeface);
        }

        @Override
        protected void onPostExecute(String json) {
            System.out.println("ON POST: " + json);
            if (!isValid(json)) {
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(CACHED_MSG, MODE_PRIVATE);
                String cachedJson = sharedPreferences.getString(getUrl(), "{\"msg\":\"" + getString(R.string.departmentInfoNotFound) + "\"}");
                System.out.println("CACHED JSON: " + cachedJson);
                updateUI(cachedJson);
                return;
            }

            cacheMessage(json);
            updateUI(json);
        }

        private void updateUI(String json) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Message.class, new MessageDeserializer());
            JsonElement jsonElement = new JsonParser().parse(json);
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            Message message = gsonBuilder.create().fromJson(jsonObject, Message.class);
            if (message.getMessage().equals("")) {
                message.setMessage(getString(R.string.emptyDepartmentInfo));
            }

            dialog.setMessage(message.getMessage());
        }

        private void cacheMessage(String json) {
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(CACHED_MSG, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getUrl(), json);
            editor.apply();
        }

        @Override
        public Context getContext() {
            return getApplicationContext();
        }
    }

}