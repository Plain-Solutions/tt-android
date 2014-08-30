package org.ssutt.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.ssutt.android.R;
import org.ssutt.android.Typefaces;
import org.ssutt.android.adapter.SubjectDetailsAdapter;

import java.util.ArrayList;

public class SubjectDetailsActivity extends ActionBarActivity {
    private static final String SUBJECT_DETAILS = "subject_details";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subject_details_view);

        int actionBarTitleid = getResources().getIdentifier("action_bar_title", "id", "android");
        TextView actionBarTitle = (TextView) findViewById(actionBarTitleid);
        actionBarTitle.setTypeface(Typefaces.get(this, "fonts/helvetica-bold"));

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle(getString(R.string.details));

        Intent intent = getIntent();
        ArrayList<String> subjectDetails = intent.getStringArrayListExtra(SUBJECT_DETAILS);

        ListView subjectDetailsListView = (ListView) findViewById(R.id.subjectDetailsListView);
        SubjectDetailsAdapter adapter = new SubjectDetailsAdapter(this, subjectDetails);
        subjectDetailsListView.setAdapter(adapter);
    }
}
