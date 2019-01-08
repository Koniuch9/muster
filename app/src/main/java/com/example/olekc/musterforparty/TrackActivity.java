package com.example.olekc.musterforparty;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

public class TrackActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int ADD_GROUP_RQ = 1000;
    private static final int ADD_PLACE_RQ = 1001;
    private static final int GROUPS_RQ = 1002;
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private DatabaseReference dbRef;
    private FirebaseUser user;
    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleMap mMap;
    private List<Group> userGroups;
    private List<User> currentUsers;
    private List<Place> currentPlaces;
    private List<Marker> currentUsersMarkers;
    private List<Marker> currentPlaceMarkers;
    private Group currentGroup;
    private String currentGroupKey;
    private SharedPreferences sP;
    private FabSpeedDial fabSpeedDial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        if(!checkPermission())
        {
            requestPermission();
        }
        fabSpeedDial = findViewById(R.id.fab_speed_dial);
        sP = TrackActivity.this.getPreferences(Context.MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        dbRef = FirebaseDatabase.getInstance().getReference();
        currentUsersMarkers = new ArrayList<>();
        currentPlaceMarkers = new ArrayList<>();
        userGroups = new ArrayList<>();
        setUserGroups();
        currentUsers = new ArrayList<>();
        currentPlaces = new ArrayList<>();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setCurrentUsers();
        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter(){
            @Override
            public boolean onMenuItemSelected(MenuItem item)
            {
                Intent i;
                switch(item.getItemId())
                {
                    case R.id.add_group:
                        i = new Intent(TrackActivity.this,AddGroupActivity.class);
                        startActivityForResult(i,ADD_GROUP_RQ);
                        return true;
                    case R.id.add_place:
                        i = new Intent(TrackActivity.this,AddPlaceActivity.class);
                        i.putExtra("groupId",currentGroupKey);
                        startActivity(i);
                        return true;
                    case R.id.show_group:
                        i = new Intent(TrackActivity.this,GroupsActivity.class);
                        startActivity(i);
                        return true;
                    case R.id.switch_group:
                        showSwitchGroupDialog();
                    default:
                        return false;
                }
            }
        });
    }

    /*
   ------------- M E N U -------------
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu,menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch(item.getItemId())
        {
            case R.id.log_out:
                mAuth.signOut();
                i = new Intent(TrackActivity.this,LoginActivity.class);
                startActivity(i);
                finish();
                return true;
           /* case R.id.add_place:
                i = new Intent(TrackActivity.this,AddPlaceActivity.class);
                i.putExtra("groupId",currentGroupKey);
                startActivity(i);
                return true;
            case R.id.show_group:
                i = new Intent(TrackActivity.this,GroupsActivity.class);
                startActivity(i);
                return true;
            case R.id.switch_group:
                showSwitchGroupDialog();*/
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
                mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location != null)
                        {
                            Double lat,lng;
                            Map<String, Double> loc = new HashMap<>();
                            loc.put("lat",lat=location.getLatitude());
                            loc.put("lon",lng=location.getLongitude());
                            dbRef.child("users").child(user.getUid()).child("location").setValue(loc);
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat,lng)));
                        }
                    }
                });
               // mMap.addMarker(new MarkerOptions().position(new LatLng(51,17)).title("To ja siema").icon(Picasso.get().load(user.getPhotoUrl()).));
            }

        }
    }

    public void showCurrentUsers()
    {
        if(!currentUsersMarkers.isEmpty())
        {
            for(Marker m : currentUsersMarkers)
            {
                m.remove();
            }
            currentUsersMarkers.clear();
        }
        if(!currentUsers.isEmpty() && mMap != null)
        {
            final LatLngBounds.Builder builder = new LatLngBounds.Builder();
            int i=0;
            for(User u : currentUsers)
            {
                double lat = u.location.get("lat"),lng = u.location.get("lon");
                LatLng pos = new LatLng(lat,lng);
                builder.include(pos);
                Marker m;
                if(u.photoUrl != null && !u.photoUrl.equals(""))
                {
                    m = mMap.addMarker(new MarkerOptions().position(pos).title(u.name));
                    Target marker = new PicassoMarker(m);
                    Picasso.get().load(u.photoUrl).into(marker);
                } else{
                    m = mMap.addMarker(new MarkerOptions().position(pos).title(u.name).icon(bitmapDescriptorFromVector2(this,R.drawable.ic_user_icon)));
                      }
                currentUsersMarkers.add(m);
                i++;
            }
            if(checkPermission())
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null)
                    {
                        Double lat = location.getLatitude(),lng = location.getLongitude();
                        builder.include(new LatLng(lat,lng));
                        LatLngBounds bounds = builder.build();
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,40),3000,null);
                    }
                    setCurrentPlaces();
                }
            });
        }
    }
     /*
   END ------------- M A P A -------------
    */

      /*
    ------------- L O G I K A -------------
    */

    public void setCurrentUsers()
    {
        if(!currentUsers.isEmpty())
            currentUsers.clear();
            if(!(currentGroupKey=sP.getString(user.getUid()+"Group","")).equals(""))
            {
                final Map<String,Boolean> members = new HashMap<>();
                dbRef.child("groups").child(currentGroupKey).child("members").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for(DataSnapshot ds : dataSnapshot.getChildren())
                            {
                                if((Boolean)ds.getValue())
                                {
                                    if(!ds.getKey().equals(user.getUid()))
                                        members.put(ds.getKey(),(Boolean)ds.getValue());
                                }
                            }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                dbRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren())
                        {
                            if(members.containsKey(ds.getKey()))
                            {
                                currentUsers.add(ds.getValue(User.class));
                            }
                        }
                        showCurrentUsers();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

    }

    public void setCurrentPlaces()
    {

        if(!currentPlaces.isEmpty())
            currentPlaces.clear();
        if(currentGroupKey != null && !currentGroupKey.equals(""))
        {
            dbRef.child("groups").child(currentGroupKey).child("places").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for(DataSnapshot ds : dataSnapshot.getChildren())
                    {
                        Place p = ds.getValue(Place.class);
                        currentPlaces.add(p);
                    }
                    showCurrentPlaces();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void showCurrentPlaces()
    {
        if(!currentPlaceMarkers.isEmpty())
        {
            for(Marker m : currentPlaceMarkers)
            {
                m.remove();
            }
            currentPlaceMarkers.clear();
        }
        if(mMap != null && currentPlaces != null)
        for(Place p : currentPlaces)
        {
           Marker m = mMap.addMarker(new MarkerOptions()
                    .icon(bitmapDescriptorFromVector2(this,R.drawable.ic_place_icon))
                    .position(new LatLng(p.location.get("lat"),p.location.get("lon")))
                    .title(p.name)
                    .snippet(p.note)
                    );
           currentPlaceMarkers.add(m);
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector2(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

   /* private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_map_pin_filled_blue_48dp);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
*/
    public void setUserGroups()
    {
        final Map<String, Boolean> usrGroups = new HashMap<>();
        dbRef.child("users").child(user.getUid()).child("groups").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    if((Boolean)ds.getValue())
                    usrGroups.put(ds.getKey(),(Boolean)ds.getValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        dbRef.child("groups").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    if(usrGroups.containsKey(ds.getKey()))
                    {
                        Group g = ds.getValue(Group.class);
                        g.member = usrGroups.get(ds.getKey());
                        g.key = ds.getKey();
                        userGroups.add(g);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void showSwitchGroupDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(TrackActivity.this);
        builder.setTitle("Wybierz grupę");
        builder.setAdapter(new GroupsAdapter(TrackActivity.this, R.layout.groups, userGroups, currentGroupKey), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences.Editor editor = sP.edit();
                editor.putString(user.getUid()+"Group",userGroups.get(i).key);
                editor.commit();
                setCurrentUsers();
                setCurrentPlaces();
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode)
        {
            case ADD_GROUP_RQ:
                break;

        }
    }

    /*
   END ------------- L O G I K A -------------
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
