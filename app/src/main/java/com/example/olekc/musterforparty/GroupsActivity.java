package com.example.olekc.musterforparty;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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

public class GroupsActivity extends AppCompatActivity {

    ListView groupList;
    List<Group> groups;
    DatabaseReference dbRef;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        groupList = findViewById(R.id.groupsList);
        groups = new ArrayList<>();

        dbRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        /*groups.add(new Group("Fajna",1,true));
        groups.add(new Group("Party Hard",1,true));
        groups.add(new Group("Party Rock",1,true));
        groups.add(new Group("Rodzinka",1,true));
        groups.add(new Group("Imprezy",1,true));
        groups.add(new Group("Chlanie",1,true));
        groups.add(new Group("Zabawa",1,true));*/
        final Map<String, Boolean> userGroups = new HashMap<>();
        dbRef.child("users").child(user.getUid()).child("groups").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    userGroups.put(ds.getKey(),(Boolean)ds.getValue());
                    Log.v("grupyusera",ds.getKey());
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
                    Log.v("grupy",ds.getKey());
                    if(userGroups.containsKey(ds.getKey()))
                    {
                        Group g = ds.getValue(Group.class);
                        g.member = userGroups.get(ds.getKey());
                        g.key = ds.getKey();
                        groups.add(g);
                    }
                }
                GroupsAdapter adapter = new GroupsAdapter(GroupsActivity.this,R.layout.groups,groups);
                groupList.setAdapter(adapter);
                groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(GroupsActivity.this,GroupActivity.class);
                        intent.putExtra("groupId",groups.get(i).key);
                        startActivity(intent);
                        finish();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public ListView getGroupList()
    {
        return groupList;
    }

    public void Back(View view) {
        Intent i = new Intent(GroupsActivity.this,TrackActivity.class);
        startActivity(i);
        finish();
    }
}
