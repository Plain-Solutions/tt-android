package org.ssutt.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.ssutt.android.R;

import java.util.ArrayList;

public class SubjectDetailsActivity extends Activity {
    private static final String SUBJECT_DETAILS = "subject_details";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subject_details_view);

        Intent intent = getIntent();
        ArrayList<String> subjectDetails = intent.getStringArrayListExtra(SUBJECT_DETAILS);

        ListView subjectDetailsListView = (ListView) findViewById(R.id.subjectDetailsListView);
        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, subjectDetails);
        subjectDetailsListView.setAdapter(adapter);
    }
}
