package com.example.dressapp1;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

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
        productId = ProductPageFragmentArgs.fromBundle(getArguments()).getProductId();
        ownerId = ProductPageFragmentArgs.fromBundle(getArguments()).getOwnerId();
        curProduct = new Product();
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

        DBModel.dbInstance.getUserById(ownerId, new DBModel.GetUserByIdListener() {
            @Override
            public void onComplete(Task task, User user) {
                if(task.isSuccessful()) {
                    owner = user;
                } else {
                    // set error.
                }
            }
        });

        DBModel.dbInstance.getProduct(productId, new DBModel.GetProductListener() {
            @Override
            public void onComplete(Task task, Product product) {
                if(task.isSuccessful()) {
                    curProduct = product;
                    progressBar.setVisibility(View.INVISIBLE);
                    sizeText.setText(curProduct.getSize());
                    priceText.setText(curProduct.getPrice());
                    productImg.setImageURI(curProduct.getImg());
                }else {
                    // set error.
                }
            }
        });


        return view;
    }
}