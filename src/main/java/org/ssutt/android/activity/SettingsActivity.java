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
import android.widget.Toast;

import org.ssutt.android.R;
import org.ssutt.android.domain.Department;

import java.util.Arrays;
import java.util.Map;

public class SettingsActivity extends ActionBarActivity {
    private static final String[] settingsItems = new String[]{"Сменить мою группу", "Очистить сохраненные группы", ""};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_view);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle(getString(R.string.settings));

        ListView settingsListView = (ListView) findViewById(R.id.settingsListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, settingsItems);
        settingsListView.setAdapter(adapter);

        settingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                SharedPreferences staredPref = getSharedPreferences("star", MODE_PRIVATE);

                SharedPreferences.Editor editor = pref.edit();
                SharedPreferences.Editor starEditor = staredPref.edit();

                switch (position) {
                    case 0:
                        Intent intent = new Intent(getApplicationContext(), DepartmentActivity.class);
                        editor.putBoolean("firstTime", true);
                        editor.apply();

                        startActivity(intent);
                        break;
                    case 1:
                        Map<String, ?> stringMap = staredPref.getAll();
                        String myDepartment = pref.getString("myDepartment", "empty");
                        String myGroup = pref.getString("myGroup", "empty");

                        System.out.println("!!! " + myDepartment + " " + myGroup);

                        for (Map.Entry<String, ?> entry : stringMap.entrySet()) {
                            String key = entry.getKey();
                            String[] split = key.split("&");

                            System.out.println(Arrays.toString(split));

                            if (!split[0].equals(myDepartment) && !split[1].equals(myGroup)) {
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
