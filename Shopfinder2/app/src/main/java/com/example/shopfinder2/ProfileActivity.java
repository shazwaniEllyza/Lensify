package com.example.shopfinder2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity {

    private EditText editOldEmail, editNewEmail, editPhone, editPassword;
    private Button saveButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize views
        editOldEmail = findViewById(R.id.edit_old_email);
        editNewEmail = findViewById(R.id.edit_new_email);
        editPhone = findViewById(R.id.edit_phone);
        editPassword = findViewById(R.id.edit_password);
        saveButton = findViewById(R.id.save_button);

        FirebaseUser currentUser = mAuth.getCurrentUser();

        // If user is not logged in, redirect to LoginActivity
        if (currentUser == null) {
            Toast.makeText(this, "Please log in to update your profile", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
            return;
        }

        saveButton.setOnClickListener(v -> {
            String oldEmail = editOldEmail.getText().toString();
            String newEmail = editNewEmail.getText().toString();
            String phone = editPhone.getText().toString();
            String password = editPassword.getText().toString();

            // Validation
            if (TextUtils.isEmpty(oldEmail) || TextUtils.isEmpty(newEmail) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
                Toast.makeText(ProfileActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                Toast.makeText(ProfileActivity.this, "Please enter a valid new email address", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if the old email is the same as the new email
            if (oldEmail.equals(newEmail)) {
                Toast.makeText(ProfileActivity.this, "The new email is the same as the old email", Toast.LENGTH_SHORT).show();
                return;
            }

            // Re-authenticate before updating email
            AuthCredential credential = EmailAuthProvider.getCredential(oldEmail, password);

            currentUser.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Successfully re-authenticated
                    currentUser.updateEmail(newEmail).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            // Update the new email in the Realtime Database
                            String userId = currentUser.getUid();
                            mDatabase.child(userId).child("email").setValue(newEmail)
                                    .addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            Toast.makeText(ProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ProfileActivity.this, "Error updating email in database", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(ProfileActivity.this, "Error updating email in Firebase", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(ProfileActivity.this, "Re-authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
