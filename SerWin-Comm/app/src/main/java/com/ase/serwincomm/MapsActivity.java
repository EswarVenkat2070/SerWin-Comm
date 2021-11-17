package com.ase.serwincomm;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;   ;
import android.os.Bundle;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    List<Places> nearbyPlacesList;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        sharedpreferences = getSharedPreferences("locationPref", Context.MODE_PRIVATE);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        nearbyPlacesList = (List<Places>) args.getSerializable("ARRAYLIST");

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
   /*     LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    */

        if(nearbyPlacesList != null) {
            for (int i = 0; i < nearbyPlacesList.size(); i++) {
                Log.d("Showing places", "Entered into showing locations");
                MarkerOptions markerOptions = new MarkerOptions();
                Places placeSingle = nearbyPlacesList.get(i);
                double lat = placeSingle.getLatitude();
                double lng = placeSingle.getLongitude();
                String placeName = placeSingle.getName();
                String dist = Float.toString(placeSingle.getDistance());
                LatLng latLng = new LatLng(lat, lng);
                markerOptions.position(latLng);
                markerOptions.title(placeName);
                mMap.addMarker(markerOptions);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

            }
        }
        else{
            Double latitude = Double.valueOf(sharedpreferences.getString("latitude", "0.00"));
            Double longitude = Double.valueOf(sharedpreferences.getString("longitude", "0.00"));

            LatLng currrent = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(currrent).title("Your location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currrent));
        }

    }
}