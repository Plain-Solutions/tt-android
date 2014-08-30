package org.ssutt.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.ssutt.android.Typefaces;

import java.util.ArrayList;

public class SubjectDetailsAdapter extends ArrayAdapter<String> {
    private ArrayList<String> data;

    public SubjectDetailsAdapter(Context context, ArrayList<String> data) {
        super(context, android.R.layout.simple_list_item_1, data);
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater vi = LayoutInflater.from(getContext());
            view = vi.inflate(android.R.layout.simple_list_item_1, null);
        }

        TextView label = (TextView) view.findViewById(android.R.id.text1);
        label.setText(data.get(position));
        label.setTypeface(Typefaces.get(getContext(), "fonts/helvetica-light.otf"));

        return view;
    }
}
