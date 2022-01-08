package com.example.dressapp1.model;

import android.widget.ArrayAdapter;

import com.google.firebase.Timestamp;

import java.util.Map;

public class User {
    String email, phone, address, fullName, city, id;
    String[] products;
    Timestamp createdAt;

    final static String ID = "id";
    final static String ADDRESS = "address";
    final static String CITY = "city";
    final static String EMAIL = "email";
    final static String FNAME = "fname";
    final static String PHONE = "phone";
    final static String PRODUCTS = "products";
    final static String TIME = "timestamp";


    public User(){}

    public User(String address, String city, String email, String fullName, String phone ){
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.fullName = fullName;
        this.city = city;
    }

    public User(String address, String city, String email, String fullName, String phone, String id, Timestamp ts ){
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.fullName = fullName;
        this.city = city;
        this.id = id;
        this.createdAt = ts;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getEmail() {
        return email;
    }

    public String[] getProducts() {
        return products;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    static User fromJson(Map<String,Object> json){
        String id = json.get(ID).toString();
        String fname =  json.get(FNAME).toString();
        String address =  json.get(ADDRESS).toString();
        String city =  json.get(CITY).toString();
        String email =  json.get(EMAIL).toString();
        String phone =  json.get(PHONE).toString();
        String products =  json.get(PRODUCTS).toString();
        Timestamp ts = (Timestamp) json.get(TIME);

        User user = new User(address, city, email, fname, phone, id, ts);

        return user;
    }

}

