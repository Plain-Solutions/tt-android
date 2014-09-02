package org.ssutt.android.activity.schedule_activity.tabs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.ssutt.android.R;
import org.ssutt.android.activity.SubjectDetailsActivity;
import org.ssutt.android.adapter.ScheduleListAdapter;
import org.ssutt.android.api.ApiConnector;
import org.ssutt.android.api.ApiRequests;
import org.ssutt.android.deserializer.LessonDeserializer;
import org.ssutt.android.domain.Lesson.Lesson;
import org.ssutt.android.domain.Lesson.Subgroup;
import org.ssutt.android.domain.Lesson.Subject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static android.content.Context.MODE_PRIVATE;
import static org.ssutt.android.activity.Constants.CACHED_SCHEDULE;
import static org.ssutt.android.activity.Constants.DEPARTMENT;
import static org.ssutt.android.activity.Constants.GROUP;
import static org.ssutt.android.activity.Constants.PREF;
import static org.ssutt.android.activity.schedule_activity.tabs.DayType.DENOMINATOR;
import static org.ssutt.android.activity.schedule_activity.tabs.DayType.FULL;
import static org.ssutt.android.activity.schedule_activity.tabs.DayType.NUMERATOR;
import static org.ssutt.android.adapter.ScheduleListAdapter.TYPE_ITEM;
import static org.ssutt.android.api.ApiConnector.*;
import static org.ssutt.android.api.ApiConnector.isInternetAvailable;

public abstract class AbstractTab extends Fragment {
    private static final String[] times = {"08:20 - 09:50", "10:00 - 11:35", "12:05 - 13:40", "13:50 - 15:25", "15:35 - 17:10", "17:20 - 18:40", "18:45 - 20:05", "20:10 - 21:30"};
    private static final String SUBJECT_DETAILS = "subject_details";
    private static String department;
    private static String group;

    private Map<Integer, List<Lesson>> scheduleByDay;
    private Map<Subject, Integer> lessonBySubject;
    private ListView scheduleListView;
    private SwipeRefreshLayout swipeLayout;
    private Context context;

    public abstract int getDayOfWeek();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        context = getActivity().getApplicationContext();
        View view = inflater.inflate(R.layout.schedule_view_list, container, false);
        scheduleListView = (ListView) view.findViewById(R.id.scheduleListView);

        Intent intentGetData = getActivity().getIntent();
        department = intentGetData.getStringExtra(DEPARTMENT);
        group = intentGetData.getStringExtra(GROUP);

        scheduleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getAdapter().getItemViewType(position) == TYPE_ITEM) {
                    Intent intent = new Intent(context, SubjectDetailsActivity.class);

                    ScheduleListAdapter adapter = (ScheduleListAdapter) parent.getAdapter();
                    ArrayList<String> data = getSubjectDetails(adapter.getItemFromData(position));
                    intent.putStringArrayListExtra(SUBJECT_DETAILS, data);

                    startActivity(intent);
                }
            }
        });

        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshSchedule(department, group);
            }
        });

        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        refreshSchedule(department, group);
        return view;
    }

    public void refreshSchedule(String department, String group) {
        DayType dayType;
        SharedPreferences preferences = context.getSharedPreferences(PREF, MODE_PRIVATE);
        if (preferences.getBoolean("btnNumerator", false)) {
            dayType = NUMERATOR;
        } else {
            dayType = DENOMINATOR;
        }

        String scheduleRequest = ApiRequests.getSchedule(department, group);
        SharedPreferences sharedPreferences = context.getSharedPreferences(CACHED_SCHEDULE, MODE_PRIVATE);
        String json = sharedPreferences.getString(scheduleRequest, "isEmpty");

        if (!json.equals("isEmpty")) {
            System.out.println("CACHED");
            updateUI(dayType, json);
        } else {
            System.out.println("NOT CACHED");
        }

        if (isInternetAvailable(context)) {
            System.out.println("DOWNLOADING FROM INTERNET");
            ScheduleTask scheduleTask = new ScheduleTask(dayType);
            scheduleTask.execute(scheduleRequest);
        } else {
            System.out.println("Internet is not available!");
            swipeLayout.setRefreshing(false);
        }
    }

    public void refreshSchedule(Context context, DayType dayType, String department, String group) {
        String scheduleRequest = ApiRequests.getSchedule(department, group);
        SharedPreferences sharedPreferences = context.getSharedPreferences(CACHED_SCHEDULE, MODE_PRIVATE);
        String json = sharedPreferences.getString(scheduleRequest, "isEmpty");

        if (!json.equals("isEmpty")) {
            System.out.println("CACHED");
            updateUI(dayType, json);
        } else {
            System.out.println("NOT CACHED");
        }

        if (isInternetAvailable(context)) {
            System.out.println("DOWNLOADING FROM INTERNET");
            ScheduleTask scheduleTask = new ScheduleTask(dayType);
            scheduleTask.execute(scheduleRequest);
        } else {
            System.out.println("Internet is not available!");
            swipeLayout.setRefreshing(false);
        }
    }

    private ArrayList<String> getSubjectDetails(Subject subject) {
        ArrayList<String> subjectDetails = new ArrayList<String>();
        subjectDetails.add(subject.getName());

        String activity = subject.getActivity();
        subjectDetails.add(
                activity.equals("lecture") ? context.getString(R.string.lecture) :
                        activity.equals("practice") ? context.getString(R.string.practice) :
                                context.getString(R.string.lab)
        );

        Lesson lesson = scheduleByDay.get(getDayOfWeek()).get(lessonBySubject.get(subject));
        subjectDetails.add(times[lesson.getSequence() - 1]);

        int parity = subject.getParity();
        subjectDetails.add(
                parity == 0 ? context.getString(R.string.numerator) :
                        parity == 1 ? context.getString(R.string.denominator) :
                                context.getString(R.string.both)
        );

        for (Subgroup subgroup : subject.getSubgroup()) {
            StringBuilder subgroupDetails = new StringBuilder();
            subgroupDetails
                    .append(subgroup.getTeacher())
                    .append("\n")
                    .append(subgroup.getLocation())
                    .append("\n")
                    .append(subgroup.getSubgroup());
            subjectDetails.add(subgroupDetails.toString());
        }

        return subjectDetails;
    }

    private void updateUI(DayType dayType, String json) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Lesson.class, new LessonDeserializer());
        JsonElement jsonElement = new JsonParser().parse(json);
        JsonArray asJsonArray = jsonElement.getAsJsonArray();

        Lesson[] lessons = gsonBuilder.create().fromJson(asJsonArray, Lesson[].class);

        scheduleByDay = new TreeMap<Integer, List<Lesson>>();
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

        lessonBySubject = new HashMap<Subject, Integer>();
        if (scheduleByDay.get(getDayOfWeek()) == null) {
            scheduleListAdapter.addSectionHeaderItem(context.getString(R.string.emptyDay));
        } else {
            int i = 0;
            for (Lesson lesson : scheduleByDay.get(getDayOfWeek())) {
                boolean isTimeAdded = false;

                for (Subject subject : lesson.getSubject()) {
                    int parity = subject.getParity();
                    if (parity == dayType.ordinal() || parity == FULL.ordinal()) {
                        if (!isTimeAdded) {
                            String time = times[lesson.getSequence() - 1];
                            scheduleListAdapter.addSectionHeaderItem(time);
                            isTimeAdded = true;
                        }

                        scheduleListAdapter.addItem(subject);
                        lessonBySubject.put(subject, i);
                    }
                }
                ++i;
            }
        }

        scheduleListView.setAdapter(scheduleListAdapter);
        swipeLayout.setRefreshing(false);
    }

    private class ScheduleTask extends ApiConnector {
        DayType dayType;

        public ScheduleTask(DayType dayType) {
            this.dayType = dayType;
        }

        @Override
        protected void onPreExecute() {
            swipeLayout.setRefreshing(true);
        }

        @Override
        public void onPostExecute(String json) {
            if(!isValid(json)) {
                Toast.makeText(context, errorMessages.get(json), Toast.LENGTH_LONG).show();
                swipeLayout.setRefreshing(false);
                return;
            }

            cacheSchedule(json);
            updateUI(dayType, json);
        }

        private void cacheSchedule(String json) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(CACHED_SCHEDULE, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getUrl(), json);
            editor.apply();
        }
    }
}