package com.example.all4sport;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BearerTokenRequest {
    private Context context;
    private final RequestQueue queue;
    private final String url = "http://172.16.107.45:8000/api";

    public BearerTokenRequest(Context context) {
        this.context = context;
        this.queue = Volley.newRequestQueue(context);
    }

    public void sendRequest(int method, String endpoint, JSONObject body, final BearerTokenRequestCallback callback) {
        JsonObjectRequest request = new JsonObjectRequest(method, url+endpoint, body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        SharedPreferences sharedPreferences = context.getSharedPreferences("MonApplication", Context.MODE_PRIVATE);
                        String token = sharedPreferences.getString("bearer_token", "");
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Authorization", "Bearer " + token);
                        return headers;
                    }
                };
        request.setRetryPolicy(
                new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    public void sendRequest(int method, String endpoint, final BearerTokenRequestCallback callback) {
        StringRequest request = new StringRequest(method, url+endpoint,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            callback.onSuccess(jsonResponse);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onError(new VolleyError("Erreur de parsing JSON"));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                SharedPreferences sharedPreferences = context.getSharedPreferences("MonApplication", Context.MODE_PRIVATE);
                String token = sharedPreferences.getString("bearer_token", "");
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        queue.add(request);
    }

    public interface BearerTokenRequestCallback {
        void onSuccess(JSONObject response);
        void onError(VolleyError error);
    }
}
