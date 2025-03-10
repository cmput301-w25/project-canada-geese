package com.example.canada_geese.Pages;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.canada_geese.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Handles user login functionality including "Remember Me" feature.
 * Authenticates users using Firebase Authentication and retrieves user information from Firestore
 */
public class LoginActivity extends AppCompatActivity {

    private EditText usernameField, passwordField;
    private Button signInButton;
    private TextView signUpText;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CheckBox rememberMeCheckBox;

    // SharedPreferences keys for storing login information
    private static final String PREF_NAME = "loginPrefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_REMEMBER_ME = "rememberMe";

    /**
     * Called when the activity is first created.
     * Initializes Firebase authentication and UI components.
     * Checks if the user is already logged in based on saved preferences.
     *
     * @param savedInstanceState Previous state of the activity, if available.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance(); // Initialize Firebase Auth
        db = FirebaseFirestore.getInstance(); // Initialize Firestore

        // Initialize views
        usernameField = findViewById(R.id.username_input);
        passwordField = findViewById(R.id.password_input);
        signInButton = findViewById(R.id.button_sign_in);
        signUpText = findViewById(R.id.sign_up_text);
        rememberMeCheckBox = findViewById(R.id.remember_me); // Remember me checkbox

        // Check if the user is already logged in from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean rememberMe = sharedPreferences.getBoolean(KEY_REMEMBER_ME, false);

        if (rememberMe) {
            // Pre-fill the username and password fields if "Remember Me" was checked
            String username = sharedPreferences.getString(KEY_USERNAME, "");
            String password = sharedPreferences.getString(KEY_PASSWORD, "");

            usernameField.setText(username);
            passwordField.setText(password);

            // Automatically log the user in
            loginWithUsername(username, password, true);
        }

        // Sign In button click listener
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();
                if (username.isEmpty()) {
                    usernameField.setError("Username is required");
                    return;
                }
                if (password.isEmpty()) {
                    passwordField.setError("Password is required");
                    return;
                }
                // Call Firebase Authentication to sign in using username and password
                loginWithUsername(username, password, rememberMeCheckBox.isChecked());
            }
        });

        // Go to Sign Up Activity
        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Logs in a user using their username and password.
     * Queries Firestore to retrieve the associated email and authenticates using FirebaseAuth.
     *
     * @param username    The username entered by the user.
     * @param password    The password entered by the user.
     * @param rememberMe  Whether to remember the user's credentials for future logins.
     */
    public void loginWithUsername(String username, String password, boolean rememberMe) {
        // Query Firestore to find the email associated with the username
        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            // Get the user's email from Firestore
                            String email = task.getResult().getDocuments().get(0).getString("email");

                            // Check if the email is not null or empty
                            if (email != null && !email.isEmpty()) {
                                // Sign in with Firebase Authentication using the email and password
                                mAuth.signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(LoginActivity.this, authTask -> {
                                            if (authTask.isSuccessful()) {
                                                // Sign in successful
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                Toast.makeText(LoginActivity.this, "Logged in as: " + user.getEmail(), Toast.LENGTH_SHORT).show();

                                                // Save the login state if "Remember Me" is checked
                                                if (rememberMe) {
                                                    saveLoginInfo(email, password, username);
                                                }

                                                // Go to MainActivity after successful login
                                                navigateToMainActivity();
                                            } else {
                                                // Login failed
                                                Toast.makeText(LoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                // Handle case where email is null or empty
                                Toast.makeText(LoginActivity.this, "Email not found for the username.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Username not found in Firestore
                            Toast.makeText(LoginActivity.this, "Username not found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Handle error retrieving user data from Firestore
                        Toast.makeText(LoginActivity.this, "Error retrieving user info.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Saves login information to SharedPreferences if "Remember Me" is checked.
     *
     * @param email     The email of the logged-in user.
     * @param password  The password of the logged-in user.
     * @param username  The username of the logged-in user.
     */
    private void saveLoginInfo(String email, String password, String username) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_USERNAME, username);  // Save username too
        editor.putBoolean(KEY_REMEMBER_ME, true);
        editor.apply();
    }

    /**
     * Navigates to the main activity of the app.
     */
    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}