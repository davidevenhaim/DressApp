package com.example.dressapp1;

import androidx.lifecycle.LiveData;

import com.example.dressapp1.model.DBModel;
import com.example.dressapp1.model.Product;

import java.util.List;

public class ProductListFragmentViewModel {

    LiveData<List<Product>> data;

//    ProductListFragmentViewModel() {
//        DBModel.dbInstance.getAllProducts(new DBModel.GetAllProductsListener() {
//            @Override
//            public void onComplete() {
////                data
//            }
//        });
//    }

    public LiveData<List<Product>> getData() {
        return data;
    }


}
