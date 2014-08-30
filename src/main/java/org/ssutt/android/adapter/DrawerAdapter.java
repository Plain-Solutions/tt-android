package org.ssutt.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.ssutt.android.R;
import org.ssutt.android.Typefaces;

public class DrawerAdapter extends BaseAdapter {
    private static final int[] iconResourcesId = {R.drawable.ic_home, R.drawable.ic_info, R.drawable.ic_star, R.drawable.ic_search, R.drawable.ic_settings};
    private LayoutInflater inflater;
    private Context context;
    private String[] data;

    public DrawerAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        data = context.getResources().getStringArray(R.array.menu_options_array);
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.drawer_list_item, null);
        }

        ImageView icon = (ImageView) convertView.findViewById(R.id.drawer_icon);
        TextView settingsText = (TextView) convertView.findViewById(R.id.settingsText);

        settingsText.setText(data[i]);
        settingsText.setTypeface(Typefaces.get(context, "fonts/helvetica-light.otf"));
        icon.setBackgroundResource(iconResourcesId[i]);

        return convertView;
    }
}
