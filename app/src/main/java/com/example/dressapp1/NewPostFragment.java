package com.example.dressapp1;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Looper;
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
import com.example.dressapp1.model.interfaces.EditProductListener;
import com.example.dressapp1.model.interfaces.PermissionCallback;
import com.example.dressapp1.model.interfaces.UploadProductListener;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NewPostFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    View view;
    Spinner genderInput, sizeInput, categoryInput;
    EditText priceInput;
    Button uploadBtn;
    ImageButton uploadImageBtn, myProfileBtn, searchBtn;
    Bitmap bitmap;
    Product product;
    ProgressBar progressBar;
    Product existingProduct;

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
        myProfileBtn = view.findViewById(R.id.bottom_bar_profile);
        searchBtn = view.findViewById(R.id.bottom_bar_search);
        existingProduct = NewPostFragmentArgs.fromBundle(getArguments()).getExistingProduct();

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
        uploadBtn.setOnClickListener(this);
        uploadImageBtn.setOnClickListener(this);
        myProfileBtn.setOnClickListener(this);
        searchBtn.setOnClickListener(this);

        if(existingProduct != null) {
            // loading info about the existing product into the fields.
            int genderPos = genderAd.getPosition(existingProduct.getGender());
            int sizePos = sizeAd.getPosition(existingProduct.getSize());
            int categoryPos = categoryAd.getPosition(existingProduct.getCategory());

            genderInput.setSelection(genderPos);
            sizeInput.setSelection(sizePos);
            categoryInput.setSelection(categoryPos);

            priceInput.setText(existingProduct.getPrice());
            Picasso.get().load(existingProduct.getImg()).into(uploadImageBtn);

            uploadBtn.setText("Edit Post");
        }

        setLocation();

        return view;
    }

    private void setLocation() {
        if (
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.REQUEST_CODE_LOCATION_PERMISSION
            );

            return;
        }
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(getActivity())
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(@NonNull LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(getActivity())
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            double longtitue = locationResult.getLastLocation().getLongitude();
                            double latitude = locationResult.getLastLocation().getLatitude();

                            product.setLongtitude(longtitue);
                            product.setLantitude(latitude);
                        }
                    }
                }, Looper.getMainLooper());
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
            case R.id.bottom_bar_profile:
                Navigation.findNavController(view).navigate(NewPostFragmentDirections.actionNewPostFragmentToMyProfileFragment());
                break;
            case R.id.bottom_bar_search:
                Navigation.findNavController(view).navigate(NewPostFragmentDirections.actionNewPostFragmentToSelectGenderFragment());
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
            Toast.makeText(getActivity(), "Gender is a required field", Toast.LENGTH_SHORT).show();
            return;
        }
        if(size.isEmpty()) {
            Toast.makeText(getActivity(), "Size is a required field", Toast.LENGTH_SHORT).show();
            return;
        }
        if(category.isEmpty()) {
            Toast.makeText(getActivity(), "Category is a required field", Toast.LENGTH_SHORT).show();
            return;
        }
        if(bitmap == null && existingProduct == null && existingProduct.getImg() == null) {
            Toast.makeText(getActivity(), "Photo is a required field", Toast.LENGTH_LONG).show();
            return;
        }
        setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        product.setSize(size);
        product.setPrice(price);
        product.setGender(gender);
        product.setCategory(category);

        if(existingProduct != null) {
            // edit the product.
            existingProduct.setSize(size);
            existingProduct.setPrice(price);
            existingProduct.setGender(gender);
            existingProduct.setCategory(category);

            Model.instance.editPost(existingProduct, bitmap, new EditProductListener() {
                @Override
                public void onComplete(Product product) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Model.instance.reloadProductList();
                    Navigation.findNavController(view).navigate(NewPostFragmentDirections.actionNewPostFragmentToMyProfileFragment());
                }
            });
        } else {
            // create new product
            Model.instance.addPost(product, bitmap, new UploadProductListener() {
                @Override
                public void onComplete(Task task, Product product, String userId) {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Model.instance.reloadProductList();
                        Toast.makeText(getActivity(), "Upload successfully", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(view).navigate(NewPostFragmentDirections.actionNewPostFragmentToProductGridFragment());
                    }
                }
            });
        }

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
//        String selected = "";
//        selected = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}