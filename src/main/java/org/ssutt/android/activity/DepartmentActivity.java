package org.ssutt.android.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.ssutt.android.R;
import org.ssutt.android.activity.schedule_activity.ScheduleActivity;
import org.ssutt.android.adapter.DepartmentListAdapter;
import org.ssutt.android.api.ApiConnector;
import org.ssutt.android.api.ApiRequests;
import org.ssutt.android.deserializer.DepartmentDeserializer;
import org.ssutt.android.domain.Department;

import static org.ssutt.android.api.ApiConnector.isInternetAvailable;

public class DepartmentActivity extends ActionBarActivity {
    private static final String DEPARTMENT = "department";
    private static final String DEPARTMENT_FULL_NAME = "department_full_name";
    private static final String GROUP = "group";

    private ListView departmentListView;
    private SwipeRefreshLayout swipeLayout;
    private Department[] departments;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.department_view);
        context = this;

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        final boolean firstTime = pref.getBoolean("firstTime", true);
        boolean forSearch = getIntent().getBooleanExtra("forSearch", false);

        if (firstTime) {
            new AlertDialog.Builder(this)
                    .setTitle("Добро пожаловать!")
                    .setMessage("Выберите Ваш факультет и группу!")
                    .setPositiveButton("Далее", null)
                    .show();
        } else if (!forSearch) {
            String department = pref.getString("myDepartment", "");
            String group = pref.getString("myGroup", "");
            String departmentFullName = pref.getString("myDepartmentFullName", "");

            Intent intent = new Intent(context, ScheduleActivity.class);
            intent.putExtra(DEPARTMENT, department);
            intent.putExtra(GROUP, group);
            intent.putExtra(DEPARTMENT_FULL_NAME, departmentFullName);

            startActivity(intent);
            DepartmentActivity.this.finish();
        }


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle(getString(R.string.chooseDepartment));

        departmentListView = (ListView) findViewById(R.id.departmentListView);
        departmentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, GroupActivity.class);
                intent.putExtra(DEPARTMENT, departments[position]);
                startActivity(intent);
                if (firstTime) {
                    DepartmentActivity.this.finish();
                }
            }
        });

        final String departmentsRequest = ApiRequests.getDepartments();
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isInternetAvailable(context)) {
                    DepartmentTask scheduleTask = new DepartmentTask();
                    scheduleTask.execute(departmentsRequest);
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
            DepartmentTask departmentTask = new DepartmentTask();
            departmentTask.execute(departmentsRequest);
        } else {
            pref = getSharedPreferences("cacheDepartments", MODE_PRIVATE);
            String json = pref.getString(departmentsRequest, "isEmpty");
            if (!json.equals("isEmpty")) {
                updateUI(json);
            }
        }
    }

    private void updateUI(String json) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Department.class, new DepartmentDeserializer());
        JsonElement jsonElement = new JsonParser().parse(json);
        JsonArray asJsonArray = jsonElement.getAsJsonArray();

        departments = gsonBuilder.create().fromJson(asJsonArray, Department[].class);
        String[] departmentNames = processDepartments(departments);

        DepartmentListAdapter adapter = new DepartmentListAdapter(context, departmentNames);
        departmentListView.setAdapter(adapter);
    }

    private String[] processDepartments(Department... departments) {
        String[] departmentNames = new String[departments.length];

        for (int i = 0; i < departments.length; i++) {
            departmentNames[i] = departments[i].getName();
            departmentNames[i] = departmentNames[i].replaceAll("[Фф]акультет", "");
            departmentNames[i] = departmentNames[i].replaceAll("[Ии]нститут", "");
            departmentNames[i] = departmentNames[i].trim();

            StringBuilder sb = new StringBuilder(departmentNames[i]);
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
            departmentNames[i] = sb.toString();
        }

        return departmentNames;
    }

    private class DepartmentTask extends ApiConnector {
        @Override
        protected void onPreExecute() {
            swipeLayout.setRefreshing(true);
        }

        @Override
        public void onPostExecute(String json) {
            SharedPreferences sharedPreferences = getSharedPreferences("cacheDepartments", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getUrl(), json);
            editor.apply();

            updateUI(json);
            swipeLayout.setRefreshing(false);
        }
    }
}
