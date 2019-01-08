package com.example.olekc.musterforparty;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MemberUserAdapter  extends ArrayAdapter<User> {
    private List<User> userList;
    private List<String> block;
    private Context context;
    int layout;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DatabaseReference dbRef;

    public MemberUserAdapter(Context context, int layout, List<User> userList, List<String> block)
    {
        super(context,layout,userList);
        this.context = context;
        this.layout = layout;
        this.userList = userList;
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        dbRef = FirebaseDatabase.getInstance().getReference();
        this.block = block;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater lI = LayoutInflater.from(context);
        View view = lI.inflate(layout,null,false);
        final User user = userList.get(position);
        Switch blockLocation = view.findViewById(R.id.switchLocation);
        blockLocation.setChecked(!block.contains(user.uid));
        ImageView img = view.findViewById(R.id.userIcon);
        if(user.photoUrl != null && !user.photoUrl.equals(""))Picasso.get().load(user.photoUrl).into(img);
        //GlideApp.with(this).load("").into(img);
        TextView userName = view.findViewById(R.id.userName);

        userName.setText(user.name);

        blockLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!b)
                    dbRef.child("users").child(currentUser.getUid()).child("block").child(user.uid).setValue(true);
                else
                    dbRef.child("users").child(currentUser.getUid()).child("block").child(user.uid).removeValue();
            }
        });

        return view;
    }
}
