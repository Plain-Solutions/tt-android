package org.ssutt.android.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.ssutt.android.R;
import org.ssutt.android.domain.Lesson.Lesson;
import org.ssutt.android.domain.Lesson.Subgroup;
import org.ssutt.android.domain.Lesson.Subject;

import java.util.List;

public class ScheduleListAdapter extends ArrayAdapter<Lesson> {
    private static final String[] times = {"08:20 - 09:50", "10:00 - 11:35", "12:05 - 13:40", "13:50 - 15:25", "15:35 - 17:10", "17:20 - 18:40", "18:45 - 20:05", "20:10 - 21:30"};

    public ScheduleListAdapter(Context context, Lesson[] lessons) {
        super(context, R.layout.schedule_view_list_item, lessons);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater vi = LayoutInflater.from(getContext());
            view = vi.inflate(R.layout.schedule_view_list_item, null);
        }

        Lesson item = getItem(position);
        if (item != null) {
            Subject subject = item.getSubject().get(0);
            List<Subgroup> subgroup = subject.getSubgroup();

            View lectureColorView = view.findViewById(R.id.lectureTypeView);
            if (subject.getActivity().equals("practice")) {
                lectureColorView.setBackgroundColor(Color.parseColor("#4cd964"));
            } else {
                lectureColorView.setBackgroundColor(Color.parseColor("#ff5e3a"));
            }

            TextView timeTextView = (TextView) view.findViewById(R.id.timeTextView);
            TextView subjectTextView = (TextView) view.findViewById(R.id.subjectTextView);
            TextView locationTextView = (TextView) view.findViewById(R.id.locationTextView);
            TextView typeTextView = (TextView) view.findViewById(R.id.subjectTypeTextView);

            timeTextView.setText(times[item.getSequence() - 1]);
            subjectTextView.setText(subject.getName());
            locationTextView.setText(subgroup.size() == 1 ? subgroup.get(0).getLocation() : "Multiply value");
            typeTextView.setText(subject.getActivity());
        }

        return view;
    }
}
