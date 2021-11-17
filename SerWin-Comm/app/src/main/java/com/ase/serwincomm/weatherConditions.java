package com.ase.serwincomm;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import com.google.android.gms.common.api.Status;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.compat.AutocompleteFilter;
import com.google.android.libraries.places.compat.Place;
import com.google.android.libraries.places.compat.ui.PlaceAutocomplete;
import com.google.android.libraries.places.compat.ui.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Locale;
import android.Manifest;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class weatherConditions extends AppCompatActivity implements LocationListener, PlaceSelectionListener {

    TextView cityField, detailsField, currentTemperatureField, weatherIcon, sub;
    LocationManager locationManager;
    Double lat, longt;
    String country, subLocality;
    Button nextPage, locationFab;
    Typeface weatherFont;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    Context context;
    private ProgressDialog dialog;
    SwipeRefreshLayout refreshLayout;


    //fab actions
    FloatingActionButton settings, call, cancel,  btnLang, diningFab, gasstationFab, hotelFab;
    LinearLayout settingLayout, callLayout, layoutCancel, layoutlocationFab;
    boolean isFABOpen = false;
    View fabBGLayout;

    //bottom sheet
    int decide = 0;

    static final Integer LOCATION = 1;
    static final Integer CALL = 2;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_weather_conditions);

        weatherFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/weathericons-regular-webfont.ttf");
        sharedpreferences = getSharedPreferences("locationPref", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        cityField =  findViewById(R.id.city_field);
        detailsField = findViewById(R.id.details_field);
        currentTemperatureField = findViewById(R.id.current_temperature_field);
        weatherIcon = findViewById(R.id.weather_icon);
        sub = findViewById(R.id.sub_field);
        weatherIcon.setTypeface(weatherFont);
        context = getApplicationContext();
        btnLang = findViewById(R.id.languageButton);
        refreshLayout = findViewById(R.id.refresh);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                decide = 0;
                setLocation();
                refreshLayout.setRefreshing(false);
            }
        });


        //fabs
        settings = findViewById(R.id.fabSettings);
        call = findViewById(R.id.fabCall);
        cancel = findViewById(R.id.fabCancel);
        locationFab = findViewById(R.id.fabLocation);
        settingLayout = findViewById(R.id.layoutfabSettings);
        callLayout = findViewById(R.id.layoutfabCall);
        layoutCancel = findViewById(R.id.layoutCancel);
        fabBGLayout = findViewById(R.id.fabBGLayout);
        layoutlocationFab = findViewById(R.id.locationLayout);
        diningFab = findViewById(R.id.dining);
        gasstationFab = findViewById(R.id.gasStation);
        hotelFab = findViewById(R.id.hotel);


        diningFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(weatherConditions.this, individualType.class).putExtra("type", "restaurant"));
            }
        });

        gasstationFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(weatherConditions.this, individualType.class).putExtra("type", "gas+station"));
            }
        });

        hotelFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(weatherConditions.this, individualType.class).putExtra("type", "hotel"));
            }
        });

        btnLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(weatherConditions.this, languageOptions.class));
                setLocale(sharedpreferences.getString("language", "en"));
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showFABMenu();
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // to make a phone call
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:112"));

                if (ActivityCompat.checkSelfPermission(weatherConditions.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(callIntent);


            }
        });

        locationFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AutocompleteFilter.Builder filterBuilder = new AutocompleteFilter.Builder();
                // filterBuilder.setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS);

                Intent intent = null;
                try {
                    intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setFilter(filterBuilder.build())
                            .build(weatherConditions.this);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
            }
        });


        nextPage = findViewById(R.id.next);
        nextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(weatherConditions.this, toVisit.class);
                startActivity(i);
            }
        });
    }


    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(weatherConditions.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(weatherConditions.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(weatherConditions.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(weatherConditions.this, new String[]{permission}, requestCode);
            }
        } else {

        }
    }

    private void showFABMenu(){
        isFABOpen=true;
        callLayout.setVisibility(View.VISIBLE);
        layoutlocationFab.setVisibility(View.VISIBLE);
        fabBGLayout.setVisibility(View.VISIBLE);

        settings.animate().rotationBy(180);
        settingLayout.setVisibility(View.GONE);
        layoutCancel.setVisibility(View.VISIBLE);

        callLayout.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        layoutlocationFab.animate().translationY(-getResources().getDimension(R.dimen.standard_100));
    }

    private void closeFABMenu(){
        isFABOpen=false;
        fabBGLayout.setVisibility(View.GONE);
        settings.animate().rotationBy(-180);

        layoutCancel.setVisibility(View.GONE);
        settingLayout.setVisibility(View.VISIBLE);

        callLayout.animate().translationY(0);
        layoutlocationFab.animate().translationY(0);

        callLayout.setVisibility(View.GONE);
        layoutlocationFab.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if(isFABOpen){
            closeFABMenu();
        }else{
            super.onBackPressed();
        }
    }

    public void setLocation(){
        try {
            locationManager = (LocationManager)  getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 5, this);

        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        try {
            dialog = new ProgressDialog(this); // this = YourActivity
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage(context.getResources().getString(R.string.dialogLocation));
            dialog.setIndeterminate(true);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

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

            Function.placeIdTask asyncTask = new Function.placeIdTask(new Function.AsyncResponse() {
                public void processFinish(String weather_city, String weather_description, String weather_temperature,
                                          String weather_humidity, String weather_pressure, String weather_updatedOn,
                                          String weather_iconText, String sun_rise) {
                    dialog.dismiss();
                    //                  sub.setText(subLocality);
                    cityField.setText(weather_city);
                    detailsField.setText(weather_description.toLowerCase());
                    currentTemperatureField.setText(weather_temperature);
                    weatherIcon.setText(Html.fromHtml(weather_iconText));

                }
            });
            asyncTask.execute(latitude, longitude, sharedpreferences.getString("language", "en")); //  asyncTask.execute("Latitude", "Longitude")


        }catch(Exception e)
        {
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Problem connecting to GPS or internet.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    protected void onPause() {
        super.onPause();
        //   Toast.makeText(this, "stop", Toast.LENGTH_LONG).show();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        setLocation();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED){
            switch (requestCode) {
                //Location
                case 1:
                    setLocation();
                    break;
                //Call
                case 2:
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel: 112"));
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        startActivity(callIntent);
                    }
                    break;

            }

        }else{
        }
    }


    public void onStart() {
        super.onStart();
        askForPermission(Manifest.permission.ACCESS_FINE_LOCATION,LOCATION);
        askForPermission(Manifest.permission.CALL_PHONE,CALL);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPlaceSelected(Place place) {
        Log.d("Place selected", place.getName().toString());
        LatLng latLng = place.getLatLng();
        lat = latLng.latitude;
        longt = latLng.longitude;
        decide = 1;
        setLocation();
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, weatherConditions.class);
        startActivity(refresh);
        finish();
    }


    @Override
    public void onError(Status status) {}
}