package com.example.olekc.musterforparty;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class GroupsAdapter extends ArrayAdapter<Group> {

    List<Group> groupList;
    Context context;
    ImageView adminIcon;
    ImageView acceptIcon;
    ImageView refuseIcon;
    int layout;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference dbRef;

    public GroupsAdapter(Context context, int layout, List<Group> groupList)
    {
        super(context,layout,groupList);
        this.context = context;
        this.groupList = groupList;
        this.layout = layout;
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        dbRef = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater lI = LayoutInflater.from(context);
        View view = lI.inflate(layout,null,false);
        final Group group = groupList.get(position);
        adminIcon = view.findViewById(R.id.adminIcon);
        acceptIcon = view.findViewById(R.id.acceptIcon);
        refuseIcon = view.findViewById(R.id.refuseIcon);
        if(!user.getUid().equals(group.admin))adminIcon.setVisibility(View.GONE);
        if(group.member)
        {
            acceptIcon.setVisibility(View.GONE);
            refuseIcon.setVisibility(View.GONE);
        }
        else
        {
            acceptIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dbRef.child("users").child(user.getUid()).child("groups").child(group.key).setValue(true);
                    dbRef.child("groups").child(group.key).child("members").child(user.getUid()).setValue(true);
                    acceptIcon.setVisibility(View.GONE);
                    refuseIcon.setVisibility(View.GONE);
                }
            });
            refuseIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dbRef.child("groups").child(group.key).child("members").child(user.getUid()).removeValue();
                    dbRef.child("users").child(user.getUid()).child("groups").child(group.key).removeValue();
                    acceptIcon.setVisibility(View.GONE);
                    refuseIcon.setVisibility(View.GONE);
                }
            });
        }
        TextView groupName = view.findViewById(R.id.groupName);
        groupName.setText(group.name);

        return view;
    }
}
