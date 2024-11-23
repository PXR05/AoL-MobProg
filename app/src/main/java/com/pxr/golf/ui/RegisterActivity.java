package com.pxr.golf.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.pxr.golf.R;
import com.pxr.golf.db.DBManager;
import com.pxr.golf.models.User;
import com.pxr.golf.utils.Auth;
import com.pxr.golf.utils.Setup;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        View root = findViewById(R.id.main);
        Setup.all(this, root);

        EditText usernameET = findViewById(R.id.usernameET);
        EditText emailET = findViewById(R.id.emailET);
        EditText passwordET = findViewById(R.id.passwordET);
        EditText confirmET = findViewById(R.id.confirmET);
        Button submitBTN = findViewById(R.id.submitBTN);

        submitBTN.setOnClickListener(e -> {
            String username = usernameET.getText().toString();
            String email = emailET.getText().toString();
            String password = passwordET.getText().toString();
            String confirmPassword = confirmET.getText().toString();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                signUp(username, email, password);
            }
        });
    }

    private void signUp(String name, String email, String password) {
        DBManager db = new DBManager(this);
        User user = db.signUp(name, email, password);
        if (user == null) {
            Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show();
            return;

        }
        Auth.saveUser(this, user);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}