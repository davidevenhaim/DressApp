package com.example.dressapp1;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.dressapp1.model.DBModel;
import com.example.dressapp1.model.Product;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class NewPostFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    View view;
    Spinner genderInput, sizeInput, categoryInput;
    EditText priceInput;
    Button uploadBtn;
    ImageButton uploadImageBtn;
    Bitmap bitmap;
    ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_new_post, container, false);

        genderInput = view.findViewById(R.id.select_gender);
        sizeInput = view.findViewById(R.id.select_size);
        categoryInput = view.findViewById(R.id.select_category);
        priceInput = view.findViewById(R.id.new_post_price);
        uploadBtn = view.findViewById(R.id.upload_post_btn);
        uploadImageBtn = view.findViewById(R.id.upload_img);
        progressBar = view.findViewById(R.id.new_post_progress);

        uploadBtn.setOnClickListener(this);
        uploadImageBtn.setOnClickListener(this);

        ArrayAdapter<CharSequence> genderAd = ArrayAdapter.
                createFromResource(getContext(), R.array.choose_gender, android.R.layout.simple_spinner_item);
        genderAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderInput.setAdapter(genderAd);

        ArrayAdapter<CharSequence> sizeAd = ArrayAdapter.
                createFromResource(getContext(), R.array.choose_size, android.R.layout.simple_spinner_item);
        genderAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sizeInput.setAdapter(sizeAd);

        ArrayAdapter<CharSequence> categoryAd = ArrayAdapter.
                createFromResource(getContext(), R.array.choose_category, android.R.layout.simple_spinner_item);
        genderAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryInput.setAdapter(categoryAd);

        genderInput.setOnItemSelectedListener(this);
        categoryInput.setOnItemSelectedListener(this);
        sizeInput.setOnItemSelectedListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.upload_post_btn:
                uploadProduct();
                break;
            case R.id.upload_img:
                uploadImage();
                break;
        }
    }

    private void setEnabled(boolean isEnabled) {
        priceInput.setEnabled(isEnabled);
        genderInput.setEnabled(isEnabled);
        categoryInput.setEnabled(isEnabled);
        sizeInput.setEnabled(isEnabled);
        uploadImageBtn.setEnabled(isEnabled);
        uploadBtn.setEnabled(isEnabled);
    }

    private void uploadProduct() {
        String gender = genderInput.getSelectedItem().toString();
        String size = sizeInput.getSelectedItem().toString();
        String category = categoryInput.getSelectedItem().toString();
        String price = priceInput.getText().toString();

        if(price.isEmpty()) {
            priceInput.setError("This is a required field");
            priceInput.requestFocus();
            return;
        }
        if(gender.isEmpty()) {
            return;
        }
        if(size.isEmpty()) {
            return;
        }
        if(category.isEmpty()) {
            return;
        }
        if(bitmap == null) {
            return;
        }
        setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        Product product = new Product(size, price, gender, category);

        DBModel.dbInstance.uploadProduct(product, bitmap, new DBModel.UploadProductListener() {
            @Override
            public void onComplete(Task task, String productId, String userId) {
                if(task.isSuccessful()) {
                progressBar.setVisibility(View.INVISIBLE);
                Navigation.findNavController(view).navigate(NewPostFragmentDirections.actionNewPostFragmentToProductPageFragment(productId, userId));
                }
            }
        });


    }

    private void uploadImage() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
//            Intent intent = new Intent(this, SomeActivity.class);
//        startActivityForResult(intent, 2);
//        builder.setItems(options, new DialogInterface() {
//            @Override
//            public void cancel() {
//                Log.d("Dismiss", "!");
//            }
//
//            @Override
//            public void dismiss() {
//                Log.d("Dismiss", "!");
//            }
//
////            @Override
////            public void onClickListener(DialogInterface dialog, int item) {
////                if (options[item].equals("Take Photo")) {
////                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
////                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
////                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
////                    startActivityForResult(intent, 1);
////                }
////                else if (options[item].equals("Choose from Gallery")) {
////                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
////                    startActivityForResult(intent, 2);
////                }
////                else if (options[item].equals("Cancel")) {
////                    dialog.dismiss();
////                }
////            }
//        });
//        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bundle bundle = data.getExtras();
            bitmap = (Bitmap) bundle.get("data");
            uploadImageBtn.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View v, int i, long l) {
        String selected = "";
        selected = adapterView.getItemAtPosition(i).toString();
        Log.d("Tag", selected);
        Log.d("ID", v.getId() + "");
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}