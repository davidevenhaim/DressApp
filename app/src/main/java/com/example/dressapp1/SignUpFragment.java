package com.example.dressapp1;

import android.os.Bundle;

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

import com.example.dressapp1.model.DBModel;
import com.example.dressapp1.model.User;

public class SignUpFragment extends Fragment implements View.OnClickListener {
    View view;
    Button signUpBtn;
    TextView alreadyMember;
    ProgressBar pBar;
    EditText fullNameInput, phoneInput, addressInput, cityInput, emailInput, passwordInput;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        if(password.length() < 6) {
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
//        setEnabled(false);

        User user = new User(address, city, email, fullName, phone);
        setEnabled(false);
        DBModel.dbInstance.registerUser(user, password, (user1, task) -> {
            if (task.isSuccessful()) {
                Toast.makeText(getActivity(), "User Created - Please log in again :)", Toast.LENGTH_LONG).show();
                Navigation.findNavController(view).navigate(SignUpFragmentDirections.actionSignUpFragmentToLogInFragment());
            } else {
                Toast.makeText(getActivity(), "User could not be created User Email/Password are not valid " , Toast.LENGTH_LONG).show();
                pBar.setVisibility(View.INVISIBLE);
            }
            setEnabled(true);
        });
    }
}