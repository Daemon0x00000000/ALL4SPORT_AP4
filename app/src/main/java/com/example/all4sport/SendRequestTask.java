package com.example.all4sport;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendRequestTask extends AsyncTask<Void, Void, Response> {
    @Override
    protected Response doInBackground(Void... voids) {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{'username':'test','password':'test'}");
        Request request = new Request.Builder()
                .url("http://192.168.1.15:8000/api/login")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        try {
            return client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Response response) {
        try {
            System.out.println(Objects.requireNonNull(response.body()).string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}