package com.example.dressapp1.model;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

public class DBModel {
    public static final DBModel dbInstance = new DBModel();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface LoginUserListener{
        void onComplete(FirebaseUser user, Task<AuthResult> task);
    }
    public void loginUser(String email, String password, LoginUserListener listener ) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    listener.onComplete(user, task);
                });
    }

    public interface SignupUserListener{
        void onComplete(FirebaseUser user, Task task);
    }

    public void registerUser(User user, String password, SignupUserListener listener) {
        mAuth.createUserWithEmailAndPassword(user.getEmail(), password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DocumentReference documentReference = db.collection("users").document(userId);
                        Map<String, Object> dbUser = new HashMap<>();
                        dbUser.put("email", user.getEmail());
                        dbUser.put("phone", user.getPhone());
                        dbUser.put("address", user.getAddress());
                        dbUser.put("fname", user.getFullName());
                        dbUser.put("city", user.getCity());
                        dbUser.put("products", user.getProducts());
                        dbUser.put("timestamp", FieldValue.serverTimestamp());

                        documentReference.set(dbUser).addOnCompleteListener(task1 -> {
                            FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
                            listener.onComplete(fbUser, task1);
                        });
                    }  else {
                        Log.d("ERR", "Error creating account");
                    }
            });
    }

    public interface UploadProductListener{
        void onComplete(Task task, String productId, String userId);
    }

    public void uploadProduct(Product product, Bitmap bitmap, UploadProductListener listener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Map<String, Object> dbProduct = new HashMap<>();
        dbProduct.put("category", product.getCategory());
        dbProduct.put("size", product.getSize());
        dbProduct.put("gender", product.getGender());
        dbProduct.put("price", product.getPrice());
        dbProduct.put("timestamp", FieldValue.serverTimestamp());

        DocumentReference productDocRef = db.collection("products").document();

        productDocRef.set(dbProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                DocumentReference userRef = db.collection("users").document(user.getUid());
                userRef.update("products", FieldValue.arrayUnion(productDocRef)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void unused) {
                        FirebaseStorage storage = FirebaseStorage.getInstance();

                        StorageReference storageRef = storage.getReference();
                        StorageReference imageRef = storageRef.child("images/" + productDocRef.getId() + ".jpg");

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();

                        UploadTask uploadTask = imageRef.putBytes(data);
                        uploadTask.addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()
                                        .addOnSuccessListener(uri -> {
                                            Uri downloadUrl = uri;
                                            listener.onComplete(task, productDocRef.getId(), user.getUid());
                                        }));
                    }
                });

            }
        });

    }

    public interface GetUserByIdListener{
        void onComplete(Task task, User user);
    }

    public void getUserById(String userId, GetUserByIdListener listener) {
        DocumentReference userDocRef = db.collection("users").document(userId);
        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot snap = task.getResult();
                User user = new User();
                user.setAddress(snap.get("address").toString());
                user.setCity(snap.get("city").toString());
                user.setEmail(snap.get("email").toString());
                user.setFullName(snap.get("fname").toString());
                user.setPhone(snap.get("phone").toString());
                listener.onComplete(task, user);
            }
        });
    }

    public interface GetProductListener{
        void onComplete(Task task,Product product);
    }

    public void getProduct(String productId, GetProductListener listener ) {
        DocumentReference productDocRef = db.collection("products").document(productId);
        productDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                DocumentSnapshot snap = task.getResult();

                storageRef.child("images/" + productId + ".jpg").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Product product = new Product();
                        product.setCategory(snap.get("category").toString());
                        product.setGender(snap.get("gender").toString());
                        product.setPrice(snap.get("price").toString());
                        product.setSize(snap.get("size").toString());
                        product.setImg(task.getResult());
                        listener.onComplete(task, product);
                    }
                });
            }
        });
    }

}
