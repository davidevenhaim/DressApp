package com.example.dressapp1;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dressapp1.model.LoadingState;
import com.example.dressapp1.model.Model;
import com.example.dressapp1.model.Product;
import com.example.dressapp1.model.User;
import com.example.dressapp1.model.helpers.Constants;
import com.example.dressapp1.model.interfaces.GetUserById;
import com.example.dressapp1.model.interfaces.OnItemClickListener;
import com.example.dressapp1.model.recycler.MyAdapter;

import java.util.List;

public class MyProfileFragment extends Fragment implements View.OnClickListener {
    Button logoutBtn;
    ImageButton addProdBtn, searchBtn;
    MyAdapter adapter;
    MyProfileFragmentViewModel viewModel;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefresh;
    TextView name, address, city, phone;
    User curUser;
    View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(MyProfileFragmentViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_profile, container, false);
        SharedPreferences sp = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        progressBar = view.findViewById(R.id.my_profile_progress_bar);
        name = view.findViewById(R.id.my_profile_name);
        address = view.findViewById(R.id.my_profile_address);
        city = view.findViewById(R.id.my_profile_city);
        phone = view.findViewById(R.id.my_profile_phone);
        logoutBtn = view.findViewById(R.id.logout_btn);
        swipeRefresh = view.findViewById(R.id.my_profile_refresh);
        recyclerView = view.findViewById(R.id.my_profile_recycler_view);

        searchBtn = view.findViewById(R.id.bottom_bar_search);
        addProdBtn = view.findViewById(R.id.add_new_post_btn);

        searchBtn.setOnClickListener(this);
        addProdBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);

        curUser = User.userFromSharedPreference(sp);
        viewModel.setData(curUser);

        String spUserId = sp.getString(Constants.CUR_USER + "_id", null);

        if(curUser.getAddress() == null || spUserId != curUser.getId() ) {
            progressBar.setVisibility(View.VISIBLE);
            Model.instance.getUserById(spUserId, new GetUserById() {
                @Override
                public void onComplete(User user) {
                    User.userToSharedPreference(user, editor);
                    name.setText("Hello, " + user.getFullName());
                    address.setText(user.getAddress());
                    city.setText(user.getCity());
                    phone.setText(user.getPhone());
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            name.setText("Hello, " + curUser.getFullName());
            address.setText(curUser.getAddress());
            city.setText(curUser.getCity());
            phone.setText(curUser.getPhone());
        }

        int largePadding = getResources().getDimensionPixelSize(R.dimen.product_grid_spacing);
        int smallPadding = getResources().getDimensionPixelSize(R.dimen.product_grid_spacing_small);
        adapter = new MyAdapter();

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new ProductGridItemDecoration(largePadding, smallPadding));

        viewModel.getData().observe(getViewLifecycleOwner(), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                adapter.setFragment(MyProfileFragment.this);
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
                MyProfileFragmentDirections.ActionMyProfileFragmentToNewPostFragment action =
                        MyProfileFragmentDirections.actionMyProfileFragmentToNewPostFragment(product);
                Navigation.findNavController(v).navigate(action);
            }
        });

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(true);
                Model.instance.reloadProductList();
                adapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
            }
        });

        swipeRefresh.setRefreshing(Model.instance.getLoadingState().getValue() == LoadingState.loading);

        Model.instance.getLoadingState().observe(getViewLifecycleOwner(), loadingState -> {
            swipeRefresh.setRefreshing(loadingState == LoadingState.loading);
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.bottom_bar_search:
                Navigation.findNavController(view).navigate(MyProfileFragmentDirections.actionMyProfileFragmentToSelectGenderFragment());
                break;
            case R.id.add_new_post_btn:
                Navigation.findNavController(view).navigate(MyProfileFragmentDirections.actionMyProfileFragmentToNewPostFragment(null));
                break;
            case R.id.logout_btn:
                logout();
                break;
            default:
                break;
        }
    }

    private void logout() {
        SharedPreferences sp = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);
        SharedPreferences.Editor Ed=sp.edit();
        User.logoutUserFromSP(Ed);
        Ed.commit();

        Navigation.findNavController(view).navigate(MyProfileFragmentDirections.actionMyProfileFragmentToLogInFragment());
    }
}