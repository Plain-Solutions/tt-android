package org.ssutt.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.ssutt.android.R;
import org.ssutt.android.domain.Lesson.Subject;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class ScheduleListAdapter extends BaseAdapter {
    public static final int TYPE_ITEM = 0;
    public static final int TYPE_SEPARATOR = 1;

    private Context context;
    private LayoutInflater inflater;
    private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();
    private List data = new ArrayList();

    public ScheduleListAdapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public Subject getItemFromData(int position) {
        return (Subject) data.get(position);
    }

    public void addItem(Subject item) {
        data.add(item);
        notifyDataSetChanged();
    }

    public void addSectionHeaderItem(final String item) {
        data.add(item);
        sectionHeader.add(data.size() - 1);
        notifyDataSetChanged();
    }

    public void clear() {
        data = new ArrayList();
    }

    @Override
    public int getItemViewType(int position) {
        return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public String getItem(int position) {
        return data.get(position).toString();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        switch (getItemViewType(position)) {
            case TYPE_ITEM:
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.schedule_view_list_item, null);
                }

                TextView subjectTextView = (TextView) convertView.findViewById(R.id.subjectTextView);
                TextView locationTextView = (TextView) convertView.findViewById(R.id.locationTextView);
                TextView subjectTypeTextView = (TextView) convertView.findViewById(R.id.subjectTypeTextView);
                View lectureTypeView = convertView.findViewById(R.id.lectureTypeView);

                Subject subject = (Subject) data.get(position);
                subjectTextView.setText(subject.getName());
                locationTextView.setText(subject.getSubgroup().size() == 1 ? subject.getSubgroup().get(0).getLocation() : context.getString(R.string.multiplyValue));

                String activity = subject.getActivity();
                String subjectTypeText;
                int lectureTypeColor;
                int backgroundColor;

                if (activity.equals("practice")) {
                    subjectTypeText = context.getString(R.string.practice);
                    lectureTypeColor = context.getResources().getColor(R.color.green);
                    backgroundColor = R.drawable.background_green;
                } else if (activity.equals("lecture")) {
                    subjectTypeText = context.getString(R.string.lecture);
                    lectureTypeColor = context.getResources().getColor(R.color.red);
                    backgroundColor = R.drawable.background_red;
                } else {
                    subjectTypeText = context.getString(R.string.lab);
                    lectureTypeColor = context.getResources().getColor(R.color.blue);
                    backgroundColor = R.drawable.background_blue;
                }

                subjectTypeTextView.setText(subjectTypeText);
                lectureTypeView.setBackgroundColor(lectureTypeColor);
                subjectTypeTextView.setBackgroundResource(backgroundColor);
                break;

            case TYPE_SEPARATOR:
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.schedule_view_list_header, null);
                }

                TextView timeTextView = (TextView) convertView.findViewById(R.id.textView);

                String time = (String) data.get(position);
                timeTextView.setText(time);
                break;
        }

        return convertView;
    }
}