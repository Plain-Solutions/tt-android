package org.ssutt.android.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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
import org.ssutt.android.adapter.DepartmentListAdapter;
import org.ssutt.android.api.ApiConnector;
import org.ssutt.android.api.ApiRequests;
import org.ssutt.android.deserializer.DepartmentDeserializer;
import org.ssutt.android.domain.Department;

import static org.ssutt.android.activity.Constants.CACHED_DEPARTMENTS;
import static org.ssutt.android.activity.Constants.DEPARTMENT;
import static org.ssutt.android.activity.Constants.DEPARTMENT_FULL_NAME;
import static org.ssutt.android.activity.Constants.FIRST_TIME;
import static org.ssutt.android.activity.Constants.FOR_SEARCH;
import static org.ssutt.android.activity.Constants.GROUP;
import static org.ssutt.android.activity.Constants.MY_DEPARTMENT;
import static org.ssutt.android.activity.Constants.MY_DEPARTMENT_FULL_NAME;
import static org.ssutt.android.activity.Constants.MY_GROUP;
import static org.ssutt.android.activity.Constants.PREF;
import static org.ssutt.android.api.ApiConnector.*;
import static org.ssutt.android.api.ApiConnector.isInternetAvailable;

public class DepartmentActivity extends ActionBarActivity {
    private static DepartmentActivity instance;
    private ListView departmentListView;
    private SwipeRefreshLayout swipeLayout;
    private Department[] departments;
    private Context context;

    public static DepartmentActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.department_view);
        instance = this;
        context = this;

        SharedPreferences pref = getSharedPreferences(PREF, MODE_PRIVATE);
        final boolean firstTime = pref.getBoolean(FIRST_TIME, true);
        boolean forSearch = getIntent().getBooleanExtra(FOR_SEARCH, false);

        if (firstTime) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.welcomeTitle))
                    .setMessage(getString(R.string.welcomeText))
                    .setPositiveButton(getString(R.string.welcomeClose), null)
                    .show();

            Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/helvetica-light.otf");

            int textViewId = dialog.getContext().getResources().getIdentifier("android:id/alertTitle", null, null);
            TextView titleTextView = (TextView) dialog.findViewById(textViewId);
            TextView messageTextView = (TextView) dialog.findViewById(android.R.id.message);
            Button btnPositive = (Button) dialog.findViewById(android.R.id.button1);

            titleTextView.setTypeface(typeface);
            messageTextView.setTypeface(typeface);
            btnPositive.setTypeface(typeface);

        } else if (!forSearch) {
            String department = pref.getString(MY_DEPARTMENT, "");
            String group = pref.getString(MY_GROUP, "");
            String departmentFullName = pref.getString(MY_DEPARTMENT_FULL_NAME, "");

            Intent intent = new Intent(context, ScheduleActivity.class);
            intent.putExtra(DEPARTMENT, department);
            intent.putExtra(GROUP, group);
            intent.putExtra(DEPARTMENT_FULL_NAME, departmentFullName);

            startActivity(intent);
            DepartmentActivity.this.finish();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            int actionBarTitleid = getResources().getIdentifier("action_bar_title", "id", "android");
            TextView actionBarTitle = (TextView) findViewById(actionBarTitleid);
            actionBarTitle.setTypeface(Typefaces.get(this, "fonts/helvetica-bold"));
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
            }
        });


        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshDepartments();
            }
        });

        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        refreshDepartments();
    }

    private void refreshDepartments() {
        String departmentsRequest = ApiRequests.getDepartments();
        SharedPreferences pref = getSharedPreferences(CACHED_DEPARTMENTS, MODE_PRIVATE);
        String cachedJson = pref.getString(departmentsRequest, "isEmpty");

        if (!cachedJson.equals("isEmpty")) {
            System.out.println("CACHED");
            updateUI(cachedJson);
        } else {
            System.out.println("NOT CACHED");
        }

        if (isInternetAvailable(context)) {
            System.out.println("DOWNLOADING FROM INTERNET");
            DepartmentTask departmentTask = new DepartmentTask();
            departmentTask.execute(departmentsRequest);
        } else {
            System.out.println("Internet is not available!");
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

    private void updateUI(String json) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Department.class, new DepartmentDeserializer());
        JsonElement jsonElement = new JsonParser().parse(json);
        JsonArray asJsonArray = jsonElement.getAsJsonArray();

        departments = gsonBuilder.create().fromJson(asJsonArray, Department[].class);
        String[] departmentNames = processDepartments(departments);

        DepartmentListAdapter adapter = new DepartmentListAdapter(context, departmentNames);
        departmentListView.setAdapter(adapter);
        swipeLayout.setRefreshing(false);
    }

    private class DepartmentTask extends ApiConnector {
        @Override
        protected void onPreExecute() {
            swipeLayout.setRefreshing(true);
        }

        @Override
        public void onPostExecute(String json) {
            if (!isValid(json)) {
                Toast.makeText(context, errorMessages.get(json), Toast.LENGTH_LONG).show();
                swipeLayout.setRefreshing(false);
                return;
            }

            cacheDepartment(json);
            updateUI(json);
        }

        private void cacheDepartment(String json) {
            SharedPreferences sharedPreferences = getSharedPreferences(CACHED_DEPARTMENTS, MODE_PRIVATE);
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
