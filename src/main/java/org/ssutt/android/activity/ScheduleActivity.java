package org.ssutt.android.activity;

import android.app.Activity;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.ssutt.android.R;
import org.ssutt.android.adapter.CustomAdapter;
import org.ssutt.android.api.ApiConnector;
import org.ssutt.android.api.ApiRequests;
import org.ssutt.android.deserializer.LessonDeserializer;
import org.ssutt.android.domain.Lesson.Lesson;
import org.ssutt.android.domain.Lesson.Subject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ScheduleActivity extends Activity {
    private static final String[] times = {"08:20 - 09:50", "10:00 - 11:35", "12:05 - 13:40", "13:50 - 15:25", "15:35 - 17:10", "17:20 - 18:40", "18:45 - 20:05", "20:10 - 21:30"};
    private TextView loadingTextView;
    private ListView scheduleListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_view);
        loadingTextView = (TextView) findViewById(R.id.loadingTextView);
        scheduleListView = (ListView) findViewById(R.id.scheduleListView);

        String department = getIntent().getStringExtra("department");
        String group = getIntent().getStringExtra("group");

        //Lesson[] schedule = getSchedule("knt", "151");
        //ScheduleListAdapter scheduleAdapter = new ScheduleListAdapter(getApplicationContext(), schedule);
        //scheduleListView.setAdapter(scheduleAdapter);

        if (ApiConnector.isInternetAvailable(this)) {
            ScheduleTask scheduleTask = new ScheduleTask();
            scheduleTask.execute(ApiRequests.getSchedule(department, group));
        } else {
            System.out.println("You have not internet connection!");
        }
    }

    private Lesson[] getSchedule(String department, String group) {
        AsyncTask<String, Integer, String> apiConnector = new ApiConnector();
        apiConnector.execute(ApiRequests.getSchedule(department, group));

        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Lesson.class, new LessonDeserializer());
            JsonElement jsonElement = new JsonParser().parse(apiConnector.get());
            JsonArray asJsonArray = jsonElement.getAsJsonArray();
            return gsonBuilder.create().fromJson(asJsonArray, Lesson[].class);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    private class ScheduleTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            loadingTextView.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
            final HttpGet getRequest = new HttpGet(params[0]);

            try {
                HttpResponse response = client.execute(getRequest);
                int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode != HttpStatus.SC_OK) {
                    Log.w("ImageDownloader", "Error " + statusCode + " while retrieving bitmap from " + params[0]);
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
                Log.w("ImageDownloader", "Error while retrieving bitmap from " + params[0]);
            } finally {
                client.close();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            loadingTextView.setVisibility(View.GONE);

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Lesson.class, new LessonDeserializer());
            JsonElement jsonElement = new JsonParser().parse(s);
            JsonArray asJsonArray = jsonElement.getAsJsonArray();

            Lesson[] lessons = gsonBuilder.create().fromJson(asJsonArray, Lesson[].class);

            Map<Integer, List<Lesson>> scheduleByDay = new HashMap<Integer, List<Lesson>>();
            for(Lesson lesson : lessons) {
                int day = lesson.getDay();
                if(!scheduleByDay.containsKey(day)) {
                    List<Lesson> lessonList = new ArrayList<Lesson>();
                    lessonList.add(lesson);
                    scheduleByDay.put(day, lessonList);
                } else {
                    scheduleByDay.get(day).add(lesson);
                }
            }

            System.out.println(Arrays.toString(lessons));
            CustomAdapter customAdapter = new CustomAdapter(getApplicationContext());

            for(Lesson lesson : scheduleByDay.get(0)) {
                String time = times[lesson.getSequence() - 1];
                customAdapter.addSectionHeaderItem(time);

                for (Subject subject : lesson.getSubject()) {
                    customAdapter.addItem(subject);
                }
            }
            scheduleListView.setAdapter(customAdapter);
        }
    }
}
