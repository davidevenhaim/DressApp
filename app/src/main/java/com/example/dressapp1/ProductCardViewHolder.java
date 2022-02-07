package com.example.dressapp1;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;

public class ProductCardViewHolder extends RecyclerView.ViewHolder {
    TextView priceText, titleText;
    NetworkImageView imageView;

    public ProductCardViewHolder(@NonNull View itemView) {
        super(itemView);
        priceText = itemView.findViewById(R.id.grid_product_price);
        titleText = itemView.findViewById(R.id.grid_product_title);
        imageView = itemView.findViewById(R.id.grid_product_image);


    }
}
