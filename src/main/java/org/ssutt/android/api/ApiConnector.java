package org.ssutt.android.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ApiConnector extends AsyncTask<String, Integer, String> {
    private static final ApiConnector API_CONNECTOR = new ApiConnector();

    private ApiConnector() {}

    public static ApiConnector getInstance() {
        return API_CONNECTOR;
    }

    @Override
    protected String doInBackground(String... params) {
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
                return result.toString();
            } else {
                Log.d("log", "Some error while connecting to api");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
