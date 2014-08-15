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
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.ssutt.android.R;
import org.ssutt.android.adapter.DepartmentListAdapter;
import org.ssutt.android.api.ApiConnector;
import org.ssutt.android.api.ApiRequests;
import org.ssutt.android.deserializer.DepartmentDeserializer;
import org.ssutt.android.domain.Department;

import static org.ssutt.android.api.ApiConnector.*;
import static org.ssutt.android.api.ApiConnector.errorToast;

public class DepartmentActivity extends Activity {
    private static final String DEPARTMENT = "department";

    private ListView departmentListView;
    private SwipeRefreshLayout swipeLayout;
    private Department[] departments;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.department_view);
        departmentListView = (ListView) findViewById(R.id.departmentListView);
        context = this;

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle(getString(R.string.chooseDepartment));

        departmentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, GroupActivity.class);
                intent.putExtra(DEPARTMENT, departments[position]);
                startActivity(intent);
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
                    errorToast(context);
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
            errorToast(context);

            SharedPreferences sharedPreferences = getSharedPreferences("cacheDepartments", MODE_PRIVATE);
            String json = sharedPreferences.getString(departmentsRequest, "isEmpty");
            if(!json.equals("isEmpty")) {
                updateUI(json);
                cacheToast(context);
            } else {
                cacheNoFoundToast(context);
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
            editor.commit();

            updateUI(json);
            swipeLayout.setRefreshing(false);
        }
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
}
