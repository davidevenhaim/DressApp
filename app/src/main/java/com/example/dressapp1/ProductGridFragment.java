package com.example.dressapp1;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.example.dressapp1.model.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProductGridFragment extends Fragment {
    View view;
    SwipeRefreshLayout swipeRefresh;
    ProductListFragmentViewModel viewModel;
    MyAdapter adapter;

    private List<Product> productList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(ProductListFragmentViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_product_grid, container, false);
        productList = new ArrayList<>();
        for(int i = 5; i < 20; i ++) {
            int price = 20 * i;
            Product prod = new Product("M", new String(price + ""), "Man", "Suit", new Long(12));
            productList.add(prod);
        }
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Product product = viewModel.getData().getValue().get(i);
                Log.d("prod",product.getPrice());
            }
        });

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
        ProductCardRecyclerViewAdapter adapter = new ProductCardRecyclerViewAdapter(
                productList);


        int largePadding = getResources().getDimensionPixelSize(R.dimen.product_grid_spacing);
        int smallPadding = getResources().getDimensionPixelSize(R.dimen.product_grid_spacing_small);
        recyclerView.addItemDecoration(new ProductGridItemDecoration(largePadding, smallPadding));

        return view;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        NetworkImageView img;
        TextView title, price;

        public MyViewHolder(@NonNull View itemView, AdapterView.OnItemClickListener listener) {
            super(itemView);
            img = itemView.findViewById(R.id.grid_product_image);
            title = itemView.findViewById(R.id.grid_product_title);
            price = itemView.findViewById(R.id.grid_product_price);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("E", "123456");
                }
            });
        }

        public void bind(Product product){
            title.setText(product.getCategory());
            price.setText(product.getPrice());
            String url = product.getImg().toString();
            if (url != null){
                Picasso.get()
                        .load(url)
                        .placeholder(R.drawable.logo)
                        .into(img);
            }
        }
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder>{

        AdapterView.OnItemClickListener listener;
        public void setOnItemClickListener(AdapterView.OnItemClickListener listener){
            this.listener = listener;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.product_card_fragment,parent,false);
            MyViewHolder holder = new MyViewHolder(view, listener);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Product product = viewModel.getData().getValue().get(position);
            holder.bind(product);
        }

        @Override
        public int getItemCount() {
            if (viewModel.getData().getValue() == null) return 0;
            return viewModel.getData().getValue().size();
        }
    }

}