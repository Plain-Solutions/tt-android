package org.ssutt.android.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.ssutt.android.R;
import org.ssutt.android.Typefaces;
import org.ssutt.android.activity.schedule_activity.ScheduleActivity;
import org.ssutt.android.adapter.GroupListAdapter;
import org.ssutt.android.api.ApiConnector;
import org.ssutt.android.api.GroupMode;
import org.ssutt.android.deserializer.GroupDeserializer;
import org.ssutt.android.domain.Department;
import org.ssutt.android.domain.Group;

import static org.ssutt.android.activity.Constants.CACHED_GROUPS;
import static org.ssutt.android.activity.Constants.DEPARTMENT;
import static org.ssutt.android.activity.Constants.DEPARTMENT_FULL_NAME;
import static org.ssutt.android.activity.Constants.FIRST_TIME;
import static org.ssutt.android.activity.Constants.GROUP;
import static org.ssutt.android.activity.Constants.MY_DEPARTMENT;
import static org.ssutt.android.activity.Constants.MY_DEPARTMENT_FULL_NAME;
import static org.ssutt.android.activity.Constants.MY_GROUP;
import static org.ssutt.android.activity.Constants.PREF;
import static org.ssutt.android.activity.Constants.STAR;
import static org.ssutt.android.api.ApiConnector.isInternetAvailable;
import static org.ssutt.android.api.ApiRequests.getGroups;

public class GroupActivity extends ActionBarActivity {
    private static GroupActivity instance;
    private SwipeRefreshLayout swipeLayout;
    private String[] groupNames;
    private ListView groupListView;
    private Context context;
    private Department department;

    public static GroupActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_view);
        groupListView = (ListView) findViewById(R.id.groupListView);
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        context = this;
        instance = this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            int actionBarTitleid = getResources().getIdentifier("action_bar_title", "id", "android");
            TextView actionBarTitle = (TextView) findViewById(actionBarTitleid);
            actionBarTitle.setTypeface(Typefaces.get(this, "fonts/helvetica-bold"));
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle(getString(R.string.chooseGroup));

        department = (Department) getIntent().getSerializableExtra(DEPARTMENT);
        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences pref = getSharedPreferences(PREF, MODE_PRIVATE);
                SharedPreferences staredPref = getSharedPreferences(STAR, MODE_PRIVATE);

                boolean firstTime = pref.getBoolean(FIRST_TIME, true);

                if (firstTime) {
                    SharedPreferences.Editor editor = pref.edit();
                    SharedPreferences.Editor staredEditor = staredPref.edit();

                    editor.putString(MY_DEPARTMENT, department.getTag());
                    editor.putString(MY_GROUP, groupNames[position]);
                    editor.putString(MY_DEPARTMENT_FULL_NAME, department.getName());
                    editor.putBoolean(FIRST_TIME, false);
                    editor.apply();

                    String tag = department.getTag() + "&" + groupNames[position] + "&" + department.getName();
                    staredEditor.putBoolean(tag, true);
                    staredEditor.apply();
                }

                Intent intent = new Intent(context, ScheduleActivity.class);
                intent.putExtra(DEPARTMENT, department.getTag());
                intent.putExtra(DEPARTMENT_FULL_NAME, department.getName());
                intent.putExtra(GROUP, groupNames[position]);


                if(ScheduleActivity.getInstance() != null) {
                    ScheduleActivity.getInstance().finish();
                }
                if(DepartmentActivity.getInstance() != null) {
                    DepartmentActivity.getInstance().finish();
                }
                GroupActivity.this.finish();

                startActivity(intent);
            }
        });

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshGroups();
            }
        });
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        refreshGroups();
    }

    private void refreshGroups() {
        final String groupsRequest = getGroups(department.getTag(), GroupMode.ALL);
        SharedPreferences sharedPreferences = getSharedPreferences(CACHED_GROUPS, MODE_PRIVATE);
        String cachedJson = sharedPreferences.getString(groupsRequest, "isEmpty");

        if (!cachedJson.equals("isEmpty")) {
            System.out.println("CACHED");
            updateUI(cachedJson);
        } else {
            System.out.println("NOT CACHED");
        }

        if (isInternetAvailable(context)) {
            System.out.println("DOWNLOADING FROM INTERNET");
            GroupTask scheduleTask = new GroupTask();
            scheduleTask.execute(groupsRequest);
        } else {
            System.out.println("Internet is not available!");
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

    private void updateUI(String json) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Group.class, new GroupDeserializer());
        JsonElement jsonElement = new JsonParser().parse(json);
        JsonArray asJsonArray = jsonElement.getAsJsonArray();

        Group[] groups = gsonBuilder.create().fromJson(asJsonArray, Group[].class);
        processGroups(groups);

        GroupListAdapter groupAdapter = new GroupListAdapter(context, groupNames);
        groupListView.setAdapter(groupAdapter);
        swipeLayout.setRefreshing(false);
    }

    private class GroupTask extends ApiConnector {

        @Override
        protected void onPreExecute() {
            swipeLayout.setRefreshing(true);
        }

        @Override
        protected void onPostExecute(String json) {
            if (!isValid(json)) {
                Toast.makeText(context, ApiConnector.errorMessages.get(json), Toast.LENGTH_LONG).show();
                swipeLayout.setRefreshing(false);
                return;
            }

            cacheGroup(json);
            updateUI(json);
        }

        private void cacheGroup(String json) {
            SharedPreferences sharedPreferences = getSharedPreferences(CACHED_GROUPS, MODE_PRIVATE);
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
