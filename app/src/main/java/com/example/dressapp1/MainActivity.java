package com.example.dressapp1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;

import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.dressapp1.model.interfaces.PermissionCallback;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    public static PermissionCallback permissionCallback;
    NavController navCtrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 123: {
                boolean result = (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED);
                if (permissionCallback != null)
                    permissionCallback.onResult(result);
                break;
            }
        }
    }
}