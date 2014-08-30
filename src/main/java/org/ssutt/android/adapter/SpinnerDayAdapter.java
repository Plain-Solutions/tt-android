package org.ssutt.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.ssutt.android.R;
import org.ssutt.android.Typefaces;

public class SpinnerDayAdapter extends ArrayAdapter {
    private String[] data;

    public SpinnerDayAdapter(Context context, String[] data) {
        super(context, R.layout.days_spinner_view, data);
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater vi = LayoutInflater.from(getContext());
        View view = vi.inflate(R.layout.days_spinner_view, null);

        TextView label = (TextView) view.findViewById(R.id.daysSpinnerItem);
        label.setText(data[position]);
        label.setTypeface(Typefaces.get(getContext(), "fonts/helvetica-light.otf"));
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        LayoutInflater vi = LayoutInflater.from(getContext());
        View view = vi.inflate(android.R.layout.simple_list_item_1, null);

        TextView label = (TextView) view.findViewById(android.R.id.text1);
        label.setText(data[position]);
        label.setTypeface(Typefaces.get(getContext(), "fonts/helvetica-light.otf"));
        return view;
    }

}