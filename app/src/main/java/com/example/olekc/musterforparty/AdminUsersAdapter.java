package com.example.olekc.musterforparty;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminUsersAdapter  extends ArrayAdapter<User> {

    private List<User> userList;
    private List<String> block;
    private List<String> invite;
    private Context context;
    int layout;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DatabaseReference dbRef;
    String currentGroupKey;

    public AdminUsersAdapter(Context context, int layout, List<User> userList, List<String> block, List<String> invite, String key)
    {
        super(context,layout,userList);
        this.context = context;
        this.layout = layout;
        this.userList = userList;
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        dbRef = FirebaseDatabase.getInstance().getReference();
        this.block = block;
        this.invite = invite;
        this.currentGroupKey = key;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater lI = LayoutInflater.from(context);
        View view = lI.inflate(layout,null,false);
        final User user = userList.get(position);
        Switch locationSwitch = view.findViewById(R.id.switchLocation);
        Switch inviteSwitch = view.findViewById(R.id.switchInvite);
        ImageView removeUser = view.findViewById(R.id.removeUser);
        if(block.contains(user.uid))locationSwitch.setChecked(false);
        else locationSwitch.setChecked(true);
        if(invite.contains(user.uid))inviteSwitch.setChecked(true);
        else inviteSwitch.setChecked(false);
        ImageView img = view.findViewById(R.id.userIcon);
        if(user.photoUrl != null && !user.photoUrl.equals(""))Picasso.get().load(user.photoUrl).into(img);

        TextView userName = view.findViewById(R.id.textView);

        userName.setText(user.name);

        locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!b)
                    dbRef.child("users").child(currentUser.getUid()).child("block").child(user.uid).setValue(true);
                else
                    dbRef.child("users").child(currentUser.getUid()).child("block").child(user.uid).removeValue();
            }
        });

        inviteSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!b)dbRef.child("groups").child(currentGroupKey).child("invite").child(user.uid).removeValue();
                else dbRef.child("groups").child(currentGroupKey).child("invite").child(user.uid).setValue(b);
            }
        });

        removeUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Czy na pewno chcesz usunąć użytkownika z grupy?")
                        .setPositiveButton("Usuń", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dbRef.child("groups").child(currentGroupKey).child("members").child(user.uid).removeValue();
                                dbRef.child("users").child(user.uid).child("groups").child(currentGroupKey).removeValue();
                                ((GroupActivity)context).userList.remove(user);
                                ((GroupActivity)context).refreshAdapter(true);
                            }
                        }).setNegativeButton("Nie",null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return view;
    }
}
