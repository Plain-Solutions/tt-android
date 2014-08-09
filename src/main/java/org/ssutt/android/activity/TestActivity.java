package org.ssutt.android.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.ssutt.android.R;
import org.ssutt.android.api.ApiConnector;
import org.ssutt.android.api.ApiRequests;
import org.ssutt.android.api.GroupMode;
import org.ssutt.android.deserializer.DepartmentDeserializer;
import org.ssutt.android.deserializer.GroupDeserializer;
import org.ssutt.android.deserializer.LessonDeserializer;
import org.ssutt.android.deserializer.MessageDeserializer;
import org.ssutt.android.domain.Department;
import org.ssutt.android.domain.Group;
import org.ssutt.android.domain.Lesson.Lesson;
import org.ssutt.android.domain.Message;

import java.util.concurrent.ExecutionException;

public class TestActivity extends Activity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.json_view);

        Button departmentsBtn = (Button) findViewById(R.id.departmentBtn);
        Button msgBtn = (Button) findViewById(R.id.msgBtn);
        Button groupsBtn = (Button) findViewById(R.id.groupsBtn);
        Button scheduleBtn = (Button) findViewById(R.id.scheduleBtn);

        departmentsBtn.setOnClickListener(this);
        msgBtn.setOnClickListener(this);
        groupsBtn.setOnClickListener(this);
        scheduleBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.departmentBtn:
                getDepartments();
                break;
            case R.id.msgBtn:
                getMsg("knt");
                break;
            case R.id.groupsBtn:
                getGroups("knt", GroupMode.ONLY_FILLED);
                break;
            case R.id.scheduleBtn:
                getSchedule("knt", "151");
                break;
        }
    }

    private void getSchedule(String department, String group) {
        ApiConnector apiConnector = new ApiConnector();
        apiConnector.execute(ApiRequests.getSchedule(department, group));

        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Lesson.class, new LessonDeserializer());
            JsonElement jsonElement = new JsonParser().parse(apiConnector.get());
            JsonArray asJsonArray = jsonElement.getAsJsonArray();

            Lesson[] lessons = gsonBuilder.create().fromJson(asJsonArray, Lesson[].class);
            for (Lesson cur : lessons) {
                System.out.println(cur);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void getDepartments() {
        ApiConnector apiConnector = new ApiConnector();
        apiConnector.execute(ApiRequests.getDepartments());

        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Department.class, new DepartmentDeserializer());
            JsonElement jsonElement = new JsonParser().parse(apiConnector.get());
            JsonArray asJsonArray = jsonElement.getAsJsonArray();

            Department[] department = gsonBuilder.create().fromJson(asJsonArray, Department[].class);
            for (Department cur : department) {
                System.out.println(cur);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void getMsg(String departmentTag) {
        ApiConnector apiConnector = new ApiConnector();
        apiConnector.execute(ApiRequests.getDepartmentMsg(departmentTag));

        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Message.class, new MessageDeserializer());
            JsonElement jsonElement = new JsonParser().parse(apiConnector.get());

            Message department = gsonBuilder.create().fromJson(jsonElement, Message.class);
            System.out.println(department);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    private void getGroups(String department, GroupMode mode) {
        ApiConnector apiConnector = new ApiConnector();
        apiConnector.execute(ApiRequests.getGroups(department, mode));

        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Group.class, new GroupDeserializer());
            JsonElement jsonElement = new JsonParser().parse(apiConnector.get());
            JsonArray asJsonArray = jsonElement.getAsJsonArray();

            Group[] groups = gsonBuilder.create().fromJson(asJsonArray, Group[].class);
            for (Group cur : groups) {
                System.out.println(cur.getName());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
