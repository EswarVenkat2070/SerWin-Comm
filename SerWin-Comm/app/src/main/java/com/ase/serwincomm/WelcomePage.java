package com.ase.serwincomm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

public class WelcomePage extends AppCompatActivity implements LocationListener {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    Double lat, longt;
    String country;
    int decide = 0;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);

        sharedpreferences = getSharedPreferences("locationPref", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        btn = findViewById(R.id.anything);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomePage.this, toVisit.class));
         }
        });

    }

    @Override
    public void onLocationChanged(Location location) {

        try {

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if(decide == 0) {
                lat = addresses.get(0).getLatitude();
                longt = addresses.get(0).getLongitude();
                country = addresses.get(0).getCountryName();
//                subLocality = addresses.get(0).getSubLocality();
            }

            String latitude = Double.toString(lat);
            String longitude = Double.toString(longt);

            // adding to shared preferences
            editor.putString("latitude", latitude);
            editor.putString("longitude", longitude);
            editor.commit();

            Toast.makeText(WelcomePage.this, (sharedpreferences.getString("latitude", "0.00")),
                    Toast.LENGTH_SHORT)
                    .show();
        }
        catch(Exception e)
        {
            Log.e("log", e.getMessage());
        }
    }
}