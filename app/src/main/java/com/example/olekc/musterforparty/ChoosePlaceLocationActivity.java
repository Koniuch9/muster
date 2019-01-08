package com.example.olekc.musterforparty;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ChoosePlaceLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    FusedLocationProviderClient fusedClient;
    Marker mMarker;
    LatLng position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_place_location);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        fusedClient = LocationServices.getFusedLocationProviderClient(this);
        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        fusedClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null)
                {
                    Double lat = location.getLatitude(),lng=location.getLongitude();
                    LatLng pos = new LatLng(lat,lng);
                    mMarker = mMap.addMarker(new MarkerOptions().position(pos).title("Miejsce"));
                    mMarker.setDraggable(true);
                    mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                        @Override
                        public void onMarkerDragStart(Marker marker) {

                        }

                        @Override
                        public void onMarkerDrag(Marker marker) {

                        }

                        @Override
                        public void onMarkerDragEnd(Marker marker) {
                            position = marker.getPosition();
                        }
                    });
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos,10));
                }
            }
        });
    }

    public void acceptLocation(View view) {
        Intent data = new Intent();
        String text = String.format(Locale.US,"%.8f,%.8f",position.latitude,position.longitude);
        data.setData(Uri.parse(text));
        setResult(RESULT_OK,data);
        finish();
    }

    public void cancelLocation(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }
}
