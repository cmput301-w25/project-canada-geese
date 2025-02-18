package com.example.canada_geese.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.canada_geese.Managers.AuthManager;
import com.example.canada_geese.R;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailField, passwordField, usernameField;
    private Button registerButton;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailField = findViewById(R.id.input_email);
        passwordField = findViewById(R.id.input_password);
        usernameField = findViewById(R.id.input_username);
        registerButton = findViewById(R.id.button_register);
        authManager = new AuthManager();

        registerButton.setOnClickListener(view -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            String username = usernameField.getText().toString().trim();

            authManager.registerUser(email, password, username, this, new AuthManager.AuthCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}