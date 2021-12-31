package com.example.dressapp1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new FirebaseOptions.Builder()
                .setApiKey("AIzaSyD0VES7-Rwrr1TafXES1FZFZ3aY3vDqE7A")
                .setDatabaseUrl("https://dressapp-ba7fe-default-rtdb.europe-west1.firebasedatabase.app")
                .setApplicationId("dressapp-ba7fe")
                .build();
    }
}