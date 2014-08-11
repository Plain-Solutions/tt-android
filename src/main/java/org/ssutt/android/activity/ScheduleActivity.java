package org.ssutt.android.activity;

import android.app.Activity;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.ssutt.android.R;
import org.ssutt.android.adapter.ScheduleListAdapter;
import org.ssutt.android.api.ApiConnector;
import org.ssutt.android.api.ApiRequests;
import org.ssutt.android.deserializer.LessonDeserializer;
import org.ssutt.android.domain.Lesson.Lesson;
import org.ssutt.android.domain.Lesson.Subject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ScheduleActivity extends Activity {
    private static final String[] times = {"08:20 - 09:50", "10:00 - 11:35", "12:05 - 13:40", "13:50 - 15:25", "15:35 - 17:10", "17:20 - 18:40", "18:45 - 20:05", "20:10 - 21:30"};
    private static final String[] day = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятницы", "Суббота"};
    private ListView scheduleListView;
    private SwipeRefreshLayout swipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_view);
        scheduleListView = (ListView) findViewById(R.id.scheduleListView);

        final String department = getIntent().getStringExtra("department");
        final String group = getIntent().getStringExtra("group");

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (ApiConnector.isInternetAvailable(getApplicationContext())) {
                    ScheduleTask scheduleTask = new ScheduleTask();
                    scheduleTask.execute(ApiRequests.getSchedule(department, group));
                } else {
                    Toast.makeText(getApplicationContext(), "You have not internet connection!", Toast.LENGTH_LONG).show();
                    swipeLayout.setRefreshing(false);
                }
            }
        });

        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        if (ApiConnector.isInternetAvailable(this)) {
            ScheduleTask scheduleTask = new ScheduleTask();
            scheduleTask.execute(ApiRequests.getSchedule(department, group));
        } else {
            System.out.println("You have not internet connection!");
        }
    }

    private class ScheduleTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
            final HttpGet getRequest = new HttpGet(params[0]);

            try {
                HttpResponse response = client.execute(getRequest);
                int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode != HttpStatus.SC_OK) {
                    return null;
                }

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream inputStream = null;
                    try {
                        inputStream = entity.getContent();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                        StringBuilder json = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            json.append(line);
                        }

                        return json.toString();
                    } finally {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        entity.consumeContent();
                    }
                }
            } catch (Exception e) {
                getRequest.abort();
            } finally {
                client.close();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Lesson.class, new LessonDeserializer());
            JsonElement jsonElement = new JsonParser().parse(s);
            JsonArray asJsonArray = jsonElement.getAsJsonArray();

            Lesson[] lessons = gsonBuilder.create().fromJson(asJsonArray, Lesson[].class);

            Map<Integer, List<Lesson>> scheduleByDay = new TreeMap<Integer, List<Lesson>>();
            for (Lesson lesson : lessons) {
                int day = lesson.getDay();
                if (!scheduleByDay.containsKey(day)) {
                    List<Lesson> lessonList = new ArrayList<Lesson>();
                    lessonList.add(lesson);
                    scheduleByDay.put(day, lessonList);
                } else {
                    scheduleByDay.get(day).add(lesson);
                }
            }

            ScheduleListAdapter scheduleListAdapter = new ScheduleListAdapter(getApplicationContext());

            for (Lesson lesson : scheduleByDay.get(0)) {
                String time = times[lesson.getSequence() - 1];
                scheduleListAdapter.addSectionHeaderItem(time);

                for (Subject subject : lesson.getSubject()) {
                    scheduleListAdapter.addItem(subject);
                }
            }

            scheduleListView.setAdapter(scheduleListAdapter);
            swipeLayout.setRefreshing(false);
        }
    }
}
