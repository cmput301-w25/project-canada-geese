package com.example.canada_geese.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.canada_geese.Managers.AuthManager;
import com.example.canada_geese.R;

public class LoginActivity extends AppCompatActivity {
    private EditText identifierField, passwordField;
    private Button loginButton;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        identifierField = findViewById(R.id.input_identifier);
        passwordField = findViewById(R.id.input_password);
        loginButton = findViewById(R.id.button_login);
        authManager = new AuthManager();

        loginButton.setOnClickListener(view -> {
            String identifier = identifierField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            authManager.loginUser(identifier, password, new AuthManager.AuthCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}