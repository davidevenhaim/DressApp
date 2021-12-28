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

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInFragment extends Fragment implements View.OnClickListener {
    View view;
    TextView forgotPassword;
    Button loginBtn, registerBtn;
    EditText emailInput, passwordInput;
    ProgressBar pBar;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_log_in, container, false);

        pBar = view.findViewById(R.id.login_progress);

        forgotPassword = view.findViewById(R.id.forgot_pass);
        loginBtn = view.findViewById(R.id.login_btn);
        registerBtn = view.findViewById(R.id.register_btn);

        emailInput = view.findViewById(R.id.login_email_input);
        passwordInput = view.findViewById(R.id.login_password_input);

        forgotPassword.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.login_btn:
                loginUser();
                break;
            case R.id.forgot_pass:
                Navigation.findNavController(view).navigate(LogInFragmentDirections.actionLogInFragmentToForgotPassFragment());
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
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        pBar.setVisibility(View.GONE);
                        if(task.isSuccessful()) {
                            if(user.isEmailVerified()) {
                            // send to homepage
//                            Toast.makeText(getActivity(), "Login successfully", Toast.LENGTH_SHORT).show();
//                            pBar.setVisibility(View.INVISIBLE);
                            }else {
                                user.sendEmailVerification();
                                Toast.makeText(getActivity(), "Email Verification Sent, check your inbox! and try login again", Toast.LENGTH_SHORT).show();
                                Log.d("E", "Email verification.");
//                                pBar.setVisibility(View.INVISIBLE);
                                setEnabled(true);
                            }
                        }else {
                            Toast.makeText(getActivity(), "Login failed. Email/Password is incorrect", Toast.LENGTH_SHORT).show();
                            Log.d("ERR","login failed");
//                            pBar.setVisibility(View.INVISIBLE);
                            setEnabled(true);
                        }
                        Log.d("E", "finished siginin in.");
                    }
                });
    }

}