package org.ssutt.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.ssutt.android.Typefaces;

public class SettingsAdapter extends ArrayAdapter<String> {
    public SettingsAdapter(Context context, String[] settings) {
        super(context, android.R.layout.simple_list_item_1, settings);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater vi = LayoutInflater.from(getContext());
            view = vi.inflate(android.R.layout.simple_list_item_1, null);
        }

        String item = getItem(position);
        if (item != null) {
            TextView label = (TextView) view.findViewById(android.R.id.text1);
            label.setText(item);
            label.setTypeface(Typefaces.get(getContext(), "fonts/helvetica-light.otf"));
        }

        return view;
    }
}
