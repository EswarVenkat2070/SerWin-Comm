package com.ase.serwincomm;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginPage extends AppCompatActivity {

    private EditText loginEmail;
    private EditText loginPassword;
    private Button loginBtn;
    private Button registerBtn;

    private FirebaseAuth fAuth;
    private ProgressBar progressBar;

    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        loginEmail =  findViewById(R.id.textLoginEmailAddress);
        loginPassword = findViewById(R.id.textLoginPassword);
        loginBtn = findViewById(R.id.btnSignIn);
        registerBtn = findViewById(R.id.btnRegister);

        fAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    //redirect
                    Log.i("TAG", "onAuthStateChanged:signed_in");

                } else {
                    // User is signed out
                    Log.d("TAG", "onAuthStateChanged:signed_out");
                }

            }
        };

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // userLogin();
                startActivity(new Intent(LoginPage.this, weatherConditions.class));

            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginPage.this, SignUp.class));
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        fAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            fAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void userLogin() {

        String email = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        if(email.isEmpty()){
            loginEmail.setError("Email is required");
            loginEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            loginPassword.setError("Password is required");
            loginPassword.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            loginEmail.setError("Please enter valid email id");
            loginEmail.requestFocus();
            return;
        }

        fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(LoginPage.this, WelcomePage.class));
                }
                else{
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        loginEmail.setError("Invalid Emaild Id");
                        loginEmail.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        Log.i("email" , "email :" + email);
                        loginPassword.setError("Invalid Password");
                        loginPassword.requestFocus();
                    } catch (FirebaseNetworkException e) {
                        Toast.makeText(LoginPage.this,"error_message_failed_sign_in_no_network", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.i("Exception line 131", e.getMessage());
                    }
                    Log.i("Exception signInWithEmail", "signInWithEmail:failed", task.getException());

                    Toast.makeText(LoginPage.this, "Login Failed, Please check your credentials!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


}