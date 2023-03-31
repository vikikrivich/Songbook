package com.example.songbook;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText password, email;
    private TextView signup, login;
    private FirebaseAuth myauth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginlayout);
        init();

        signup.setOnClickListener(oclsignin);
        login.setOnClickListener(oclsignup);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser cUser = myauth.getCurrentUser();
        if (cUser != null) {
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
        }
    }

    private void init() {
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        signup = findViewById(R.id.getin);
        login = findViewById(R.id.login);
        myauth = FirebaseAuth.getInstance();
    }



    View.OnClickListener oclsignup = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!TextUtils.isEmpty(email.getText().toString()) && !TextUtils.isEmpty(password.getText().toString()) ){
                myauth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString());
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
            }
        }
    };

    View.OnClickListener oclsignin = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!TextUtils.isEmpty(email.getText().toString()) && !TextUtils.isEmpty(password.getText().toString()) ){
                myauth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString());
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
            }
        }
    };


}
