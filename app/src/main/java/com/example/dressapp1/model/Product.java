package com.example.dressapp1.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.example.dressapp1.MainActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.util.Map;

public class Product {
    String size, price, gender, category;
    Uri img;
    Timestamp createdAt;

    final static String ID = "id";
    final static String CATEGORY = "category";
    final static String GENDER = "gender";
    final static String OWNER = "ownerRef";
    final static String PRICE = "price";
    final static String SIZE = "size";
    final static String TIME = "timestamp";

    public Product(){}

    public Product(String size, String price, String gender, String category ) {
        this.size = size;
        this.price = price;
        this.gender = gender;
        this.category = category;
    }

    public Product(String size, String price, String gender, String category, Timestamp ts) {
        this.size = size;
        this.price = price;
        this.gender = gender;
        this.category = category;
        this.createdAt = ts;
    }

    public String getSize() {
        return size;
    }

    public Uri getImg() {return img;}

    public String getPrice() {
        return price;
    }

    public String getGender() {
        return gender;
    }

    public String getCategory() {
        return category;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setImg(Uri img) {
        this.img = img;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    static Product fromJson(Map<String,Object> json){
        String size = json.get(SIZE).toString();
        String price = json.get(PRICE).toString();
        String gender = json.get(GENDER).toString();
        String category = json.get(CATEGORY).toString();
        Timestamp ts =  (Timestamp) json.get(TIME);

        Product product = new Product(size, price, gender, category, ts);

        return product;
    }

    static Long getLocalLastUpdated(){
        Long localLastUpdate = MainActivity.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE)
                .getLong("STUDENTS_LAST_UPDATE",0);
        return localLastUpdate;
    }

    static void setLocalLastUpdated(Long date){
        SharedPreferences.Editor editor = MainActivity.getContext()
                .getSharedPreferences("TAG", Context.MODE_PRIVATE).edit();
        editor.putLong("STUDENTS_LAST_UPDATE",date);
        editor.commit();
        Log.d("TAG", "new lud " + date);
    }

}
