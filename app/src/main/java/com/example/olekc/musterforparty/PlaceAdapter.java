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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class PlaceAdapter  extends ArrayAdapter<Place> {

    List<Place> places;
    Context context;
    int layout;
    boolean adminView;
    String currentGroupKey;

    public PlaceAdapter(Context context, int layout, List<Place> placeList, boolean adminView, String currentGroup) {
        super(context, layout, placeList);
        this.context = context;
        this.layout = layout;
        this.adminView = adminView;
        this.currentGroupKey = currentGroup;
        this.places = placeList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater lI = LayoutInflater.from(context);
        View view = lI.inflate(layout, null, false);
        final Place place = places.get(position);
        TextView name = view.findViewById(R.id.placeName);
        ImageView removePlace = view.findViewById(R.id.removePlace);
        if(!adminView)removePlace.setVisibility(View.GONE);
        else
            {
                removePlace.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Czy na pewno chcesz usunąć miejsce z grupy?")
                                .setPositiveButton("Usuń", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                                        dbRef.child("groups").child(currentGroupKey).child("places").child(place.key).removeValue();
                                        ((GroupActivity)context).placeList.remove(place);
                                        ((GroupActivity)context).refreshAdapter(false);
                                    }
                                }).setNegativeButton("Nie",null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
            }
        name.setText(places.get(position).name);
        return view;
    }
}
