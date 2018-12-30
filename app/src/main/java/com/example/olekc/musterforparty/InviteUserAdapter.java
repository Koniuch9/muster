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

import java.util.List;

public class InviteUserAdapter extends ArrayAdapter<User> {

    private List<User> userList;
    private Context context;
    int layout;

    public InviteUserAdapter(Context context, int layout, List<User> userList)
    {
        super(context,layout,userList);
        this.context = context;
        this.layout = layout;
        this.userList = userList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater lI = LayoutInflater.from(context);
        View view = lI.inflate(layout,null,false);
        final User user = userList.get(position);
        ImageView img = view.findViewById(R.id.userPhoto);
        TextView userName = view.findViewById(R.id.userName);
        final CheckBox selected = view.findViewById(R.id.checkBox);
        userName.setText(user.name);
        selected.setChecked(user.isSelected());

        selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                user.setSelected(b);
            }
        });
        return view;
    }
}
