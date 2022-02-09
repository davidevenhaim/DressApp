package com.example.dressapp1.model;

import androidx.room.TypeConverter;

import java.util.Arrays;
import java.util.List;

public class ProductListConverter {
    @TypeConverter
    public User storedStringsToProducts(String value) {
        List<String> prodcuts = Arrays.asList(value.split("\\s*,\\s*"));
        return new User();
    }

    @TypeConverter
    public String userProductsToStoredString(User cl) {
        String value = "";

//        for (String lang :cl.getProducts())
//            value += lang + ",";

        return value;
    }
}


