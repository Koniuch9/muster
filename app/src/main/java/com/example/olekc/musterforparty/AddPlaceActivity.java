package com.example.olekc.musterforparty;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddPlaceActivity extends AppCompatActivity {

    TextView currentLocation;
    FusedLocationProviderClient fusedClient;
    LatLng position;
    EditText placeName;
    EditText placeNote;
    String currentGroupKey;
    Bundle extra;
    private DatabaseReference dbRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);
        extra = getIntent().getExtras();
        if(extra == null || (currentGroupKey=extra.getString("groupId")).equals(""))finish();
        placeName = findViewById(R.id.editText);
        placeNote = findViewById(R.id.editText3);
        currentLocation = findViewById(R.id.textView3);
        fusedClient = LocationServices.getFusedLocationProviderClient(this);
        dbRef = FirebaseDatabase.getInstance().getReference();
        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            fusedClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null)
                    {
                        Double lat = location.getLatitude(),lng=location.getLongitude();
                        LatLng pos = new LatLng(lat,lng);
                        position = pos;
                        currentLocation.setText(String.format(Locale.US,"lat: %.4f, lon: %.4f",lat,lng));
                    }
                }
            });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 123)
        {
            if(resultCode == RESULT_OK)
            {
                String returned = data.getData().toString();
                Double lat,lng;
                String[] tab = returned.split(",");
                lat = Double.parseDouble(tab[0]);
                lng = Double.parseDouble(tab[1]);
                currentLocation.setText(String.format(Locale.US,"lat: %.4f, lon: %.4f",lat,lng));
                position = new LatLng(lat,lng);
            }
        }
    }

    public void Confirm(View view) {

        if(!placeName.getText().equals(""))
        {
            String key;
            key = dbRef.child("groups").child(currentGroupKey).child("places").push().getKey();
            Place p = new Place(placeName.getText().toString(),placeNote.getText().toString());
            Map<String, Double> loc = new HashMap<>();
            loc.put("lat",position.latitude);
            loc.put("lon",position.longitude);
            p.location = loc;
            dbRef.child("groups").child(currentGroupKey).child("places").child(key).setValue(p.toMap());
        }
        finish();
    }

    public void Cancel(View view) {
        /*Intent i = new Intent(AddPlaceActivity.this, TrackActivity.class);
        startActivity(i);*/
        finish();
    }

    public void changeLocation(View view) {
        Intent i = new Intent(AddPlaceActivity.this, ChoosePlaceLocationActivity.class);
        startActivityForResult(i,123);
    }
}
