package com.example.dressapp1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.dressapp1.model.Model;
import com.example.dressapp1.model.Product;
import com.example.dressapp1.model.User;
import com.example.dressapp1.model.helpers.Constants;
import com.example.dressapp1.model.interfaces.GetUserById;
import com.squareup.picasso.Picasso;

public class ProductPageFragment extends Fragment implements View.OnClickListener {
    View view;
    Product curProduct;
    ImageView productImg, userAvatar;
    Button callBtn, mapBtn, editProduct;
    TextView priceText, sizeText;
    ProgressBar progressBar;
    ImageButton myProfileBtn, searchBtn,addProdBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        curProduct = ProductPageFragmentArgs.fromBundle(getArguments()).getProduct();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_product_page, container, false);

        productImg = view.findViewById(R.id.product_details_preview);
        userAvatar = view.findViewById(R.id.user_details_profile);
        callBtn = view.findViewById(R.id.product_details_call);
        mapBtn = view.findViewById(R.id.product_details_map);
        priceText = view.findViewById(R.id.product_price);
        sizeText = view.findViewById(R.id.product_size);
        progressBar = view.findViewById(R.id.product_progress);
        myProfileBtn = view.findViewById(R.id.bottom_bar_profile);
        searchBtn = view.findViewById(R.id.bottom_bar_search);
        addProdBtn = view.findViewById(R.id.add_new_post_btn);
        editProduct = view.findViewById(R.id.edit_product);

        callBtn.setOnClickListener(this);
        mapBtn.setOnClickListener(this);
        myProfileBtn.setOnClickListener(this);
        searchBtn.setOnClickListener(this);
        editProduct.setOnClickListener(this);
        addProdBtn.setOnClickListener(this);

        Model.instance.getUserById(curProduct.getOwnerId(), new GetUserById() {
            @Override
            public void onComplete(User user) {
                if(user != null) {
                    curProduct.setOwnerPhone(user.getPhone());
                }
            }
        });

        if(isProductOwner()) {
            editProduct.setVisibility(View.VISIBLE);
        }

        priceText.setText(curProduct.getPrice() + "$");
        sizeText.setText(curProduct.getSize());

        if(curProduct.getImg() != null) {
            Picasso.get().load(curProduct.getImg()).into(productImg);
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.product_details_call:
                call();
                break;
            case R.id.product_details_map:
                Navigation.findNavController(view).navigate(ProductPageFragmentDirections.actionProductPageFragmentToMapFragment(curProduct));
                break;
            case R.id.bottom_bar_profile:
                Navigation.findNavController(view).navigate(ProductPageFragmentDirections.actionProductPageFragmentToMyProfileFragment());
                break;
            case R.id.bottom_bar_search:
                Navigation.findNavController(view).navigate(ProductPageFragmentDirections.actionProductPageFragmentToSelectGenderFragment());
                break;
            case R.id.edit_product:
                ProductPageFragmentDirections.ActionProductPageFragmentToNewPostFragment action =
                        ProductPageFragmentDirections.actionProductPageFragmentToNewPostFragment(curProduct);
                Navigation.findNavController(view).navigate(action);
                break;
            case R.id.add_new_post_btn:
                Navigation.findNavController(view).navigate(ProductPageFragmentDirections.actionProductPageFragmentToNewPostFragment(null));
                break;
            default:
                break;
        }
    }

    public void call() {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", curProduct.getOwnerPhone() + "", null)));
        Toast.makeText(getActivity(), "phone call", Toast.LENGTH_LONG).show();
    }

    private boolean isProductOwner() {
        SharedPreferences sp = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);
        String currentUserId = sp.getString(Constants.CUR_USER + "_id", null);

        if(curProduct.getOwnerId().trim().equals(currentUserId.trim())) {
            return true;
        }
        return false;
    }

}