package org.ssutt.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.ssutt.android.R;
import org.ssutt.android.activity.schedule_activity.ScheduleActivity;
import org.ssutt.android.activity.schedule_activity.ViewPagerFragmentActivity;
import org.ssutt.android.adapter.GroupListAdapter;
import org.ssutt.android.api.ApiConnector;
import org.ssutt.android.api.ApiRequests;
import org.ssutt.android.api.GroupMode;
import org.ssutt.android.deserializer.GroupDeserializer;
import org.ssutt.android.domain.Department;
import org.ssutt.android.domain.Group;

public class GroupActivity extends Activity {
    private SwipeRefreshLayout swipeLayout;
    private String[] groupNames;
    private ListView groupListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_view);
        groupListView = (ListView) findViewById(R.id.groupListView);
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);

        final Department department = (Department) getIntent().getSerializableExtra("department");
        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Intent intent = new Intent(getApplicationContext(), ScheduleActivity.class);
                Intent intent = new Intent(getApplicationContext(), ViewPagerFragmentActivity.class);
                intent.putExtra("department", department.getTag());
                intent.putExtra("group", groupNames[position]);
                startActivity(intent);
            }
        });

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (ApiConnector.isInternetAvailable(getApplicationContext())) {
                    GroupTask scheduleTask = new GroupTask();
                    scheduleTask.execute(ApiRequests.getGroups(department.getTag(), GroupMode.ONLY_FILLED));
                } else {
                    Toast.makeText(getApplicationContext(), "You have not internet connection!", Toast.LENGTH_LONG).show();
                    swipeLayout.setRefreshing(false);
                }
            }
        });

        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        if (ApiConnector.isInternetAvailable(this)) {
            GroupTask scheduleTask = new GroupTask();
            scheduleTask.execute(ApiRequests.getGroups(department.getTag(), GroupMode.ONLY_FILLED));
        } else {
            Toast.makeText(getApplicationContext(), "You have not internet connection!", Toast.LENGTH_LONG).show();
        }
    }

    private class GroupTask extends ApiConnector {
        @Override
        protected void onPreExecute() {
            swipeLayout.setRefreshing(true);
        }

        @Override
        protected void onPostExecute(String s) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Group.class, new GroupDeserializer());
            JsonElement jsonElement = new JsonParser().parse(s);
            JsonArray asJsonArray = jsonElement.getAsJsonArray();

            Group[] groups = gsonBuilder.create().fromJson(asJsonArray, Group[].class);
            processGroups(groups);

            GroupListAdapter groupAdapter = new GroupListAdapter(getApplicationContext(), groupNames);
            groupListView.setAdapter(groupAdapter);
            swipeLayout.setRefreshing(false);
        }
    }

    private void processGroups(Group[] groups) {
        groupNames = new String[groups.length];
        int i = 0;
        for (Group group : groups) {
            groupNames[i++] = group.getName();
        }
    }
}
