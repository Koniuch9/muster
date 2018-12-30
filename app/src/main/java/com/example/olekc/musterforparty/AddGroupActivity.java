package com.example.olekc.musterforparty;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddGroupActivity extends AppCompatActivity {

    ListView usersInvite;
    List<User> userList;
    private FirebaseDatabase database;
    FirebaseAuth mAuth;
    DatabaseReference dbRef;
    EditText name;
    Spinner type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        mAuth = FirebaseAuth.getInstance();
        userList = new ArrayList<>();
        usersInvite = findViewById(R.id.usersInvite);
        database = FirebaseDatabase.getInstance();
        type = findViewById(R.id.groupType);
        name = findViewById(R.id.groupName);
        dbRef = database.getReference();

        //example data add
        userList.add(new User("Marcin Wolski"));
        userList.add(new User("Marcin Wolski Junior kurde"));
        userList.add(new User("Aleksander Maciejaka"));
        userList.add(new User("Lolek"));
        userList.add(new User("Bojan"));
        userList.add(new User("Martucha"));
        userList.add(new User("Wilku"));
        userList.add(new User("O_O"));

        InviteUserAdapter adapter = new InviteUserAdapter(this,R.layout.users_invite, userList);
        usersInvite.setAdapter(adapter);


        usersInvite.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                boolean x = userList.get(pos).changeSelected();
                ((CheckBox)view.findViewById(R.id.checkBox)).setChecked(x);
            }
        });
    }

    public void Confirm(View view) {
        if(!name.getText().toString().equals(""))
        {
            String key = dbRef.child("groups").push().getKey();
            Group gr = new Group(name.getText().toString(), 1, true);
            FirebaseUser user = mAuth.getCurrentUser();
            gr.members.put(user.getUid(),true);
            Map<String,Object> groupVals = gr.toMap();
            Map<String,Object> childUpdates = new HashMap<>();
            dbRef.child("users").child(user.getUid()).child("groups").child(key).setValue(true);
            childUpdates.put("/groups/"+key,groupVals);
            dbRef.updateChildren(childUpdates);
        }
        Intent i = new Intent(AddGroupActivity.this, TrackActivity.class);
        startActivity(i);
        finish();
    }

    public void Cancel(View view) {
        Intent i = new Intent(AddGroupActivity.this, TrackActivity.class);
        startActivity(i);
        finish();
    }
}
