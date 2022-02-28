package com.example.dressapp1.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.dressapp1.MainActivity;
import com.example.dressapp1.MyApplication;
import com.example.dressapp1.model.helpers.Constants;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

@Entity
public class Product implements Parcelable {
    @PrimaryKey
    @NonNull
    private String id;
    private String size, price, gender, category, img, ownerId, ownerPhone;
    private Long lastUpdated;
    private boolean isDeleted;
    private double longtitude;
    private double lantitude;

    public Product(){}

    public Product(String size, String price, String gender, String category, Long lastU, String img, double longtitude, double lantitude, String ownerId) {
        this.size = size;
        this.price = price;
        this.gender = gender;
        this.category = category;
        this.lastUpdated = lastU;
        this.img = img;
        this.longtitude = longtitude;
        this.lantitude = lantitude;
        this.ownerId = ownerId;
    }


    public String getSize() {
        return size;
    }

    public String getImg() {return img;}

    public String getId() {
        return id;
    }

    public String getPrice() {
        return price;
    }

    public String getGender() {
        return gender;
    }

    public String getCategory() {
        return category;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public Double getLongtitude() {return this.longtitude;}

    public Double getLantitude() {return this.lantitude;}

    public boolean isDeleted() {
        return isDeleted;
    }

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public void setOwnerId(String ownerId ) {
        this.ownerId = ownerId;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setId(String id) {
        this.id = id;
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

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public void setLantitude(double lantitude) {
        this.lantitude = lantitude;
    }

    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    static Product fromJson(Map<String,Object> json) {
        try {
            String size = json.get(Constants.SIZE) + "";
            String price = json.get(Constants.PRICE) + "";
            String gender = json.get(Constants.GENDER) + "";
            String category = json.get(Constants.CATEGORY) + "";
            String img = json.get(Constants.IMG) + "";
            String ownerId = json.get(Constants.OWNER) + "";
            Boolean isDeleted = (Boolean) json.get("is_deleted");

            Double longtitude = (double) json.get(Constants.LANG);
            Double lantitude = (double) json.get(Constants.LANT);

            Timestamp ts =  (Timestamp) json.get(Constants.TIME);

            Product product = new Product(size, price, gender, category, new Long(ts.getSeconds()), img, longtitude, lantitude, ownerId);
            product.setDeleted(isDeleted);
            return product;
        }catch (Error e) {
        }
        return new Product();
    }

    public Map<String, Object> toJson() {
        Map<String, Object> dbProduct = new HashMap<>();

        dbProduct.put(Constants.IMG, img);
        dbProduct.put(Constants.CATEGORY, category);
        dbProduct.put(Constants.SIZE, size);
        dbProduct.put(Constants.GENDER, gender);
        dbProduct.put(Constants.PRICE, price);
        dbProduct.put(Constants.TIME, FieldValue.serverTimestamp());
        dbProduct.put(Constants.OWNER, ownerId);
        dbProduct.put(Constants.LANG, longtitude);
        dbProduct.put(Constants.LANT, lantitude);
        dbProduct.put(Constants.IS_DELETED, false);

        return dbProduct;
    }

    static Long getLocalLastUpdated(){
        Long localLastUpdate = MyApplication.getContext().getSharedPreferences(Constants.TAG, Context.MODE_PRIVATE)
                .getLong(Constants.PRODUCT_LAST_UPDATE,0);
        return localLastUpdate;
    }

    static void setLocalLastUpdated(Long date){
        SharedPreferences.Editor editor = MyApplication.getContext()
                .getSharedPreferences(Constants.TAG, Context.MODE_PRIVATE).edit();
        editor.putLong(Constants.PRODUCT_LAST_UPDATE,date);
        editor.commit();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(size);
        parcel.writeString(price);
        parcel.writeString(gender);
        parcel.writeString(category);
        parcel.writeString(img);
        parcel.writeDouble(lantitude);
        parcel.writeDouble(longtitude);
        parcel.writeString(ownerId);

        parcel.writeByte((byte) (isDeleted ? 1 : 0));

        if (lastUpdated == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(lastUpdated);
        }
    }

    protected Product(Parcel in) {
        id = in.readString();
        size = in.readString();
        price = in.readString();
        gender = in.readString();
        category = in.readString();
        img = in.readString();
        lantitude = in.readDouble();
        longtitude = in.readDouble();
        ownerId = in.readString();

        isDeleted = in.readByte() != 0;

        if (in.readByte() == 0) {
            lastUpdated = null;
        } else {
            lastUpdated = in.readLong();
        }
    }

}
