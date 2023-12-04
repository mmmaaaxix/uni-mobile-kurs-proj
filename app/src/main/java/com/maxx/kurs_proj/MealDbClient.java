package com.maxx.kurs_proj;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MealDbClient {
    private String _mainUrl = "https://www.themealdb.com/api/json/v1/1";
    private RequestQueue _requestQueue;

    public MealDbClient(Context context) {
        _requestQueue = Volley.newRequestQueue(context);
    }
    public void GetRandomMeal(
            Response.Listener<Meal> responseListener,
            Response.ErrorListener errorListener)
    {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                _mainUrl + "/random.php",
                null,

                (Response.Listener<JSONObject>) response -> {
                    try {
                        responseListener.onResponse(MapToMeal(response));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },

                (Response.ErrorListener) error -> {
                    errorListener.onErrorResponse(error);
                }
        );

        _requestQueue.add(jsonObjectRequest);
    }

    public void GetMealById(
            long id,
            Response.Listener<Meal> responseListener,
            Response.ErrorListener errorListener)
    {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                _mainUrl + "/lookup.php?i=" + id,
                null,

                (Response.Listener<JSONObject>) response -> {
                    try {
                        responseListener.onResponse(MapToMeal(response));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },

                (Response.ErrorListener) error -> {
                    errorListener.onErrorResponse(error);
                }
        );

        _requestQueue.add(jsonObjectRequest);
    }

    private Meal MapToMeal(JSONObject json) throws JSONException {
        JSONObject jsonMeal = json.getJSONArray("meals").getJSONObject(0);
        return new Meal(
            jsonMeal.getLong("idMeal"),
            jsonMeal.getString("strMeal"),
            jsonMeal.getString("strInstructions"),
            jsonMeal.getString("strMealThumb")
        );
    }
}
