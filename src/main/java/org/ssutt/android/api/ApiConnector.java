package org.ssutt.android.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public abstract class ApiConnector extends AsyncTask<String, Integer, String> {
    private static final String errorMessage = "При загрузке данных произошла ошибка. Проверьте Ваше подключение к сети.";
    private static final String cacheMessage = "Данные загружены из локального хранилища.";
    private static final String cacheNoFoundMessage = "Данные в локальном хранилище не найдены.";
    public static final Map<String, String> errorMessages = new HashMap<String, String>();
    private String url;

    static {
        errorMessages.put("0", "Database error!");
        errorMessages.put("1", "Production server is down!");
        errorMessages.put("2", "No such department!");
        errorMessages.put("3", "No such group!");
        errorMessages.put("4", "Unsupported encoding!");
        errorMessages.put("5", "Timeout!");
        errorMessages.put("6", "Connection refused!");
    }

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

    protected boolean isValid(String json) {
        return !errorMessages.containsKey(json);
    }

    public String getUrl() {
        return url;
    }

    @Override
    protected String doInBackground(String... arg) {
        url = arg[0];
        final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
        final HttpGet getRequest = new HttpGet(url);

        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
        HttpConnectionParams.setSoTimeout(httpParams, 5000);
        getRequest.setParams(httpParams);

        try {
            HttpResponse response = client.execute(getRequest);
            int statusCode = response.getStatusLine().getStatusCode();

            switch (statusCode) {
                case 200:
                    break;
                case 209:
                    return "0";
                case 404:
                    return "1";
                case 309:
                    return "2";
                case 399:
                    return "3";
                case 509:
                    return "4";
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
                        System.out.println(line);
                    }
                    return json.toString();
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (ConnectTimeoutException ce) {
            return "5";
        } catch (Exception e) {
            getRequest.abort();
        } finally {
            client.close();
        }

        return "6";
    }
}
