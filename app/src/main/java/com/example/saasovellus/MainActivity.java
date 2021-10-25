package com.example.saasovellus;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private RequestQueue queue;
    private float temperature;
    private float windSpeed;
    private String weatherDesc;
    private String city;
    private String unit = "metric";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView temperatureTextView = findViewById(R.id.tempTextView);
        TextView descriptionTextView = findViewById(R.id.weatherDesc);
        TextView windTextView = findViewById(R.id.windSpeedTextView);
        TextView cityTextView = findViewById(R.id.cityTextView);
        if(savedInstanceState != null){
            unit = savedInstanceState.getString("LOG_UNIT");
            city = savedInstanceState.getString("LOG_CITY");
            temperature = savedInstanceState.getFloat("LOG_TEMPERATURE");
            windSpeed = savedInstanceState.getFloat("LOG_WINDSPEED");
            weatherDesc = savedInstanceState.getString("LOG_DESC");
            cityTextView.setText(city);
            if(unit.equals("metric")){
                temperatureTextView.setText(temperature + " °C");
                windTextView.setText(windSpeed + " m/s");
            } else {
                temperatureTextView.setText(temperature + " °F");
                windTextView.setText(windSpeed + " mph");
            }
            descriptionTextView.setText(weatherDesc);
            setWeatherIcon(weatherDesc);

        }
        // Instantiate the RequestQueue
        queue = Volley.newRequestQueue(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_options_menu, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save data into bundle;
        outState.putFloat("LOG_TEMPERATURE", temperature);
        outState.putFloat("LOG_WINDSPEED", windSpeed);
        outState.putString("LOG_DESC", weatherDesc);
        outState.putString("LOG_CITY", city);
        outState.putString("LOG_UNIT", unit);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.item_one:
                unit = "imperial";
                item.setChecked(!item.isChecked());
                break;
            case R.id.item_two:
                unit = "metric";
                item.setChecked(!item.isChecked());
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
    // Gets weatherdata from openweathermap api
    public void getWeatherData(View view) {
        EditText cityEdit = findViewById(R.id.cityEditText);
        city = cityEdit.getText().toString();
        TextView cityText = findViewById(R.id.cityTextView);
        cityText.setText(city);
        String url = "https://api.openweathermap.org/data/2.5/weather?q="+ city + "&units="+unit+"&appid=0b3dbb4aefe225ec3f1eb4113f674050";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    // Web req ok, response here as a string
                    parseJsonAndUpdateUI(response);
                }, error -> {
            // Error getting from web
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void parseJsonAndUpdateUI(String response) {
        try {
            JSONObject mainObj = new JSONObject(response);
            temperature = (float) mainObj.getJSONObject("main").getDouble("temp");
            windSpeed = (float) mainObj.getJSONObject("wind").getDouble("speed");
            weatherDesc = mainObj.getJSONArray("weather").getJSONObject(0).getString("main");
            // Display data
            TextView temperatureTextView = findViewById(R.id.tempTextView);
            TextView descriptionTextView = findViewById(R.id.weatherDesc);
            descriptionTextView.setText(weatherDesc);
            TextView windTextView = findViewById(R.id.windSpeedTextView);
            if(unit.equals("metric")){
                temperatureTextView.setText(temperature + " °C");
                windTextView.setText(windSpeed + " m/s");
            } else {
                temperatureTextView.setText(temperature + " °F");
                windTextView.setText(windSpeed + " mph");
            }
            // Select icon that matches description
            setWeatherIcon(weatherDesc);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void openForecast(View view) {
        Intent openForecast = new Intent(this, ForecastActivity.class);
        openForecast.putExtra("CITY_NAME_KEY", city);
        openForecast.putExtra("UNIT_KEY", unit);
        startActivity(openForecast);
    }
    public void setWeatherIcon(String desc) {
        ImageView weatherIcon = findViewById(R.id.weatherImageView);
        switch (desc) {
            case "Clear":
                weatherIcon.setImageResource(R.drawable.sunny_icon);
                break;
            case "Clouds":
                weatherIcon.setImageResource(R.drawable.cloudy_icon);
                break;
            case "Rain":
                weatherIcon.setImageResource(R.drawable.rainy_icon);
                break;
            default:
                weatherIcon.setImageResource(R.drawable.fog_icon);
                break;
        }
    }
}

