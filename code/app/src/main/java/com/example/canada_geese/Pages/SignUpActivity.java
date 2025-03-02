package com.example.canada_geese.Pages;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.canada_geese.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class SignUpActivity extends AppCompatActivity {
    private EditText emailField, passwordField, usernameField;
    private Button SignUpButton;
    private TextView sign_in_text;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailField = findViewById(R.id.email_input);
        passwordField = findViewById(R.id.password_input);
        usernameField = findViewById(R.id.username_input);
        SignUpButton = findViewById(R.id.button_sign_in);
        //authManager = new AuthManager();
        sign_in_text = findViewById(R.id.sign_up_text);


        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get input values
                String email = emailField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();
                String username = usernameField.getText().toString().trim();
                // Validate inputs
                if (email.isEmpty()) {
                    emailField.setError("Email is required");
                    return;
                }
                if (password.isEmpty()) {
                    passwordField.setError("Password is required");
                    return;
                }
                if (username.isEmpty()) {
                    usernameField.setError("Username is required");
                    return;
                }
                // Call the signUp method to register the user
                signUp(email, password, username);
                // Optionally, show a loading indicator
                // You can also add logic to show a loading spinner until the sign-up process completes.
            }
        });

        sign_in_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start MainActivity when the Sign In button is pressed
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Finish the LoginActivity so the user can't go back to it
            }
        });

/* auth manager broken
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
            }
            );
        });*/
    }

    public void signUp(String email, String password, String username) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // User registration successful
                        FirebaseUser user = mAuth.getCurrentUser();

                        // Now save the username in Firestore
                        if (user != null) {
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("username", username);
                            userMap.put("email", email);

                            // Save the username to Firestore under the user's UID
                            db.collection("users").document(user.getUid())
                                    .set(userMap)
                                    .addOnSuccessListener(aVoid -> {
                                        // Username saved successfully
                                        Log.d("SignUp", "User's username saved to Firestore");
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle error
                                        Log.e("SignUp", "Error saving username", e);
                                    });
                            //REMEMBER ME
                        }
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // If sign up fails, display a message to the user.
                        Log.e("SignUp", "Sign up failed", task.getException());
                    }
                });
    }
}

