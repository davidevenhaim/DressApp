package com.example.dressapp1.model.recycler;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dressapp1.R;
import com.example.dressapp1.model.Product;
import com.example.dressapp1.model.interfaces.OnItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
    OnItemClickListener listener;
    private List<Product> data;
    private Fragment fragment;

    public MyAdapter() {
    }

    public MyAdapter(List<Product> data,Fragment fragment) {
        this.data = data;
        this.fragment = fragment;
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public void setData(List<Product> data) {
        this.data = data;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = fragment.getLayoutInflater().inflate(R.layout.product_card_fragment, parent, false);
        MyViewHolder holder = new MyViewHolder(view, listener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Product p = data.get(position);
        holder.title.setText(p.getCategory());
        holder.price.setText(p.getPrice());

        if(p.getImg() != null) {
            Picasso.get().load(p.getImg()).into(holder.img);
        }
    }


    @Override
    public int getItemCount() {
        if(data == null)
            return 0;
        return data.size();
    }

}
