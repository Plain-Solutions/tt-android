package org.ssutt.android.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.ssutt.android.R;
import org.ssutt.android.api.ApiConnector;
import org.ssutt.android.api.ApiRequests;
import org.ssutt.android.api.GroupMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class GroupActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener{
    private LinearLayout internetAvailableView;
    private Button tryAgainBtn;
    private ListView groupListView;

    private List<String> groups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_view);
        init();
    }

    private void init() {
        internetAvailableView = (LinearLayout) findViewById(R.id.internetAvailableView);
        tryAgainBtn = (Button) findViewById(R.id.tryAgainBtn);
        groupListView = (ListView) findViewById(R.id.groupListView);
        fillGroupView(getIntent().getStringExtra("tag"));

        tryAgainBtn.setOnClickListener(this);
        groupListView.setOnItemClickListener(this);
    }

    private void fillGroupView(String departmentTag) {
        if(ApiConnector.isInternetAvailable(this)) {
            internetAvailableView.setVisibility(View.GONE);

            ApiConnector apiConnector = new ApiConnector();
            apiConnector.execute(ApiRequests.getGroups(departmentTag, GroupMode.ONLY_FILLED));
            System.out.println(ApiRequests.getGroups(departmentTag, GroupMode.ONLY_FILLED));

            try {
                JSONArray groupsJson = apiConnector.get();
                groups = new ArrayList<String>();
                for (int i = 0; i < groupsJson.length(); i++) {
                    groups.add(groupsJson.getString(i));
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, groups);
                groupListView.setAdapter(arrayAdapter);
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
        fillGroupView(getIntent().getStringExtra("tag"));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
