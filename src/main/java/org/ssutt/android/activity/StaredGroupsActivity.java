package org.ssutt.android.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.ssutt.android.R;
import org.ssutt.android.Typefaces;
import org.ssutt.android.activity.schedule_activity.ScheduleActivity;
import org.ssutt.android.adapter.StaredGroupsListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StaredGroupsActivity extends ActionBarActivity {
    private static final String DEPARTMENT = "department";
    private static final String DEPARTMENT_FULL_NAME = "department_full_name";
    private static final String GROUP = "group";

    private List<String> departmentTags = new ArrayList<String>();
    private List<String> groups = new ArrayList<String>();
    private List<String> departmentFullNames = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stared_groups_view);

        int actionBarTitleid = getResources().getIdentifier("action_bar_title", "id", "android");
        TextView actionBarTitle = (TextView) findViewById(actionBarTitleid);
        actionBarTitle.setTypeface(Typefaces.get(this, "fonts/helvetica-bold"));
        
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle(getString(R.string.staredGroups));

        ListView staredGroupsListView = (ListView) findViewById(R.id.staredGroupView);
        List<String> starredGroups = getStarredGroups();

        String myDepartment = getIntent().getStringExtra("myDepartment");
        String myGroup = getIntent().getStringExtra("myGroup");

        StaredGroupsListAdapter staredGroupsListAdapter = new StaredGroupsListAdapter(getApplicationContext(), starredGroups, myDepartment, myGroup);
        staredGroupsListView.setAdapter(staredGroupsListAdapter);

        staredGroupsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getApplicationContext(), ScheduleActivity.class);
                intent.putExtra(DEPARTMENT, departmentTags.get(position));
                intent.putExtra(GROUP, groups.get(position));
                intent.putExtra(DEPARTMENT_FULL_NAME, departmentFullNames.get(position));
                startActivity(intent);
            }
        });
    }

    private List<String> getStarredGroups() {
        List<String> staredGroups = new ArrayList<String>();
        SharedPreferences pref = getSharedPreferences("star", MODE_PRIVATE);

        Map<String, ?> allGroups = pref.getAll();
        for (Map.Entry<String, ?> entry : allGroups.entrySet()) {
            if (entry.getValue().equals(Boolean.TRUE)) {
                String tag = entry.getKey();

                String[] split = tag.split("&");
                staredGroups.add(split[2]);
                staredGroups.add(split[1]);
                staredGroups.add(split[0]);

                departmentTags.add(split[0]);
                groups.add(split[1]);
                departmentFullNames.add(split[2]);
            }
        }

        return staredGroups;
    }
}
