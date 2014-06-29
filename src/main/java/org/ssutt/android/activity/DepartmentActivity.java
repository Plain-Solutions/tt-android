package org.ssutt.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ssutt.android.R;
import org.ssutt.android.api.ApiConnector;
import org.ssutt.android.api.ApiRequests;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.ssutt.android.api.ApiConnector.isInternetAvailable;

public class DepartmentActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener, AdapterView.OnItemLongClickListener {
    private LinearLayout internetAvailableView;
    private Button tryAgainBtn;
    private ListView departmentsListView;

    private List<String> departmentTags;
    private List<String> departmentNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.department_view);
        init();
    }

    private void init() {
        internetAvailableView = (LinearLayout) findViewById(R.id.internetAvailableView);
        departmentsListView = (ListView) findViewById(R.id.departmentListView);
        tryAgainBtn = (Button) findViewById(R.id.tryAgainBtn);
        fillDepartmentsView();

        tryAgainBtn.setOnClickListener(this);
        departmentsListView.setOnItemClickListener(this);
        departmentsListView.setOnItemLongClickListener(this);
    }

    private void fillDepartmentsView() {
        if (isInternetAvailable(this)) {
            internetAvailableView.setVisibility(View.GONE);

            ApiConnector apiConnector = new ApiConnector();
            apiConnector.execute(ApiRequests.getDepartments());

            try {
                JSONArray departments = apiConnector.get();
                departmentTags = new ArrayList<String>();
                departmentNames = new ArrayList<String>();

                for (int i = 0; i < departments.length(); i++) {
                    JSONObject curDepartment = departments.getJSONObject(i);
                    departmentTags.add(curDepartment.getString("tag"));
                    departmentNames.add(curDepartment.getString("name"));
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, departmentNames);
                departmentsListView.setAdapter(adapter);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            internetAvailableView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        fillDepartmentsView();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(DepartmentActivity.this, GroupActivity.class);
        intent.putExtra("tag", departmentTags.get(position));
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return true;
    }
}
