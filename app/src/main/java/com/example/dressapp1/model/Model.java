package com.example.dressapp1.model;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.dressapp1.MyApplication;
import com.example.dressapp1.model.interfaces.DeleteProductListener;
import com.example.dressapp1.model.interfaces.EditProductListener;
import com.example.dressapp1.model.interfaces.GetUserById;
import com.example.dressapp1.model.interfaces.UploadProductListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;



public class Model {
    public static final Model instance = new Model();
    DBModel fbModel = new DBModel();
    MutableLiveData<LoadingState> loadingState= new MutableLiveData<LoadingState>();
    MutableLiveData<List<Product>> productListLtd = new MutableLiveData<List<Product>>();

    private Model(){
        loadingState.setValue(LoadingState.loaded);
        reloadProductList();
    }

    public LiveData<LoadingState> getLoadingState() {
        return loadingState;
    }

    public LiveData<List<Product>> getAll() {
        return productListLtd;
    }

    public LiveData<List<Product>> getAllByGender(String gender) {
        return AppLocalDB.db.productDao().getProductByGender(gender);
    }

    public void reloadProductList() {
        Long localLastUpdate = Product.getLocalLastUpdated();
        loadingState.setValue(LoadingState.loading);

        fbModel.getAllProducts(localLastUpdate,(list)-> {
            if(list != null) {
                MyApplication.executorService.execute(()->{
                    Long lLastUpdate = new Long(0);

                    for(Product product : list) {
                            if(!product.isDeleted()) {
                                AppLocalDB.db.productDao().insertAll(product);
                            } else {
                                Log.d("!", product.getPrice() + " Is deleted");
                                AppLocalDB.db.productDao().delete(product);
                            } if (product.getLastUpdated() > lLastUpdate){
                                lLastUpdate = product.getLastUpdated();
                            }
                    }
                    Product.setLocalLastUpdated(new Long(0));
                    List<Product> allProducts = AppLocalDB.db.productDao().getAll();
                    for (Product p: allProducts){
                        if(p.isDeleted()){
                            AppLocalDB.db.productDao().delete(p);
                        }
                    }
                    productListLtd.postValue(allProducts);

                    loadingState.postValue(LoadingState.loaded);
                });
            }
        });
    }

    public void getUserById(String userId, GetUserById listener) {
        fbModel.getUserById(userId,listener);
    }

    public LiveData<List<Product>> getUserProductsByUserId(String userId) {
        return AppLocalDB.db.productDao().getProductById(userId);
    }

    public void DeletePost(Product product, DeleteProductListener listener) {
        fbModel.deleteProduct(product, isSuccess -> {
            product.setDeleted(true);
            reloadProductList();
            listener.onComplete(true);
        });
    }

    public void addPost(Product product, Bitmap bitmap, UploadProductListener listener){
        fbModel.uploadProduct(product, bitmap, (task, product1, userId) -> {
        reloadProductList();
        listener.onComplete(task, product1, userId);
        });
    }

    public void editPost(Product product, Bitmap bitmap, EditProductListener listener) {
        fbModel.editProduct(product, bitmap, product1 -> listener.onComplete(product1));
    }
}
