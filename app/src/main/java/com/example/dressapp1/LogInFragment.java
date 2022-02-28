package com.example.dressapp1;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dressapp1.model.DBModel;
import com.example.dressapp1.model.helpers.Constants;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

public class LogInFragment extends Fragment implements View.OnClickListener {
    View view;
    Button loginBtn, registerBtn;
    EditText emailInput, passwordInput;
    ProgressBar pBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_log_in, container, false);

        pBar = view.findViewById(R.id.login_progress);

        loginBtn = view.findViewById(R.id.login_btn);
        registerBtn = view.findViewById(R.id.register_btn);

        emailInput = view.findViewById(R.id.login_email_input);
        passwordInput = view.findViewById(R.id.login_password_input);

        loginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);

        SharedPreferences sp1 = this.getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);

        String email = sp1.getString(Constants.CUR_USER + "_email", null);
        String password = sp1.getString(Constants.CUR_USER + "_password", null);

        if(email != null && password != null) {
            pBar.setVisibility(View.VISIBLE);
//            setEnabled(false);
            loginUserDB(email, password);
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.login_btn:
                loginUser();
                break;
            case R.id.register_btn:
                Navigation.findNavController(view).navigate(LogInFragmentDirections.actionLogInFragmentToSignUpFragment());
                break;
        }
    }

    private void setEnabled(boolean isEnabled) {
        emailInput.setEnabled(isEnabled);
        passwordInput.setEnabled(isEnabled);
        registerBtn.setEnabled(isEnabled);
        loginBtn.setEnabled(isEnabled);
    }

    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if(email.isEmpty()) {
            emailInput.setError("Email is Required");
            emailInput.requestFocus();
            return;
        }
        if(password.isEmpty()) {
            passwordInput.setError("Password is required");
            passwordInput.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Email is not valid");
            emailInput.requestFocus();
            return;
        }
        pBar.setVisibility(View.VISIBLE);
        setEnabled(false);
        loginUserDB(email, password);

    }

    private void loginUserDB(String email, String password) {
        DBModel.dbInstance.loginUser(email, password, new DBModel.LoginUserListener() {
            @Override
            public void onComplete(FirebaseUser user, Task<AuthResult> task) {
                if(task.isSuccessful()) {
                        SharedPreferences sp= getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);
                        SharedPreferences.Editor Ed=sp.edit();
                        Ed.putString(Constants.CUR_USER + "_email", email );
                        Ed.putString(Constants.CUR_USER + "_password", password);
                        Ed.putString(Constants.CUR_USER + "_id", user.getUid());
                        Ed.commit();
                        Navigation.findNavController(view).navigate(LogInFragmentDirections.actionLogInFragmentToSelectGenderFragment());
                        Toast.makeText(getActivity(), "Login successfully", Toast.LENGTH_SHORT).show();
                        pBar.setVisibility(View.INVISIBLE);
                } else {
                    Toast.makeText(getActivity(), "Login failed. Email/Password is incorrect", Toast.LENGTH_SHORT).show();
                    Log.d("ERR","login failed");
                    pBar.setVisibility(View.INVISIBLE);
                }
                setEnabled(true);
            }
        });
    }

}