package com.pxr.golf.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.pxr.golf.R;
import com.pxr.golf.db.DBManager;
import com.pxr.golf.models.User;
import com.pxr.golf.utils.Auth;
import com.pxr.golf.utils.Setup;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        View root = findViewById(R.id.main);
        Setup.all(this, root);
    }

    private void signIn(String email, String password) {
        DBManager db = new DBManager(this);
        User user = db.signIn(email, password);
        if (user == null) {
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            return;
        }
        Auth.saveUser(this, user);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}