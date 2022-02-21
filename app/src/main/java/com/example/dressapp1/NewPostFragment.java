package com.example.dressapp1;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.dressapp1.model.Model;
import com.example.dressapp1.model.Product;
import com.example.dressapp1.model.helpers.Constants;
import com.example.dressapp1.model.interfaces.PermissionCallback;
import com.example.dressapp1.model.interfaces.UploadProductListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

public class NewPostFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    View view;
    Spinner genderInput, sizeInput, categoryInput;
    EditText priceInput;
    Button uploadBtn;
    ImageButton uploadImageBtn;
    Bitmap bitmap;
    Product product;
    ProgressBar progressBar;
    OnMapReadyCallback onMapReadyCallback;
    LatLng lastKnownLocation = null;
    MapView map;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_new_post, container, false);
        genderInput = view.findViewById(R.id.select_gender);
        sizeInput = view.findViewById(R.id.select_size);
        categoryInput = view.findViewById(R.id.select_category);
        priceInput = view.findViewById(R.id.new_post_price);
        uploadBtn = view.findViewById(R.id.upload_post_btn);
        uploadImageBtn = view.findViewById(R.id.upload_img);
        progressBar = view.findViewById(R.id.new_post_progress);

        uploadBtn.setOnClickListener(this);
        uploadImageBtn.setOnClickListener(this);

        product = new Product();

        ArrayAdapter<CharSequence> genderAd = ArrayAdapter.
                createFromResource(getContext(), R.array.choose_gender, android.R.layout.simple_spinner_item);
        genderAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderInput.setAdapter(genderAd);

        ArrayAdapter<CharSequence> sizeAd = ArrayAdapter.
                createFromResource(getContext(), R.array.choose_size, android.R.layout.simple_spinner_item);
        genderAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sizeInput.setAdapter(sizeAd);

        ArrayAdapter<CharSequence> categoryAd = ArrayAdapter.
                createFromResource(getContext(), R.array.choose_category, android.R.layout.simple_spinner_item);
        genderAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryInput.setAdapter(categoryAd);

        genderInput.setOnItemSelectedListener(this);
        categoryInput.setOnItemSelectedListener(this);
        sizeInput.setOnItemSelectedListener(this);

        InitialGoogleMap(savedInstanceState);

        return view;
    }

    private void InitialGoogleMap(Bundle savedInstanceState) {
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(Constants.MAPVIEW_BUNDLE_KEY);
        }

        onMapReadyCallback = map -> {
            MainActivity.permissionCallback = new PermissionCallback() {
                @SuppressLint("MissingPermission")
                @Override
                public void onResult(boolean isGranted) {
                    if (isGranted) {
                    }
                }
            };
            if (ActivityCompat.checkSelfPermission(MyApplication.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyApplication.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);
            }

            map.setMyLocationEnabled(true);
            map.setOnMapClickListener(latLng -> {
                lastKnownLocation = new LatLng(latLng.latitude, latLng.longitude);
                map.addMarker(new MarkerOptions().position(new LatLng(latLng.latitude, latLng.longitude)).title(product.getCategory()));
            });

            if (lastKnownLocation == null)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(31.789080,34.654600), 7.5F));
        };
        map.onCreate(mapViewBundle);
        map.getMapAsync(onMapReadyCallback);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.upload_post_btn:
                uploadProduct();
                break;
            case R.id.upload_img:
                uploadImage();
                break;
        }
    }

    private void setEnabled(boolean isEnabled) {
        priceInput.setEnabled(isEnabled);
        genderInput.setEnabled(isEnabled);
        categoryInput.setEnabled(isEnabled);
        sizeInput.setEnabled(isEnabled);
        uploadImageBtn.setEnabled(isEnabled);
        uploadBtn.setEnabled(isEnabled);
    }

    private void uploadProduct() {
        String gender = genderInput.getSelectedItem().toString();
        String size = sizeInput.getSelectedItem().toString();
        String category = categoryInput.getSelectedItem().toString();
        String price = priceInput.getText().toString();

        if(price.isEmpty()) {
            priceInput.setError("This is a required field");
            priceInput.requestFocus();
            return;
        }
        if(gender.isEmpty()) {
            return;
        }
        if(size.isEmpty()) {
            return;
        }
        if(category.isEmpty()) {
            return;
        }
        if(bitmap == null) {
            return;
        }
        setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        product.setSize(size);
        product.setPrice(price);
        product.setGender(gender);
        product.setCategory(category);

        Model.instance.addPost(product, bitmap, new UploadProductListener() {
            @Override
            public void onComplete(Task task, Product product, String userId) {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getActivity(), "Upload successfully", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(view).navigate(NewPostFragmentDirections.actionNewPostFragmentToProductPageFragment(product));
                }
            }
        });
    }

    private void uploadImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bundle bundle = data.getExtras();
            bitmap = (Bitmap) bundle.get("data");
            uploadImageBtn.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View v, int i, long l) {
        String selected = "";
        selected = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}