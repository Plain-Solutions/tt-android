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

public class CustomAdapter extends BaseAdapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;

    private List mData = new ArrayList();
    private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();

    private LayoutInflater mInflater;

    public CustomAdapter(Context context) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItem(Subject item) {
        mData.add(item);
        notifyDataSetChanged();
    }

    public void addSectionHeaderItem(final String item) {
        mData.add(item);
        sectionHeader.add(mData.size() - 1);
        notifyDataSetChanged();
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
        return mData.size();
    }

    @Override
    public String getItem(int position) {
        return mData.get(position).toString();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        int rowType = getItemViewType(position);

        if (convertView == null) {
            switch (rowType) {
                case TYPE_ITEM:
                    convertView = mInflater.inflate(R.layout.schedule_view_list_item, null);
                    TextView subjectTextView = (TextView) convertView.findViewById(R.id.subjectTextView);
                    TextView locationTextView = (TextView) convertView.findViewById(R.id.locationTextView);
                    TextView subjectTypeTextView = (TextView) convertView.findViewById(R.id.subjectTypeTextView);

                    Subject subject = (Subject) mData.get(position);
                    subjectTextView.setText(subject.getName());
                    locationTextView.setText(subject.getSubgroup().size() == 1 ? subject.getSubgroup().get(0).getLocation() : "Multiply value");
                    subjectTypeTextView.setText(subject.getActivity());
                    break;

                case TYPE_SEPARATOR:
                    convertView = mInflater.inflate(R.layout.schedule_view_list_header, null);
                    TextView timeTextView = (TextView) convertView.findViewById(R.id.textView);

                    String time = (String) mData.get(position);
                    timeTextView.setText(time);
                    break;
            }
        }

        return convertView;
    }

    public static class ViewHolder {
        public TextView textView;
    }

}