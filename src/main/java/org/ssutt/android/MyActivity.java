package org.ssutt.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ssutt.android.api.ApiConnector;
import org.ssutt.android.api.ApiRequests;

import java.util.concurrent.ExecutionException;

public class MyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);

        Button button = (Button) findViewById(R.id.connect);
        final TextView tv = (TextView) findViewById(R.id.textView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiConnector apiConnector = new ApiConnector();

                apiConnector.execute(ApiRequests.getDepartments());
                try {
                    JSONArray json = apiConnector.get();
                    for (int i = 0; i < json.length(); i++) {
                        tv.append(json.getJSONObject(i).getString("name"));
                        tv.append("\n");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
