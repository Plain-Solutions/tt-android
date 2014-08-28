package org.ssutt.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.ssutt.android.R;

import java.util.List;

public class StaredGroupsListAdapter extends ArrayAdapter<String> {
    private List<String> staredGroups;

    public StaredGroupsListAdapter(Context context, List<String> staredGroups) {
        super(context, R.layout.stared_groups_list_item, staredGroups);
        this.staredGroups = staredGroups;
    }

    @Override
    public int getCount() {
        return staredGroups.size() / 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater vi = LayoutInflater.from(getContext());
            view = vi.inflate(R.layout.stared_groups_list_item, null);
        }

        /*String department = getItem(2 * position);
        String group = getItem(2 * position + 1);*/

        System.out.println(staredGroups);

        String department = staredGroups.get(2 * position);
        String group = staredGroups.get(2 * position + 1);

        TextView departmentTextView = (TextView) view.findViewById(R.id.department);
        TextView groupTextView = (TextView) view.findViewById(R.id.group);

        departmentTextView.setText(department);
        groupTextView.setText(group);

        return view;
    }
}
