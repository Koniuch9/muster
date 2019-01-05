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
    ImageView exitEdit;
    List<User> userList;
    boolean adminView;
    String key;
    Bundle extras;
    Group g;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        userList = new ArrayList<>();
        extras = getIntent().getExtras();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance();
        dbRef = db.getReference();
        groupName = findViewById(R.id.groupName);
        exitEdit = findViewById(R.id.exit_editGroup);
        members = findViewById(R.id.group_user_list);
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
                        adminView = true;
                    }
                    else adminView = false;

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
                            userList.add(u);
                        }
                    }

                    AdminUsersAdapter adapter = new AdminUsersAdapter(GroupActivity.this,R.layout.users_admin, userList);
                    members.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


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
