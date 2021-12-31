package com.example.dressapp1.model;

public class Product {
    String img, size, price, uid;

    public Product(){}

    public Product(String size, String uid, String img, String price, String phone ){
        this.img = img;
        this.size = size;
        this.price = price;
        this.uid = uid;
    }

    public String getSize() {
        return size;
    }

    public String getUid() {
        return uid;
    }

    public String getImg() {
        return img;
    }

    public String getPrice() {
        return price;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
