package com.example.dressapp1.model;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.dressapp1.MyApplication;

import java.util.List;

public class Model {
    public static final Model instance = new Model();
    DBModel fbModel = new DBModel();

    private Model(){
        reloadStudentsList();
    }

    public interface GetAllProductsListener {
        void onComplete(List<Product> data);
    }

    MutableLiveData<List<Product>> productListLtd = new MutableLiveData<List<Product>>();

    private void reloadStudentsList() {
        Long localLastUpdate = Product.getLocalLastUpdated();
        Log.d("TAG","localLastUpdate: " + localLastUpdate);

        fbModel.getAllProducts(localLastUpdate,(list)->{
            MyApplication.executorService.execute(()->{
                Long lLastUpdate = new Long(0);
                Log.d("TAG", "FB returned " + list.size());
                for(Product product : list){
//                    AppLocalDB.db.studentDao().insertAll(product);
                    if (product.getLastUpdated() > lLastUpdate){
                        lLastUpdate = product.getLastUpdated();
                    }
                }
                Product.setLocalLastUpdated(lLastUpdate);

                //5. return all records to the caller
//                List<Product> stList = AppLocalDB.db.studentDao().getAll();
//                productListLtd.postValue(stList);
            });
        });
    }
}
