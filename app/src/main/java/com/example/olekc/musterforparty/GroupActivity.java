package com.example.olekc.musterforparty;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupActivity extends AppCompatActivity {

    FirebaseUser user;
    FirebaseAuth mAuth;
    FirebaseDatabase db;
    DatabaseReference dbRef;
    TextView groupName;
    ListView members;
    ListView places;
    ImageView exitEdit;
    List<User> userList;
    List<Place> placeList;
    List<String> block;
    List<String> invite;
    boolean adminView;
    String key;
    Bundle extras;
    Group g;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        userList = new ArrayList<>();
        placeList = new ArrayList<>();
        block = new ArrayList<>();
        invite = new ArrayList<>();
        extras = getIntent().getExtras();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance();
        dbRef = db.getReference();
        groupName = findViewById(R.id.groupName);
        exitEdit = findViewById(R.id.exit_editGroup);
        members = findViewById(R.id.group_user_list);
        places = findViewById(R.id.group_place_list);
        if((key = extras.getString("groupId")) != null)
        {

            dbRef.child("groups").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    g = dataSnapshot.getValue(Group.class);
                    groupName.setText(g.name);
                    if(g.admin.equals(user.getUid()))
                    {
                        exitEdit.setImageDrawable(getDrawable(R.drawable.ic_edit_icon));
                        invite.addAll(g.invite.keySet());
                        adminView = true;
                    }
                    else adminView = false;

                    for(DataSnapshot dss : dataSnapshot.getChildren())
                    {
                        if(dss.getKey().equals("places"))
                        {
                            for(DataSnapshot ds : dss.getChildren())
                            {
                                Place p = ds.getValue(Place.class);
                                if(p != null) {
                                    p.key = ds.getKey();
                                    placeList.add(p);
                                }
                            }

                    PlaceAdapter adapter = new PlaceAdapter(GroupActivity.this,R.layout.places,placeList,adminView,key);
                    places.setAdapter(adapter);
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
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        if(g.members.containsKey(ds.getKey()))
                        {
                            User u = ds.getValue(User.class);
                            u.uid = ds.getKey();
                            if(!ds.getKey().equals(user.getUid()))
                                userList.add(u);
                            else
                                {
                                    block.addAll(u.block.keySet());
                                }
                        }
                    }

                    if(adminView) {
                        AdminUsersAdapter adapter = new AdminUsersAdapter(GroupActivity.this, R.layout.users_admin, userList, block, invite, key);
                        members.setAdapter(adapter);
                    } else {
                        MemberUserAdapter adapter = new MemberUserAdapter(GroupActivity.this, R.layout.users_member, userList, block);
                        members.setAdapter(adapter);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            /*dbRef.child("groups").child(key).child("places").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren())
                    {
                        Place p = ds.getValue(Place.class);
                        if(p != null) {
                            p.key = ds.getKey();
                            placeList.add(p);
                            Toast.makeText(GroupActivity.this,p.name,Toast.LENGTH_SHORT).show();
                        }
                    }

                    *//*PlaceAdapter adapter = new PlaceAdapter(GroupActivity.this,R.layout.places,placeList,adminView,key);
                    places.setAdapter(adapter);*//*
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });*/

        }
    }

    public void refreshAdapter(boolean t)
    {
        if(t) {
            if (adminView) {
                AdminUsersAdapter adapter = new AdminUsersAdapter(GroupActivity.this, R.layout.users_admin, userList, block, invite, key);
                members.setAdapter(adapter);
            } else {
                MemberUserAdapter adapter = new MemberUserAdapter(GroupActivity.this, R.layout.users_member, userList, block);
                members.setAdapter(adapter);
            }
        } else {
            PlaceAdapter adapter = new PlaceAdapter(GroupActivity.this,R.layout.places,placeList,adminView,key);
            places.setAdapter(adapter);
        }
    }

    public void exitEdit(View view) {

        if(adminView)
        {
            Intent i = new Intent(GroupActivity.this,EditGroupActivity.class);
            i.putExtra("groupId",key);
            startActivity(i);
            finish();
        }
        else
        {
            // exit
            AlertDialog.Builder builder = new AlertDialog.Builder(GroupActivity.this);
            builder.setMessage(R.string.exit_group_warning);
            builder.setTitle(R.string.exit_group_alert);
            builder.setPositiveButton(R.string.exit_group_confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if((key = extras.getString("groupId")) != null)
                    {
                        dbRef.child("groups").child(key).child("members").child(user.getUid()).removeValue();
                        dbRef.child("users").child(user.getUid()).child("groups").child(key).removeValue();
                        finish();
                    }
                }
            });
            builder.setNegativeButton(R.string.exit_group_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
