package com.example.dressapp1;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.dressapp1.model.LoadingState;
import com.example.dressapp1.model.Model;
import com.example.dressapp1.model.Product;
import com.example.dressapp1.model.interfaces.OnItemClickListener;
import com.example.dressapp1.model.recycler.MyAdapter;

import java.util.List;

public class ProductGridFragment extends Fragment implements View.OnClickListener {
    ProductListFragmentViewModel viewModel;
    View view;
    SwipeRefreshLayout swipeRefresh;
    MyAdapter adapter;
    ProgressBar progressBar;
    ImageButton addProdBtn, myProfileBtn, searchBtn;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(ProductListFragmentViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_product_grid, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        progressBar = view.findViewById(R.id.product_list_progress_bar);
        addProdBtn = view.findViewById(R.id.add_new_post_btn);
        swipeRefresh = view.findViewById(R.id.product_list_swipe_refresh);
        myProfileBtn = view.findViewById(R.id.bottom_bar_profile);
        searchBtn = view.findViewById(R.id.bottom_bar_search);

        progressBar.setVisibility(View.VISIBLE);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(true);
                Model.instance.reloadProductList();
                adapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
            }
        });

        int largePadding = getResources().getDimensionPixelSize(R.dimen.product_grid_spacing);
        int smallPadding = getResources().getDimensionPixelSize(R.dimen.product_grid_spacing_small);
        adapter = new MyAdapter();

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new ProductGridItemDecoration(largePadding, smallPadding));

        addProdBtn.setOnClickListener(this);
        myProfileBtn.setOnClickListener(this);
        searchBtn.setOnClickListener(this);

        viewModel.getData().observe(getViewLifecycleOwner(), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                adapter.setFragment(ProductGridFragment.this);
                adapter.setData(products);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }
        });

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                progressBar.setVisibility(View.VISIBLE);
                Product product = viewModel.getData().getValue().get(position);
                ProductGridFragmentDirections.ActionProductGridFragmentToProductPageFragment action =
                        ProductGridFragmentDirections.actionProductGridFragmentToProductPageFragment(product);
                Navigation.findNavController(v).navigate(action);
            }
        });

        swipeRefresh.setRefreshing(Model.instance.getLoadingState().getValue()== LoadingState.loading);
        Model.instance.getLoadingState().observe(getViewLifecycleOwner(), loadingState -> {
            swipeRefresh.setRefreshing(loadingState == LoadingState.loading);
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.add_new_post_btn:
                Navigation.findNavController(view).navigate(ProductGridFragmentDirections.actionProductGridFragmentToNewPostFragment());
                break;
            case R.id.bottom_bar_profile:
                Navigation.findNavController(view).navigate(ProductGridFragmentDirections.actionProductGridFragmentToMyProfileFragment());
                break;
            case R.id.bottom_bar_search:
                Navigation.findNavController(view).navigate(ProductGridFragmentDirections.actionProductGridFragmentToSelectGenderFragment());
                break;
            default:
                break;
        }
    }

}