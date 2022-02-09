package com.example.dressapp1.model;

import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.dressapp1.model.helpers.Constants;
import com.google.firebase.Timestamp;

import java.util.Map;

@Entity
public class User {
    @PrimaryKey
    @NonNull
    String id;

    String email, phone, address, fullName, city;
//    String[] products;
//    Timestamp createdAt;

    public User(){}

    public User(String address, String city, String email, String fullName, String phone ){
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.fullName = fullName;
        this.city = city;
    }

    public User(String address, String city, String email, String fullName, String phone, String id ){
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.fullName = fullName;
        this.city = city;
        this.id = id;
//        this.createdAt = ts;
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

//    public String[] getProducts() {
//        return products;
//    }

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
        String id = json.get(Constants.ID).toString();
        String fname =  json.get(Constants.FNAME).toString();
        String address =  json.get(Constants.ADDRESS).toString();
        String city =  json.get(Constants.CITY).toString();
        String email =  json.get(Constants.EMAIL).toString();
        String phone =  json.get(Constants.PHONE).toString();
        String products =  json.get(Constants.PRODUCTS).toString();
//        Timestamp ts = (Timestamp) json.get(TIME);

        User user = new User(address, city, email, fname, phone, id);

        return user;
    }

}

