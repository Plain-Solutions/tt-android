package org.ssutt.android.api;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ApiConnector extends AsyncTask<String, Boolean, JSONArray> {

    @Override
    protected JSONArray doInBackground(String... params) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet request = new HttpGet(params[0]);

        try {
            HttpResponse response = httpClient.execute(request);
            StatusLine status = response.getStatusLine();

            if (status.getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                return new JSONArray(result.toString());
            } else {
                Log.d("log", "Some error while connecting to api");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new JSONArray();
    }
}
