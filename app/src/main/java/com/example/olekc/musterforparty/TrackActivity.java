package com.example.olekc.musterforparty;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

public class TrackActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener {

    private static final int PERMISSION_REQUEST_CODE = 1;

    private FirebaseAuth mAuth;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        if(!checkPermission())
        {
            requestPermission();
        }
        mAuth = FirebaseAuth.getInstance();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /*
   ------------- M E N U -------------
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch(item.getItemId())
        {
            case R.id.add_group:
                i = new Intent(TrackActivity.this,AddGroupActivity.class);
                startActivity(i);
                finish();
                return true;
            case R.id.add_place:
                i = new Intent(TrackActivity.this,AddPlaceActivity.class);
                startActivity(i);
                finish();
                return true;
            case R.id.show_group:
                i = new Intent(TrackActivity.this,GroupsActivity.class);
                startActivity(i);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    /*
   END ------------- M E N U -------------
    */

    /*
   ------------- M A P A -------------
    */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        moveMapOnMe();

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void moveMapOnMe()
    {
        if(mMap != null)
        {
            if(checkPermission())
            {
                mMap.setMyLocationEnabled(true);
                mMap.setOnMyLocationButtonClickListener(this);
                mMap.setOnMyLocationClickListener(this);
            }

        }
    }
    /*
   END ------------- M A P A -------------
    */

    /*
    ------------- P O Z W O L E N I A -------------
     */
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(TrackActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                + ContextCompat.checkSelfPermission(TrackActivity.this, Manifest.permission.INTERNET);
        return (result == PackageManager.PERMISSION_GRANTED);

    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.INTERNET},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(TrackActivity.this,
                            "Permission accepted", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(TrackActivity.this,
                            "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
     /*
    END ------------- P O Z W O L E N I A -------------
     */

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }
}
