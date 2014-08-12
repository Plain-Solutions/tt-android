package org.ssutt.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.ssutt.android.R;

public class DepartmentListAdapter extends ArrayAdapter<String> {
    public DepartmentListAdapter(Context context, String[] departments) {
        super(context, R.layout.department_view_list_item, departments);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater vi = LayoutInflater.from(getContext());
            view = vi.inflate(R.layout.department_view_list_item, null);
        }

        String item = getItem(position);
        if (item != null) {
            TextView label = (TextView) view.findViewById(R.id.departmentLabel);
            label.setText(item);
        }

        return view;
    }
}
