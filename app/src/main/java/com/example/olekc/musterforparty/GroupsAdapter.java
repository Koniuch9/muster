package com.example.olekc.musterforparty;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class GroupsAdapter extends ArrayAdapter<Group> {

    List<Group> groupList;
    Context context;
    int layout;

    public GroupsAdapter(Context context, int layout, List<Group> groupList)
    {
        super(context,layout,groupList);
        this.context = context;
        this.groupList = groupList;
        this.layout = layout;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater lI = LayoutInflater.from(context);
        View view = lI.inflate(layout,null,false);
        final Group group = groupList.get(position);

        TextView groupName = view.findViewById(R.id.groupName);
        groupName.setText(group.name);

        return view;
    }
}
