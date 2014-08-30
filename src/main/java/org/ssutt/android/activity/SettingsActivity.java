package org.ssutt.android.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.ssutt.android.R;
import org.ssutt.android.Typefaces;
import org.ssutt.android.adapter.SettingsAdapter;

import java.util.Arrays;
import java.util.Map;

import static org.ssutt.android.activity.Constants.FIRST_TIME;
import static org.ssutt.android.activity.Constants.MY_DEPARTMENT;
import static org.ssutt.android.activity.Constants.MY_GROUP;
import static org.ssutt.android.activity.Constants.PREF;
import static org.ssutt.android.activity.Constants.STAR;

public class SettingsActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_view);

        int actionBarTitleid = getResources().getIdentifier("action_bar_title", "id", "android");
        TextView actionBarTitle = (TextView) findViewById(actionBarTitleid);
        actionBarTitle.setTypeface(Typefaces.get(this, "fonts/helvetica-bold"));

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle(getString(R.string.settings));

        ListView settingsListView = (ListView) findViewById(R.id.settingsListView);
        SettingsAdapter adapter = new SettingsAdapter(this, new String[]{
                getString(R.string.changeMyGroup),
                getString(R.string.cleanStaredGroups),
                ""});

        settingsListView.setAdapter(adapter);
        settingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                SharedPreferences pref = getSharedPreferences(PREF, MODE_PRIVATE);
                SharedPreferences staredPref = getSharedPreferences(STAR, MODE_PRIVATE);

                SharedPreferences.Editor editor = pref.edit();
                SharedPreferences.Editor starEditor = staredPref.edit();

                switch (position) {
                    case 0:
                        Intent intent = new Intent(getApplicationContext(), DepartmentActivity.class);
                        editor.putBoolean(FIRST_TIME, true);
                        editor.apply();

                        startActivity(intent);
                        break;
                    case 1:
                        Map<String, ?> stringMap = staredPref.getAll();
                        String myDepartment = pref.getString(MY_DEPARTMENT, "empty");
                        String myGroup = pref.getString(MY_GROUP, "empty");

                        for (Map.Entry<String, ?> entry : stringMap.entrySet()) {
                            String key = entry.getKey();
                            String[] split = key.split("&");

                            System.out.println(Arrays.toString(split));

                            if (!(split[0].equals(myDepartment) && split[1].equals(myGroup))) {
                                starEditor.putBoolean(key, false);
                            }
                        }

                        starEditor.apply();
                        Toast.makeText(getApplicationContext(), getString(R.string.staredGroupsRemoved), Toast.LENGTH_SHORT).show();

                        break;
                }
            }
        });
    }
}
