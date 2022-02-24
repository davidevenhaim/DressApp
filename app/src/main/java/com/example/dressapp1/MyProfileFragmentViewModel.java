package com.example.dressapp1;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.dressapp1.model.Model;
import com.example.dressapp1.model.Product;
import com.example.dressapp1.model.User;

import java.util.List;

public class MyProfileFragmentViewModel extends ViewModel {
    LiveData<List<Product>> data;
    User user;
    public LiveData<List<Product>> getData() {
        return data;
    }

    public void setData(User user) {
        this.user = user;
        this.data = Model.instance.getUserProductsByUserId(user.getId());
    }
}
