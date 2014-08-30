package org.ssutt.android.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.ssutt.android.R;
import org.ssutt.android.Typefaces;

import java.util.List;

public class StaredGroupsListAdapter extends ArrayAdapter<String> {
    String myDepartment;
    String myGroup;
    private List<String> staredGroups;

    public StaredGroupsListAdapter(Context context, List<String> staredGroups, String myDepartment, String myGroup) {
        super(context, R.layout.stared_groups_list_item, staredGroups);
        this.staredGroups = staredGroups;
        this.myDepartment = myDepartment;
        this.myGroup = myGroup;
    }

    @Override
    public int getCount() {
        return staredGroups.size() / 3;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater vi = LayoutInflater.from(getContext());
            view = vi.inflate(R.layout.stared_groups_list_item, null);
        }

        String department = staredGroups.get(3 * position);
        String group = staredGroups.get(3 * position + 1);

        TextView departmentTextView = (TextView) view.findViewById(R.id.department);
        TextView groupTextView = (TextView) view.findViewById(R.id.group);

        departmentTextView.setText(department);
        groupTextView.setText(group);
        departmentTextView.setTypeface(Typefaces.get(getContext(), "fonts/helvetica-light.otf"));
        groupTextView.setTypeface(Typefaces.get(getContext(), "fonts/helvetica-light.otf"));

        if (staredGroups.get(3 * position + 2).equals(myDepartment) && group.equals(myGroup)) {
            departmentTextView.setTypeface(Typefaces.get(getContext(), "fonts/helvetica-bold"));
            groupTextView.setTypeface(Typefaces.get(getContext(), "fonts/helvetica-bold"));
        }

        return view;
    }
}
