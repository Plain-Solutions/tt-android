package org.ssutt.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.ssutt.android.R;
import org.ssutt.android.api.ApiConnector;
import org.ssutt.android.api.ApiRequests;
import org.ssutt.android.api.GroupMode;
import org.ssutt.android.deserializer.GroupDeserializer;
import org.ssutt.android.domain.Department;
import org.ssutt.android.domain.Group;

import java.util.concurrent.ExecutionException;

public class GroupActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_view);

        ListView groupListView = (ListView) findViewById(R.id.groupListView);

        final Department department = (Department) getIntent().getSerializableExtra("department");
        final Group[] groups = getGroups(department.getTag(), GroupMode.ONLY_FILLED);
        String[] groupNames = processGroups(groups);

        ListAdapter groupAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, groupNames);
        groupListView.setAdapter(groupAdapter);

        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(position + " " + groups[position].getName());

                Intent intent = new Intent(getApplicationContext(), ScheduleActivity.class);
                intent.putExtra("department", department.getTag());
                intent.putExtra("group", groups[position].getName());
                startActivity(intent);
            }
        });
    }

    private String[] processGroups(Group[] groups) {
        String[] groupNames = new String[groups.length];

        int i = 0;
        for (Group group : groups) {
            groupNames[i++] = group.getName();
        }

        return groupNames;
    }

    private Group[] getGroups(String department, GroupMode mode) {
        ApiConnector apiConnector = new ApiConnector();
        apiConnector.execute(ApiRequests.getGroups(department, mode));

        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Group.class, new GroupDeserializer());
            JsonElement jsonElement = new JsonParser().parse(apiConnector.get());
            JsonArray asJsonArray = jsonElement.getAsJsonArray();

            return gsonBuilder.create().fromJson(asJsonArray, Group[].class);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }
}
