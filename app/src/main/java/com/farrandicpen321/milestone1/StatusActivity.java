package com.farrandicpen321.milestone1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class StatusActivity extends AppCompatActivity {
    final static String TAG = "StatusActivity";
    TextView textView1, textView2, textView3, textView4;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        // assign variable
        textView1 = findViewById(R.id.text_view1);
        textView2 = findViewById(R.id.text_view2);
        textView3 = findViewById(R.id.text_view3);
        textView4 = findViewById(R.id.text_view4);

        //Initialize fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        textView4.setText(Html.fromHtml(
                "<font color = '36200EE'><b>Phone Manufacturer : </b><br></font>"
                        + Build.MANUFACTURER + "<br>"
                        + "<font color = '36200EE'><b>Phone Model : </b><br></font>"
                        + Build.MODEL
        ));

        getLocation();
    }

    private void getLocation() {
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
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                // Initialize location
                Location location = task.getResult();
                if (location != null) {
                    try{
                        // Initialize Geocoder
                        Geocoder geocoder = new Geocoder(StatusActivity.this, Locale.getDefault());
                        // Initialize address list
                        List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                        // Set city on TextView
                        textView1.setText(Html.fromHtml(
                                "<font color = '36200EE'><b>Location : </b><br></font>"
                                        + addressList.get(0).getLocality()
                                        + ","
                                        + addressList.get(0).getCountryName()
                        ));
                        textView2.setText(Html.fromHtml(
                                "<font color = '36200EE'><b>Latitute : </b><br></font>"
                                        + addressList.get(0).getLatitude()
                        ));
                        textView3.setText(Html.fromHtml(
                                "<font color = '36200EE'><b>Longtitude : </b><br></font>"
                                        + addressList.get(0).getLongitude()
                        ));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}