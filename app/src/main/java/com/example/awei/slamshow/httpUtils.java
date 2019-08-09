package com.example.awei.slamshow;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class httpUtils {
    private static httpUtils httpHelper;
    private final OkHttpClient client;
    private httpUtils() {
        client = new OkHttpClient();
    }
    public static httpUtils getHttpHelper() {
        if (httpHelper == null) {
            synchronized (httpUtils.class) {
                if (httpHelper == null) {
                    httpHelper = new httpUtils();
                }
            }
        }
        return httpHelper;
    }

    public void doGet(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
