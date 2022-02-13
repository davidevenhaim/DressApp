package com.example.dressapp1.model;

import android.graphics.Bitmap;
import android.net.Uri;
import android.nfc.Tag;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.dressapp1.model.helpers.Constants;
import com.example.dressapp1.model.interfaces.UploadImageListener;
import com.example.dressapp1.model.interfaces.UploadProductListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
                        dbUser.put(Constants.EMAIL, user.getEmail());
                        dbUser.put(Constants.PHONE, user.getPhone());
                        dbUser.put(Constants.ADDRESS , user.getAddress());
                        dbUser.put(Constants.FNAME, user.getFullName());
                        dbUser.put(Constants.CITY , user.getCity());
                        dbUser.put(Constants.TIME , FieldValue.serverTimestamp());
//                        dbUser.put("products", user.getProducts());

                        documentReference.set(dbUser).addOnCompleteListener(task1 -> {
                            FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
                            listener.onComplete(fbUser, task1);
                        });
                    }  else {
                        Log.d("ERR", "Error creating account");
                    }
            });
    }

    public interface GetProductListener{
        void onComplete(Product product);
    }

    public void getProduct(String productId, GetProductListener listener ) {
        DocumentReference productDocRef = db.collection(Constants.PRODUCTS).document(productId);
        productDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                DocumentSnapshot document = task.getResult();
                storageRef.child("images/" + productId + ".jpg").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(document.exists()) {
                            Product product = Product.fromJson(document.getData());
                            Log.d("product", product.getCategory() + product.getGender() + product.getPrice());
                            product.setImg(task.getResult().toString());
                            listener.onComplete(product);
                        }else {
                            listener.onComplete(null);
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onComplete(null);
            }
        });
    }

    public interface GetAllProductsListener{
        void onComplete(List<Product> productsList);
    }


    public void getAllProducts(Long since,GetAllProductsListener listener) {
        Log.d("TIME", since.toString());
        db.collection(Constants.PRODUCTS).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    LinkedList<Product> productList = new LinkedList<Product>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Product p = Product.fromJson(document.getData());
//                        p.setId(document.getId());
                        if(p != null) {
                            productList.add(p);
                        }
                    }
                    listener.onComplete(productList);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onComplete(null);
            }
        });
    }

    public void uploadProduct(Product product, Bitmap bitmap, UploadProductListener listener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference userRef = db.collection(Constants.USERS).document(user.getUid());

        Map<String, Object> dbProduct = new HashMap<>();

        DocumentReference productDocRef = db.collection(Constants.PRODUCTS).document();

        productDocRef.set(dbProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                DocumentReference userRef = db.collection(Constants.USERS).document(user.getUid());
                userRef.update( Constants.PRODUCTS, FieldValue.arrayUnion(productDocRef)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void unused) {
                        uploadImage(bitmap, productDocRef.getId(), url -> {
                            if(url != null) {
                                dbProduct.put(Constants.IMG, url);
                                dbProduct.put(Constants.CATEGORY, product.getCategory());
                                dbProduct.put(Constants.SIZE, product.getSize());
                                dbProduct.put(Constants.GENDER, product.getGender());
                                dbProduct.put(Constants.PRICE, product.getPrice());
                                dbProduct.put(Constants.TIME, FieldValue.serverTimestamp());
                                dbProduct.put(Constants.OWNER, userRef);
                                productDocRef.set(dbProduct).addOnCompleteListener(task1 -> {
                                            product.setImg(url);
                                            listener.onComplete(task1, product, user.getUid());
                                        });
                            } else {
                                listener.onComplete(task, new Product(), user.getUid());
                            }
                        });
                    }
                });

            }
        });

    }

    public void uploadImage(Bitmap bitmap, String docId, final UploadImageListener listener) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference imageRef;

        imageRef = storage.getReference().child(Constants.MODEL_FIRE_BASE_IMAGE_COLLECTION).child(docId);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask=imageRef.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onComplete(null);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        listener.onComplete(uri.toString());
                    }
                });
            }
        });
    }

}
