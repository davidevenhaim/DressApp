package com.example.dressapp1.model.recycler;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dressapp1.R;
import com.example.dressapp1.model.interfaces.OnItemClickListener;

public class MyViewHolder extends RecyclerView.ViewHolder {
    ImageView img;
    TextView title, price;

    public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
        super(itemView);
        img = itemView.findViewById(R.id.grid_product_image);
        title = itemView.findViewById(R.id.grid_product_title);
        price = itemView.findViewById(R.id.grid_product_price);

        itemView.setOnClickListener(v -> {
            int position = getAdapterPosition();
            if(listener != null) {
                listener.onItemClick(position, v);
            }
        });
    }
}
