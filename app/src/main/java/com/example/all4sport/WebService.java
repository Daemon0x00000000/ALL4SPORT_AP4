package com.example.all4sport;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WebService {
    private static final String URL_API = "http://127.0.0.1/projet-all4sport/ALL4SPORT/api/";
    private static OkHttpClient client = new OkHttpClient();

    public boolean getLogin(String login, String password) throws IOException {
        String url = URL_API + "login";
        String credentials = "{\"login\":\"" + login + "\",\"password\":\"" + password + "\"}";
        // Send post request
        RequestBody body = RequestBody.create(credentials, okhttp3.MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()             
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        Response response = call.execute();
        return response.isSuccessful();
        
    }
}
