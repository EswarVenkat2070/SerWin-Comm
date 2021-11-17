package com.ase.serwincomm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText mFullName, mEmail, mPassword;
    Button mRegisterBtn, mLoginBtn;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setTitle("Sign Up");

        mFullName                =  findViewById(R.id.editTextPersonName);
        mEmail                   =  findViewById(R.id.editRegisterEmailAddress);
        mPassword                =  findViewById(R.id.editRegisterPassword);

        mRegisterBtn             =  findViewById(R.id.btnSignup);

        fAuth = FirebaseAuth.getInstance();

        fStore= FirebaseFirestore.getInstance();

        //if(fAuth.getCurrentUser() !=null)
        //{
        //startActivity(new Intent(getApplicationContext(),MainActivity.class));
        //finish();
        //}

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                final String fullName = mFullName.getText().toString();


                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is required");
                    mEmail.requestFocus();
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    mEmail.setError("Invalid Email");
                    mEmail.requestFocus();
                }

                if (TextUtils.isEmpty(fullName)) {
                    mFullName.setError("Name is required");
                    mFullName.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is required");
                    mPassword.requestFocus();
                    return;
                }
                if (password.length() < 8) {
                    mPassword.setError("Password must be >=6 characters");
                    mPassword.requestFocus();
                    return;
                }

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUp.this, "User Created", Toast.LENGTH_SHORT).show();

                            userID = fAuth.getCurrentUser().getUid();
                            Toast.makeText(SignUp.this, userID, Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(getApplicationContext(), WelcomePage.class));
                        } else {
                            Toast.makeText(SignUp.this, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


    }
}