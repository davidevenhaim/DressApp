package com.example.dressapp1.model;

import android.content.Context;
import android.content.SharedPreferences;
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

    public String getId() {
        return id;
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
        String fname =  json.get(Constants.FNAME).toString();
        String address =  json.get(Constants.ADDRESS).toString();
        String city =  json.get(Constants.CITY).toString();
        String email =  json.get(Constants.EMAIL).toString();
        String phone =  json.get(Constants.PHONE).toString();

        User user = new User(address, city, email, fname, phone);

        return user;
    }

    public static void userToSharedPreference(User user, SharedPreferences.Editor editor) {
        editor.putString(Constants.CUR_USER + "_fname", user.getFullName());
        editor.putString(Constants.CUR_USER + "_address", user.getAddress());
        editor.putString(Constants.CUR_USER + "_email", user.getEmail());
        editor.putString(Constants.CUR_USER + "_city", user.getCity());
        editor.putString(Constants.CUR_USER + "_phone", user.getPhone());
    }

    public static User userFromSharedPreference(SharedPreferences sp) {
        String fname = sp.getString(Constants.CUR_USER + "_fname", null);
        String address = sp.getString(Constants.CUR_USER + "_address", null);
        String email = sp.getString(Constants.CUR_USER + "_email", null);
        String city = sp.getString(Constants.CUR_USER + "_city", null);
        String phone = sp.getString(Constants.CUR_USER + "_phone", null);
        String id = sp.getString(Constants.CUR_USER + "_id", null);

        return new User(address, city, email, fname, phone, id);
    }

    public static void logoutUserFromSP(SharedPreferences.Editor editor) {
        editor.remove(Constants.CUR_USER + "_fname");
        editor.remove(Constants.CUR_USER + "_address");
        editor.remove(Constants.CUR_USER + "_email");
        editor.remove(Constants.CUR_USER + "_city");
        editor.remove(Constants.CUR_USER + "_phone");
        editor.remove(Constants.CUR_USER + "_id");
    }

}

