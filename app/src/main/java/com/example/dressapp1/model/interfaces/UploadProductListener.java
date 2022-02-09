package com.example.dressapp1.model.interfaces;

import com.example.dressapp1.model.Product;
import com.google.android.gms.tasks.Task;

public interface UploadProductListener {
    void onComplete(Task task, Product product, String userId);
}
