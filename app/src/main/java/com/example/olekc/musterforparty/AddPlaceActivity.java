package com.example.olekc.musterforparty;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AddPlaceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);
    }

    public void Confirm(View view) {
        Intent i = new Intent(AddPlaceActivity.this, TrackActivity.class);
        startActivity(i);
        finish();
    }

    public void Cancel(View view) {
        Intent i = new Intent(AddPlaceActivity.this, TrackActivity.class);
        startActivity(i);
        finish();
    }
}
