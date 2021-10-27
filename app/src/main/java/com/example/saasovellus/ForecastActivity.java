package com.example.saasovellus;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ForecastActivity extends AppCompatActivity {

    private RequestQueue queue;
    private String cityName;
    private String unit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        cityName = getIntent().getStringExtra("CITY_NAME_KEY");
        unit = getIntent().getStringExtra("UNIT_KEY");
        queue = Volley.newRequestQueue(this);
        getForecastForCity();

        TextView cityNameTextView = findViewById(R.id.forecastCityNameTextView);
        cityNameTextView.setText(cityName);

    }

    public void getForecastForCity() {
        String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + cityName + "&units="+unit+"&appid=0b3dbb4aefe225ec3f1eb4113f674050";
        // Request a string response from the provided URL.
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            parseJsonAndUpdateUI(response);
        }, error -> {

        });
        queue.add(stringRequest);
    }

    private void parseJsonAndUpdateUI(JSONObject response) {
        String weatherForecastItemString;
        try {
            JSONArray forecastList = response.getJSONArray("list");
            for (int i = 0; i < forecastList.length(); i++) {
                JSONObject weatherItem = forecastList.getJSONObject(i);
                weatherForecastItemString = weatherItem.getJSONArray("weather").getJSONObject(0).getString("main");
                float temperature = (float) weatherItem.getJSONObject("main").getDouble("temp");
                if (unit.equals("metric")) {
                    weatherForecastItemString += " " + temperature + " °C";
                } else {
                    weatherForecastItemString += " " + temperature + " °F";
                }

                TextView forecastListTextView = findViewById(R.id.weatherForecastListTextView);
                forecastListTextView.append(weatherForecastItemString + "\n\n");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}