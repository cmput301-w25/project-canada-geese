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

/**
 * Activity for user registration in the app.
 * Allows users to sign up with an email, password, and username,
 * and saves the user data to Firebase Authentication and Firestore.
 */
public class SignUpActivity extends AppCompatActivity {
    private EditText emailField, passwordField, usernameField;
    private Button SignUpButton;
    private TextView sign_in_text;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Called when the activity is first created.
     * Initializes the views and sets up event listeners for sign-up and sign-in actions.
     *
     * @param savedInstanceState Previous state of the activity, if available.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize views
        emailField = findViewById(R.id.email_input);
        passwordField = findViewById(R.id.password_input);
        usernameField = findViewById(R.id.username_input);
        SignUpButton = findViewById(R.id.button_sign_in);
        sign_in_text = findViewById(R.id.sign_up_text);

        // Set up sign-up button click listener
        SignUpButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Validates input fields and initiates sign-up process on button click.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
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

                // Initiate sign-up
                signUp(email, password, username);
            }
        });

        // Set up sign-in text click listener to navigate to LoginActivity
        sign_in_text.setOnClickListener(new View.OnClickListener() {
            /**
             * Navigates to the login activity when sign-in text is clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Close SignUpActivity to prevent returning to it
            }
        });
    }

    /**
     * Handles user registration using Firebase Authentication and saves additional user data to Firestore.
     *
     * @param email    The email entered by the user.
     * @param password The password entered by the user.
     * @param username The username entered by the user.
     */
    public void signUp(String email, String password, String username) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null) {
                            // Create a map to store user information
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("username", username);
                            userMap.put("email", email);

                            // Save user data to Firestore
                            db.collection("users").document(user.getUid())
                                    .set(userMap)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("SignUp", "User's username saved to Firestore");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("SignUp", "Error saving username", e);
                                    });
                        }

                        // Navigate to MainActivity upon successful sign-up
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.e("SignUp", "Sign up failed", task.getException());
                    }
                });
    }
}


