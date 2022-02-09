package com.example.dressapp1;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.example.dressapp1.model.LoadingState;
import com.example.dressapp1.model.Model;
import com.example.dressapp1.model.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProductGridFragment extends Fragment {
    ProductListFragmentViewModel viewModel;
    View view;
    SwipeRefreshLayout swipeRefresh;
    MyAdapter adapter;
    ProgressBar progressBar;

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
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        progressBar = view.findViewById(R.id.product_list_progress_bar);
        swipeRefresh = view.findViewById(R.id.product_list_swipe_refresh);


        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(true);
                Model.instance.reloadProductList();
                adapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);

        viewModel.getData().observe(getViewLifecycleOwner(), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                adapter.setFragment(ProductGridFragment.this);
                adapter.setData(products);
                adapter.notifyDataSetChanged();
            }
        });

        progressBar.setVisibility(View.GONE);

        adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                progressBar.setVisibility(View.VISIBLE);
                Product product = viewModel.getData().getValue().get(i);
                ProductGridFragmentDirections.ActionProductGridFragmentToProductPageFragment action =
                        ProductGridFragmentDirections.actionProductGridFragmentToProductPageFragment(product);
                Navigation.findNavController(view).navigate(action);
                Log.d("prod",product.getPrice());
            }
        });

        swipeRefresh.setRefreshing(Model.instance.getLoadingState().getValue()== LoadingState.loading);
        Model.instance.getLoadingState().observe(getViewLifecycleOwner(), loadingState -> {
            swipeRefresh.setRefreshing(loadingState == LoadingState.loading);
        } );

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
        private List<Product> data;
        private Fragment fragment;

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

        public void setData(List<Product> data) {
            this.data=data;
        }

        public void setFragment(Fragment fragment) {
            this.fragment = fragment;
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