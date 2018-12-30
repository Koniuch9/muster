package com.example.olekc.musterforparty;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class GroupsActivity extends AppCompatActivity {

    ListView groupList;
    List<Group> groups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        groupList = findViewById(R.id.groupsList);
        groups = new ArrayList<>();

        groups.add(new Group("Fajna",1,true));
        groups.add(new Group("Party Hard",1,true));
        groups.add(new Group("Party Rock",1,true));
        groups.add(new Group("Rodzinka",1,true));
        groups.add(new Group("Imprezy",1,true));
        groups.add(new Group("Chlanie",1,true));
        groups.add(new Group("Zabawa",1,true));

        GroupsAdapter adapter = new GroupsAdapter(this,R.layout.groups,groups);
        groupList.setAdapter(adapter);
    }

    public void Back(View view) {
        Intent i = new Intent(GroupsActivity.this,TrackActivity.class);
        startActivity(i);
        finish();
    }
}
