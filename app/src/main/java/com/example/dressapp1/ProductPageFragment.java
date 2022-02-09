package com.example.dressapp1;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dressapp1.model.DBModel;
import com.example.dressapp1.model.Product;
import com.example.dressapp1.model.User;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

public class ProductPageFragment extends Fragment {
    View view;
    String productId;
    String ownerId;
    Product curProduct;
    User owner;
    ImageView productImg, userAvatar;
    Button callBtn, mapBtn;
    TextView priceText, sizeText;
    ImageButton favoriteImgBtn;
    ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        curProduct = ProductPageFragmentArgs.fromBundle(getArguments()).getProduct();
        owner = new User();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_product_page, container, false);

        productImg = view.findViewById(R.id.product_preview);
        userAvatar = view.findViewById(R.id.user_profile);
        callBtn = view.findViewById(R.id.product_call);
        mapBtn = view.findViewById(R.id.product_map);
        priceText = view.findViewById(R.id.product_price);
        sizeText = view.findViewById(R.id.product_size);
        favoriteImgBtn = view.findViewById(R.id.favorite_btn);
        progressBar = view.findViewById(R.id.product_progress);

        progressBar.setVisibility(View.VISIBLE);

        priceText.setText(curProduct.getPrice());
        sizeText.setText(curProduct.getSize());
        String url = curProduct.getImg() + "";
        if(!url.isEmpty()) {
            Picasso.get().load(url).into(productImg);
        }
        Log.d("PRODUCT", curProduct.getSize());

        progressBar.setVisibility(View.INVISIBLE);

        return view;
    }
}