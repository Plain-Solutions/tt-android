package org.ssutt.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.ssutt.android.R;

import java.util.ArrayList;

public class SubjectDetailsActivity extends ActionBarActivity {
    private static final String SUBJECT_DETAILS = "subject_details";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subject_details_view);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle(getString(R.string.details));

        Intent intent = getIntent();
        ArrayList<String> subjectDetails = intent.getStringArrayListExtra(SUBJECT_DETAILS);

        ListView subjectDetailsListView = (ListView) findViewById(R.id.subjectDetailsListView);
        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, subjectDetails);
        subjectDetailsListView.setAdapter(adapter);
    }
}
