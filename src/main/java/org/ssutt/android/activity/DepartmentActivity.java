package org.ssutt.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import org.ssutt.android.deserializer.LessonDeserializer;
import org.ssutt.android.deserializer.MessageDeserializer;
import org.ssutt.android.domain.Department;
import org.ssutt.android.domain.Lesson.Lesson;
import org.ssutt.android.domain.Message;

import java.util.concurrent.ExecutionException;

public class DepartmentActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.department_view);
        ListView departmentListView = (ListView) findViewById(R.id.departmentListView);

        if(ApiConnector.isInternetAvailable(this)) {
            final Department[] departments = getDepartments();
            String[] departmentNames = processDepartments(departments);

            DepartmentListAdapter adapter = new DepartmentListAdapter(this, departmentNames);
            departmentListView.setAdapter(adapter);

            departmentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    System.out.println(position + " " + departments[position].getTag());

                    Intent intent = new Intent(getApplicationContext(), GroupActivity.class);
                    intent.putExtra("department", departments[position]);
                    startActivity(intent);
                }
            });
        } else {
            Toast.makeText(this, "You have not internter connection!", Toast.LENGTH_LONG).show();
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



    private Department[] getDepartments() {
        ApiConnector apiConnector = new ApiConnector();
        apiConnector.execute(ApiRequests.getDepartments());

        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Department.class, new DepartmentDeserializer());
            JsonElement jsonElement = new JsonParser().parse(apiConnector.get());
            JsonArray asJsonArray = jsonElement.getAsJsonArray();
            System.out.println(asJsonArray);
            return gsonBuilder.create().fromJson(asJsonArray, Department[].class);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Message getMsg(String departmentTag) {
        ApiConnector apiConnector = new ApiConnector();
        apiConnector.execute(ApiRequests.getDepartmentMsg(departmentTag));

        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Message.class, new MessageDeserializer());
            JsonElement jsonElement = new JsonParser().parse(apiConnector.get());

            return gsonBuilder.create().fromJson(jsonElement, Message.class);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
