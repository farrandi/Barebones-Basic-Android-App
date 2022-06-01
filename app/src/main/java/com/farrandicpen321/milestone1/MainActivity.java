package com.farrandicpen321.milestone1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {
    private int RC_SIGN_IN=1;
    private Button favCityButton;
    private Button statusButton;
    private Button weatherButton;
    private Button serverButton;
    final static String TAG = "MainActivity"; // good practice for debugging
    private GoogleSignInClient mGoogleSignInClient;
    private final String url = "http://18.236.111.48:5001";
    String serverIP, serverTime, serverName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        favCityButton = findViewById(R.id.fav_city_button);
        favCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Trying to open Favourite City in Google Maps");

                Intent mapsIntent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(mapsIntent);
            }
        });

        statusButton = findViewById(R.id.status_button);
        statusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Trying to open Phone Model");

                Intent statusIntent = new Intent(MainActivity.this, StatusActivity.class);
                startActivity(statusIntent);
            }
        });

        weatherButton = findViewById(R.id.weather_button);
        weatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Trying to Open Weathers");

                Intent weatherIntent = new Intent(MainActivity.this, WeatherActivity.class);
                startActivity(weatherIntent);
            }
        });

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.server_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.server_button:
                        signIn();
                        break;
                }
//                Intent serverIntent = new Intent(MainActivity.this, ServerActivity.class);
//                startActivity(serverIntent);
            }
        });

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
            Log.d(TAG, "Trying to Open Server");
            Intent serverIntent = new Intent(MainActivity.this, ServerActivity.class);
            startActivity(serverIntent);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account == null) {
            Log.d(TAG, "There is no user signed in");
        } else {
            Log.d(TAG, "User:" + account.getDisplayName() + ", Email: " + account.getEmail() + " " + account.getPhotoUrl());

            // Move to another activity
            serverButton = findViewById(R.id.server_button);
            serverButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Trying to Open Server");
                    Intent serverIntent = new Intent(MainActivity.this, ServerActivity.class);
                    startActivity(serverIntent);
                }
            });
        }
    }

    private void checkLocationPermissions() {
        // check if user has allowed location permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "We have these Permissions yay!", Toast.LENGTH_LONG).show();

            return;
        } else {
            requestLocationPermission();
        }

    }
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(MainActivity.this, "We need these location permissions to run!", Toast.LENGTH_LONG).show();

            Log.d(TAG, "Trying to Request Location Permissions"); // for debugging
            // Toast is for pop up in phone screen
            Toast.makeText(MainActivity.this, "Trying to request location permissions", Toast.LENGTH_LONG).show();
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Need Location Permissions")
                    .setMessage("We need the location permissions to mark your location on a map")
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this, "We need these location permissions to run!", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                        }
                    })
                    .create()
                    .show();
        }
        else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

}