package com.farrandicpen321.milestone1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationRequest;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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

        // check if user has allowed location permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "We have these Permissions yay!", Toast.LENGTH_LONG).show();
            Log.d(TAG, "getting Location 1");
            getLocation();
        } else {
            requestLocationPermission();
            Log.d(TAG, "request permission");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==1) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "We have these Permissions yay!", Toast.LENGTH_LONG).show();
                Log.d(TAG, "getting Location 1");
                getLocation();
            } else {
                requestLocationPermission();
                Log.d(TAG, "request permission");
            }
        }
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(StatusActivity.this, "We need these location permissions to run!", Toast.LENGTH_LONG).show();

            Log.d(TAG, "Trying to Request Location Permissions"); // for debugging
            // Toast is for pop up in phone screen
            Toast.makeText(StatusActivity.this, "Trying to request location permissions", Toast.LENGTH_LONG).show();
            new AlertDialog.Builder(StatusActivity.this)
                    .setTitle("Need Location Permissions")
                    .setMessage("We need the location permissions to mark your location on a map")
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(StatusActivity.this, "We need these location permissions to run!", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(StatusActivity.this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            getLocation();
                        }
                    })
                    .create()
                    .show();
        }
        else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        return;
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

        fusedLocationProviderClient.getCurrentLocation(LocationRequest.QUALITY_HIGH_ACCURACY, null).addOnCompleteListener(new OnCompleteListener<Location>() {
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
//                        startActivity(getIntent());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}