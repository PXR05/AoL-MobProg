package com.pxr.golf.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.pxr.golf.R;
import com.pxr.golf.db.DBManager;
import com.pxr.golf.models.User;
import com.pxr.golf.utils.Auth;
import com.pxr.golf.utils.Setup;

public class LandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_landing);
        Setup.all(this, null);

        new DBManager(this);

        if (getSharedPreferences("user", MODE_PRIVATE).contains("uid")) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        Button guestBtn = findViewById(R.id.landingGuestBtn);
        guestBtn.setOnClickListener(v -> {
            DBManager db = new DBManager(this);
            User user = db.signIn("guest@mail.com", "guestpwd");
            Auth.saveUser(this, user);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        Button registerBtn = findViewById(R.id.landingRegisterBtn);
        registerBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });

        Button loginBtn = findViewById(R.id.landingLoginBtn);
        loginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}