package com.farrandicpen321.milestone1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Dictionary;
import java.util.List;
import java.util.Locale;

public class WeatherActivity extends AppCompatActivity {
    private final String TAG = "WeatherActivity";
    TextView textWeatherResult;
    TextView textLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private final String url = "https://api.openweathermap.org/data/2.5/weather";
    private final String appid = "93513180cead95ead58a16b7961b3eb8";
    DecimalFormat df = new DecimalFormat("#.##");
    private double lat,lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        // assign variables
        textWeatherResult = findViewById(R.id.text_weatherResult);
        textLocation = findViewById(R.id.text_location);

        // get current city
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getWeatherOfCurrentCity();
    }

    private void getWeather() {
        // Call weather API
        String tempurl = url + "?lat=" + lat + "&lon=" + lon + "&appid=" + appid;
        Log.d(TAG, tempurl);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, tempurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);
                // show the json result from api call
                String output = "";
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                    JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                    String description = jsonObjectWeather.getString("description");
                    JSONObject jsonObjectMain=jsonResponse.getJSONObject("main");
                    double temp  = jsonObjectMain.getDouble( "temp") - 273.15;
                    double feelsLike = jsonObjectMain.getDouble("feels_like") - 273.15;
                    float pressure =  jsonObjectMain.getInt("pressure");
                    int humidity = jsonObjectMain. getInt("humidity") ;
                    JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                    String wind = jsonObjectWind. getString("speed") ;
                    JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");
                    String clouds = jsonObjectClouds.getString("all") ;

//                    textWeatherResult.setTextColor(Color.));
                    output = "Temp: " + df.format(temp) + "°C"
                            + "\nFeels Like: " + df.format(feelsLike) + "°C"
                            + "\nHumidity: " + humidity
                            + "\nDescription: " + description
                            + "\nWind Speed: " + wind + "m/s"
                            + "\nCloudiness: " + clouds + "%"
                            + "\nPressure: " + pressure + "hPa";
                    Log.d(TAG, output);
                    textWeatherResult.setText(output);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString());
                Toast.makeText(WeatherActivity.this, error.toString().trim(), Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(WeatherActivity.this);
        requestQueue.add(stringRequest);
    }

    private void getWeatherOfCurrentCity() {
        // Find the latitude, longtitude, and city
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        // Get city, lat, lon
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                // Initialize location
                Location location = task.getResult();
                if (location != null) {
                    try {
                        // Initialize Geocoder
                        Geocoder geocoder = new Geocoder(WeatherActivity.this, Locale.getDefault());
                        // Initialize address list
                        List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                        lat = addressList.get(0).getLatitude();
                        lon = addressList.get(0).getLongitude();
                        Log.d(TAG, "loc:" + lat + "," + lon);

                        // set location
                        textLocation.setText(addressList.get(0).getLocality());

                        getWeather();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}