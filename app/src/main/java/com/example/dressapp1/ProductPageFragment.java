package com.example.dressapp1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dressapp1.model.Product;
import com.example.dressapp1.model.User;
import com.squareup.picasso.Picasso;

public class ProductPageFragment extends Fragment implements View.OnClickListener {
    View view;
    Product curProduct;
    User owner;
    ImageView productImg, userAvatar;
    Button callBtn, mapBtn;
    TextView priceText, sizeText;
    ProgressBar progressBar;
    ImageButton myProfileBtn, searchBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        curProduct = ProductPageFragmentArgs.fromBundle(getArguments()).getProduct();
        owner = new User();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_product_page, container, false);

        productImg = view.findViewById(R.id.product_preview);
        userAvatar = view.findViewById(R.id.user_profile);
        callBtn = view.findViewById(R.id.product_call);
        mapBtn = view.findViewById(R.id.product_map);
        priceText = view.findViewById(R.id.product_price);
        sizeText = view.findViewById(R.id.product_size);
        progressBar = view.findViewById(R.id.product_progress);
        myProfileBtn = view.findViewById(R.id.bottom_bar_profile);
        searchBtn = view.findViewById(R.id.bottom_bar_search);

        callBtn.setOnClickListener(this);
        mapBtn.setOnClickListener(this);
        myProfileBtn.setOnClickListener(this);
        searchBtn.setOnClickListener(this);

        priceText.setText(curProduct.getPrice());
        sizeText.setText(curProduct.getSize());

        if(curProduct.getImg() != null) {
            Picasso.get().load(curProduct.getImg()).into(productImg);
        }

        return view;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.product_call:
                call();
                break;
            case R.id.product_map:
                map();
                break;
            case R.id.bottom_bar_profile:
                Navigation.findNavController(view).navigate(ProductGridFragmentDirections.actionProductGridFragmentToMyProfileFragment());
                break;
            case R.id.bottom_bar_search:
                Navigation.findNavController(view).navigate(ProductGridFragmentDirections.actionProductGridFragmentToSelectGenderFragment());
                break;
        }
    }

    public void call() {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "0509619325", null)));
        Toast.makeText(getActivity(), "phone call", Toast.LENGTH_LONG).show();
    }

    public void map() {
        Log.d("MAP", "map clicked");
    }


}