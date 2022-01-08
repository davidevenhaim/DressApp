package com.example.dressapp1.model;

import android.widget.ArrayAdapter;

public class User {
    String email, phone, address, fullName,city;
    String[] products;

    public User(){}

    public User(String address, String city, String email, String fullName, String phone ){
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.fullName = fullName;
        this.city = city;
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
}
