package com.example.olekc.musterforparty;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EditGroupActivity extends AppCompatActivity {

    EditText groupName;
    DatabaseReference dbRef;
    FirebaseAuth mAuth;
    FirebaseUser user;
    String key;
    Bundle extras;
    Group g;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);
        groupName = findViewById(R.id.editText2);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        dbRef = FirebaseDatabase.getInstance().getReference();
        extras = getIntent().getExtras();
        key = extras.getString("groupId");
        dbRef.child("groups").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                g = dataSnapshot.getValue(Group.class);
                groupName.setText(g.name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void deleteGroup(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditGroupActivity.this);
        builder.setMessage(R.string.delete_group_warning);
        builder.setTitle(R.string.delete_group_alert);
        builder.setPositiveButton(R.string.delete_group_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if((key = extras.getString("groupId")) != null)
                {
                    Map<String,Object> childUpdates = new HashMap<>();
                    for(String userKey : g.members.keySet())
                    {
                        childUpdates.put("/users/"+userKey+"/groups/"+key,null);
                    }
                    dbRef.child("groups").child(key).removeValue();
                    dbRef.updateChildren(childUpdates);
                    finish();
                }
            }
        });
        builder.setNegativeButton(R.string.delete_group_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
