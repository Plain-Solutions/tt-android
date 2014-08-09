package org.ssutt.android.activity;

import android.app.Activity;
import android.os.Bundle;

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

public class TestActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.json_view);

        getDepartments();
        getMsg("knt");
        getGroups("knt", GroupMode.ONLY_FILLED);
        getSchedule("knt", "151");
    }

    private void getSchedule(String department, String group) {
        ApiConnector apiConnector = ApiConnector.getInstance();
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
        ApiConnector apiConnector = ApiConnector.getInstance();
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

    private void getMsg(String deprtment) {
        ApiConnector apiConnector = ApiConnector.getInstance();
        apiConnector.execute(ApiRequests.getDepartmentMsg(deprtment));

        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Message.class, new MessageDeserializer());
            JsonElement jsonElement = new JsonParser().parse(apiConnector.get());

            Message department = gsonBuilder.create().fromJson(jsonElement, Message.class);
            System.out.println(department.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void getGroups(String department, GroupMode mode) {
        ApiConnector apiConnector = ApiConnector.getInstance();
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
