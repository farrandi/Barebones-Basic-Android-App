package com.farrandicpen321.milestone1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ServerActivity extends AppCompatActivity {
    private final String TAG = "ServerActivity";
    TextView textContent;
    TextView textServerIp;
    TextView textServerTime;
    TextView textServerName;
    private final String url = "http://18.236.111.48:5001";
    GoogleSignInAccount account;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        account = GoogleSignIn.getLastSignedInAccount(ServerActivity.this);
        textContent = findViewById(R.id.text_content);

        textServerIp = findViewById(R.id.text_serverip);
        textServerName = findViewById(R.id.text_servername);
        textServerTime = findViewById(R.id.text_servertime);

        fillContent();
    }

    private void fillContent() {
        getServerInfo();

        String output = "";

        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String clientIp = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        Log.d(TAG, "client ip: " + clientIp);

        Date currentTime = Calendar.getInstance().getTime();
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String s = formatter.format(currentTime);
        Log.d(TAG, "clientTime: " + s);

        output = "Client IP adress: " + clientIp +
                "\nClient Local Time: " + s +
                "\nLogged In: " + account.getDisplayName();
        textContent.setText(output);

    }

    private void getServerInfo() {
        RequestQueue requestQueue = Volley.newRequestQueue(ServerActivity.this);
        // Get Server IP
        StringRequest ipRequest = new StringRequest(Request.Method.GET, url + "/ip", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "server ip: " + response);
                textServerIp.setText("Server Ip: " + response.replace("\n",""));
                //here
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString());
            }
        });
        requestQueue.add(ipRequest);

        // Get Server Time
        StringRequest timeRequest = new StringRequest(Request.Method.GET, url + "/time", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "server time : " + response);
                textServerTime.setText("Server Time: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString());
            }
        });
        requestQueue.add(timeRequest);

        // Get Server Name
        StringRequest nameRequest = new StringRequest(Request.Method.GET, url + "/name", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "server name: " + response);
                textServerName.setText("Server Name: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString());
            }
        });
        requestQueue.add(nameRequest);
    }
}