package com.example.dressapp1.model;

import android.graphics.Bitmap;
import android.net.Uri;
import android.nfc.Tag;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.dressapp1.model.helpers.Constants;
import com.example.dressapp1.model.interfaces.DeleteProductListener;
import com.example.dressapp1.model.interfaces.EditProductListener;
import com.example.dressapp1.model.interfaces.GetUserById;
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

                        documentReference.set(dbUser).addOnCompleteListener(task1 -> {
                            FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
                            listener.onComplete(fbUser, task1);
                        });
                    }  else {
                        Log.d("ERR", "Error creating account");
                    }
            });
    }

    public interface GetAllProductsListener{
        void onComplete(List<Product> productsList);
    }


    public void getAllProducts(Long since, GetAllProductsListener listener) {
        db.collection(Constants.PRODUCTS)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    LinkedList<Product> productList = new LinkedList<Product>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Product p = Product.fromJson(document.getData());
                        p.setId(document.getId());
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

    public void getUserById(String uid, GetUserById listener) {

        DocumentReference docRef = db.collection(Constants.FB_USER_COLLECTION).document(uid);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    User u = User.fromJson(document.getData());
                    listener.onComplete(u);
                } else {
                    listener.onComplete(null);
                }
            } else {
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
                // creating array of references in the user doc.
                userRef.update( Constants.PRODUCTS, FieldValue.arrayUnion(productDocRef)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void unused) {
                        uploadImage(bitmap, productDocRef.getId(), url -> {
                            if(url != null) {
                                product.setImg(url);
                                product.setOwnerId(user.getUid());
                                productDocRef.set(product.toJson()).addOnCompleteListener(task1 -> {
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

    public void deleteProduct(Product product, DeleteProductListener listener) {
        DocumentReference productRef = db.collection(Constants.PRODUCTS).document(product.getId());

        productRef.update(Constants.IS_DELETED, true).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    String ownerId = product.getOwnerId();
                    DocumentReference userRef = db.collection(Constants.FB_USER_COLLECTION).document(ownerId);
                    userRef.update(Constants.PRODUCTS, FieldValue.arrayRemove(productRef))
                            .addOnCompleteListener(task1 -> listener.onComplete(true));
                }
             }
        });
    }

    public void editProduct(Product product, Bitmap bitmap, EditProductListener listener) {
        DocumentReference docRef = db.collection(Constants.PRODUCTS).document(product.getId());
        if(bitmap == null) {
            Log.d("BITMAP null", "bitmap checked and is null!@#!2312312");
            docRef.set(product.toJson()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    listener.onComplete(product);
                }
            });
        } else {
            Log.d("BITMAP not NULL", "bitmap checked and is NOT!!! !@#!2312312");
            uploadImage(bitmap, product.getId(), new UploadImageListener() {
                @Override
                public void onComplete(String url) {
                    product.setImg(url);
                    docRef.set(product.toJson()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            listener.onComplete(product);
                        }
                    });

                }
            });
        }

    }

    public void uploadImage(Bitmap bitmap, String docId, final UploadImageListener listener) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference imageRef;

        imageRef = storage.getReference().child(Constants.FB_IMAGE_COLLECTION).child(docId);
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
