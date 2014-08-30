package org.ssutt.android.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class ApiConnector extends AsyncTask<String, Integer, String> {
    private static final String errorMessage = "При загрузке данных произошла ошибка. Проверьте Ваше подключение к сети.";
    private static final String cacheMessage = "Данные загружены из локального хранилища.";
    private static final String cacheNoFoundMessage = "Данные в локальном хранилище не найдены.";
    private String url;

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public static void errorToast(Context context) {
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
    }

    public static void cacheToast(Context context) {
        Toast.makeText(context, cacheMessage, Toast.LENGTH_SHORT).show();
    }

    public static void cacheNoFoundToast(Context context) {
        Toast.makeText(context, cacheNoFoundMessage, Toast.LENGTH_SHORT).show();
    }

    public String getUrl() {
        return url;
    }

    @Override
    protected String doInBackground(String... params) {
        url = params[0];
        final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
        final HttpGet getRequest = new HttpGet(url);

        try {
            HttpResponse response = client.execute(getRequest);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                    StringBuilder json = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        json.append(line);
                    }
                    return json.toString();
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (Exception e) {
            getRequest.abort();
        } finally {
            client.close();
        }

        return null;
    }
}
