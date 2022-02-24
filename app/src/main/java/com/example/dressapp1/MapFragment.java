package com.example.dressapp1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.dressapp1.MainActivity;
import com.example.dressapp1.MyApplication;
import com.example.dressapp1.R;
import com.example.dressapp1.model.Product;
import com.example.dressapp1.model.User;
import com.example.dressapp1.model.interfaces.PermissionCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {
    Product curProduct;
    View view;
    Button backToProdBtn, callBtn;
    ImageButton myProfileBtn, searchBtn, addProductBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        curProduct = MapFragmentArgs.fromBundle(getArguments()).getProduct();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng prodcutLocation = new LatLng(curProduct.getLantitude(), curProduct.getLongtitude());
        Log.d("D", curProduct.getLantitude() + "");
        MarkerOptions markerOptions = new MarkerOptions()
                .position(prodcutLocation)
                .title(curProduct.getCategory());

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(prodcutLocation,40F));
        googleMap.addMarker(markerOptions);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map, container, false);

        backToProdBtn = view.findViewById(R.id.product_map_back);
        callBtn = view.findViewById(R.id.product_map_call);
        myProfileBtn = view.findViewById(R.id.bottom_bar_profile);
        searchBtn = view.findViewById(R.id.bottom_bar_search);
        addProductBtn = view.findViewById(R.id.add_new_post_btn);

        backToProdBtn.setOnClickListener(this);
        callBtn.setOnClickListener(this);
        myProfileBtn.setOnClickListener(this);
        searchBtn.setOnClickListener(this);
        addProductBtn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(getContext(), "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.product_map_back:
                Navigation.findNavController(view).navigate(MapFragmentDirections.actionMapFragmentToProductPageFragment(curProduct));
                break;
            case R.id.product_map_call:
                call();
                break;
            case R.id.bottom_bar_profile:
                Navigation.findNavController(view).navigate(MapFragmentDirections.actionMapFragmentToMyProfileFragment());
                break;
            case R.id.bottom_bar_search:
                Navigation.findNavController(view).navigate(MapFragmentDirections.actionMapFragmentToSelectGenderFragment());
                break;
            case R.id.add_new_post_btn:
                Navigation.findNavController(view).navigate(MapFragmentDirections.actionMapFragmentToNewPostFragment(null));
                break;
            default:
                break;

        }
    }

    public void call() {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",  curProduct.getOwnerPhone() + "", null)));
        Toast.makeText(getActivity(), "phone call", Toast.LENGTH_LONG).show();
    }
}