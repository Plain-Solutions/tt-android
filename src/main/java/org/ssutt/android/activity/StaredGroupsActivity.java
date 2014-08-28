package org.ssutt.android.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;

import org.ssutt.android.R;
import org.ssutt.android.adapter.StaredGroupsListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class StaredGroupsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stared_groups_view);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle(getString(R.string.staredGroups));

        ListView staredGroupsListView = (ListView) findViewById(R.id.staredGroupView);
        StaredGroupsListAdapter staredGroupsListAdapter = new StaredGroupsListAdapter(getApplicationContext(), getStarredGroups());
        staredGroupsListView.setAdapter(staredGroupsListAdapter);
    }

    private List<String> getStarredGroups() {
        List<String> staredGroups = new ArrayList<String>();
        SharedPreferences pref = getSharedPreferences("star", MODE_PRIVATE);

        Map<String, ?> allGroups = pref.getAll();
        System.out.println("ALL MAP: " + allGroups);
        for (Map.Entry<String, ?> entry : allGroups.entrySet()) {
            if(entry.getValue().equals(Boolean.TRUE)) {
                String tag = entry.getKey();

                String[] split = tag.split("&");
                System.out.println(Arrays.toString(split));
                staredGroups.add(split[0]);
                staredGroups.add(split[1]);
            }
        }

        return staredGroups;
    }
}
