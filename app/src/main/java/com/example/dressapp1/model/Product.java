package com.example.dressapp1.model;

import android.net.Uri;

public class Product {
    String size, price, gender, category;
    Uri img;


    public Product(){}

    public Product(String size, String price, String gender, String category ) {
        this.size = size;
        this.price = price;
        this.gender = gender;
        this.category = category;

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
}
