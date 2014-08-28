package org.ssutt.android.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.ssutt.android.R;
import org.ssutt.android.activity.schedule_activity.ScheduleActivity;
import org.ssutt.android.adapter.GroupListAdapter;
import org.ssutt.android.api.ApiConnector;
import org.ssutt.android.api.GroupMode;
import org.ssutt.android.deserializer.GroupDeserializer;
import org.ssutt.android.domain.Department;
import org.ssutt.android.domain.Group;

import static org.ssutt.android.api.ApiConnector.isInternetAvailable;
import static org.ssutt.android.api.ApiRequests.getGroups;

public class GroupActivity extends Activity {
    private static final String DEPARTMENT = "department";
    private static final String GROUP = "group";

    private SwipeRefreshLayout swipeLayout;
    private String[] groupNames;
    private ListView groupListView;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_view);
        groupListView = (ListView) findViewById(R.id.groupListView);
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        context = this;

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle(getString(R.string.chooseGroup));

        final Department department = (Department) getIntent().getSerializableExtra(DEPARTMENT);
        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences sharedPreferences = getSharedPreferences("pref", MODE_PRIVATE);
                boolean firstTime = sharedPreferences.getBoolean("firstTime", true);
                if (firstTime) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("myDepartment", department.getTag());
                    editor.putString("myGroup", groupNames[position]);
                    editor.putBoolean("firstTime", false);
                    editor.apply();
                }

                Intent intent = new Intent(context, ScheduleActivity.class);
                intent.putExtra(DEPARTMENT, department.getTag());
                intent.putExtra(GROUP, groupNames[position]);
                startActivity(intent);

                if (firstTime) {
                    GroupActivity.this.finish();
                }
            }
        });

        final String groupsRequest = getGroups(department.getTag(), GroupMode.ONLY_FILLED);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isInternetAvailable(context)) {
                    GroupTask scheduleTask = new GroupTask();
                    scheduleTask.execute(groupsRequest);
                } else {
                    swipeLayout.setRefreshing(false);
                }
            }
        });

        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        if (isInternetAvailable(context)) {
            GroupTask scheduleTask = new GroupTask();
            scheduleTask.execute(groupsRequest);
        } else {
            SharedPreferences sharedPreferences = getSharedPreferences("cacheGroups", MODE_PRIVATE);
            String json = sharedPreferences.getString(groupsRequest, "isEmpty");
            if (!json.equals("isEmpty")) {
                updateUI(json);
            }
        }
    }

    private void updateUI(String json) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Group.class, new GroupDeserializer());
        JsonElement jsonElement = new JsonParser().parse(json);
        JsonArray asJsonArray = jsonElement.getAsJsonArray();

        Group[] groups = gsonBuilder.create().fromJson(asJsonArray, Group[].class);
        processGroups(groups);

        GroupListAdapter groupAdapter = new GroupListAdapter(context, groupNames);
        groupListView.setAdapter(groupAdapter);
    }

    private void processGroups(Group[] groups) {
        groupNames = new String[groups.length];
        int i = 0;
        for (Group group : groups) {
            groupNames[i++] = group.getName();
        }
    }

    private class GroupTask extends ApiConnector {

        @Override
        protected void onPreExecute() {
            swipeLayout.setRefreshing(true);
        }

        @Override
        protected void onPostExecute(String json) {
            SharedPreferences sharedPreferences = getSharedPreferences("cacheGroups", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getUrl(), json);
            editor.apply();

            updateUI(json);
            swipeLayout.setRefreshing(false);
        }
    }
}
