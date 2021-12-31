package com.example.dressapp1;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpFragment extends Fragment implements View.OnClickListener {
    View view;
    Button signUpBtn;
    TextView alreadyMember;
    ProgressBar pBar;
    EditText fullNameInput, phoneInput, addressInput, cityInput, emailInput, passwordInput;
    FirebaseAuth mAuth;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseReference ref = FirebaseDatabase.getInstance("https://dressapp-ba7fe-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_sign_up, container, false);

        signUpBtn = view.findViewById(R.id.signup_btn);
        alreadyMember = view.findViewById(R.id.already_member);

        pBar = view.findViewById(R.id.signup_progress);

        fullNameInput = view.findViewById(R.id.register_name_input);
        phoneInput = view.findViewById(R.id.register_phone_input);
        addressInput = view.findViewById(R.id.register_address_input);
        cityInput = view.findViewById(R.id.register_city_input);
        emailInput = view.findViewById(R.id.register_email_input);
        passwordInput = view.findViewById(R.id.register_password_input);

        signUpBtn.setOnClickListener(this);
        alreadyMember.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signup_btn:
                registerUser();
                break;
            case R.id.already_member:
                Navigation.findNavController(view).navigate(SignUpFragmentDirections.actionSignUpFragmentToLogInFragment());
                break;
        }
    }

    private void setEnabled(boolean isEnabled) {
        signUpBtn.setEnabled(isEnabled);
        alreadyMember.setEnabled(isEnabled);
        fullNameInput.setEnabled(isEnabled);
        phoneInput.setEnabled(isEnabled);
        addressInput.setEnabled(isEnabled);
        cityInput.setEnabled(isEnabled);
        emailInput.setEnabled(isEnabled);
        passwordInput.setEnabled(isEnabled);
    }

    private void registerUser() {

        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String fullName = fullNameInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();
        String city = cityInput.getText().toString().trim();

        if(email.isEmpty()) {
            emailInput.setError("Required Field");
            emailInput.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailInput.setError("Email is not VALID!!");
            emailInput.requestFocus();
            return;
        }
        if(password.isEmpty()) {
            phoneInput.setError("Required Field");
            passwordInput.requestFocus();
            return;
        }
        if(password.length() < 2) {
            phoneInput.setError("Password is not long enough :(");
            passwordInput.requestFocus();
            return;
        }
        if(fullName.isEmpty()) {
            fullNameInput.setError("Required Field");
            fullNameInput.requestFocus();
            return;
        }

        pBar.setVisibility(View.VISIBLE);
        setEnabled(false);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            User user = new User(address, city, email, fullName, phone);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user)
                                    .addOnCompleteListener(task1 -> {
                                        Log.d("TASK11", "ENTERED TASK 1");
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(getActivity(), "User Created - Check your email inbox", Toast.LENGTH_LONG).show();
                                            FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
                                            Navigation.findNavController(view).navigate(SignUpFragmentDirections.actionSignUpFragmentToLogInFragment());
                                            Log.d("Success", "Email sent");
                                        } else {
                                            Toast.makeText(getActivity(), "User could not be created", Toast.LENGTH_LONG).show();
                                            Log.d("ELSE", "Secondary else case reached.");
                                        }
                                    });
                        }    else {
                            Log.d("ELSE", "MAIN else case reached");
                            try {
                                throw task.getException();
                            }
                            catch (FirebaseAuthInvalidCredentialsException e) {
                                Toast.makeText(getActivity(), "Invalid Password", Toast.LENGTH_LONG).show();
                            }
                            catch (FirebaseAuthEmailException e){
                                Toast.makeText(getActivity(), "Invalid Email", Toast.LENGTH_LONG).show();
                            }
                            catch (FirebaseAuthException e){
                                Toast.makeText(getActivity(), "Invalid Credentials", Toast.LENGTH_LONG).show();
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getActivity(), "Error creating user", Toast.LENGTH_LONG).show();
                            }

                        }
                            pBar.setVisibility(View.INVISIBLE);
                            setEnabled(true);
                    }
                });
    }
}