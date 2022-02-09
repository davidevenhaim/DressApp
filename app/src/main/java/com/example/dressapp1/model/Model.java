package com.example.dressapp1.model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.dressapp1.MyApplication;

import java.util.List;



public class Model {
    public static final Model instance = new Model();
    DBModel fbModel = new DBModel();

    private Model(){
        loadingState.setValue(LoadingState.loaded);
        reloadProductList();
    }

    public interface GetAllProductsListener {
        void onComplete(List<Product> data);
    }

    MutableLiveData<LoadingState> loadingState= new MutableLiveData<LoadingState>();
    MutableLiveData<List<Product>> productListLtd = new MutableLiveData<List<Product>>();

    public LiveData<LoadingState> getLoadingState() {
        return loadingState;
    }

    public LiveData<List<Product>> getAll() {
        return productListLtd;
    }

    public void reloadProductList() {
        Long localLastUpdate = Product.getLocalLastUpdated();
        Log.d("TAG","localLastUpdate: " + localLastUpdate);

        fbModel.getAllProducts(localLastUpdate,(list)-> {
            if(list != null) {
            MyApplication.executorService.execute(()->{
                Long lLastUpdate = new Long(0);

                for(Product product : list){
                    AppLocalDB.db.productDao().insertAll(product);
                    if (product.getLastUpdated() > lLastUpdate){
                        lLastUpdate = product.getLastUpdated();
                    }
                }
                Product.setLocalLastUpdated(lLastUpdate);
                List<Product> allProducts = AppLocalDB.db.productDao().getAll();

                for(Product pro : allProducts) {
                    Log.d("1", pro.getId());
                }
                productListLtd.postValue(allProducts);
                //5. return all records to the caller
                
                loadingState.postValue(LoadingState.loaded);
            });

            }
        });
    }
}
