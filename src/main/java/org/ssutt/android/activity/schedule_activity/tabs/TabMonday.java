package org.ssutt.android.activity.schedule_activity.tabs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.ssutt.android.R;
import org.ssutt.android.adapter.ScheduleListAdapter;
import org.ssutt.android.api.ApiConnector;
import org.ssutt.android.api.ApiRequests;
import org.ssutt.android.deserializer.LessonDeserializer;
import org.ssutt.android.domain.Lesson.Lesson;
import org.ssutt.android.domain.Lesson.Subject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TabMonday extends Fragment {
    private static final String[] times = {"08:20 - 09:50", "10:00 - 11:35", "12:05 - 13:40", "13:50 - 15:25", "15:35 - 17:10", "17:20 - 18:40", "18:45 - 20:05", "20:10 - 21:30"};
    private ListView scheduleListView;
    private SwipeRefreshLayout swipeLayout;
    private Context context;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        View view = inflater.inflate(R.layout.tab_monday, container, false);
        context = getActivity().getApplicationContext();
        scheduleListView = (ListView) view.findViewById(R.id.scheduleListView);
        ;

        Intent intent = getActivity().getIntent();
        final String department = intent.getStringExtra("department");
        final String group = intent.getStringExtra("group");

        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (ApiConnector.isInternetAvailable(context)) {
                    ScheduleTask scheduleTask = new ScheduleTask();
                    scheduleTask.execute(ApiRequests.getSchedule(department, group));
                } else {
                    Toast.makeText(context, "You have not internet connection!", Toast.LENGTH_LONG).show();
                    swipeLayout.setRefreshing(false);
                }
            }
        });

        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        if (ApiConnector.isInternetAvailable(context)) {
            ScheduleTask scheduleTask = new ScheduleTask();
            scheduleTask.execute(ApiRequests.getSchedule(department, group));
        } else {
            Toast.makeText(context, "You have not internet connection!", Toast.LENGTH_LONG).show();
        }

        return view;
    }

    private class ScheduleTask extends ApiConnector {
        @Override
        protected void onPreExecute() {
            swipeLayout.setRefreshing(true);
        }

        @Override
        public void onPostExecute(String s) {
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

            ScheduleListAdapter scheduleListAdapter = new ScheduleListAdapter(context);

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