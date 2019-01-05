package com.example.olekc.musterforparty;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
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
    private Context context;
    int layout;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference dbRef;

    public AdminUsersAdapter(Context context, int layout, List<User> userList)
    {
        super(context,layout,userList);
        this.context = context;
        this.layout = layout;
        this.userList = userList;
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        dbRef = FirebaseDatabase.getInstance().getReference();
        
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater lI = LayoutInflater.from(context);
        View view = lI.inflate(layout,null,false);
        final User user = userList.get(position);

        ImageView img = view.findViewById(R.id.userIcon);
        if(user.photoUrl != null && !user.photoUrl.equals(""))Picasso.get().load(user.photoUrl).into(img);
        //GlideApp.with(this).load("").into(img);
        TextView userName = view.findViewById(R.id.textView);

        userName.setText(user.name);

        return view;
    }
}
