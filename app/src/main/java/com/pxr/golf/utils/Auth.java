package com.pxr.golf.utils;

import android.content.Context;

import com.pxr.golf.models.User;

public class Auth {
    public static User loadUser(Context ctx) {
        String uid = ctx.getSharedPreferences("user", Context.MODE_PRIVATE).getString("uid", null);
        String name = ctx.getSharedPreferences("user", Context.MODE_PRIVATE).getString("name", null);
        String email = ctx.getSharedPreferences("user", Context.MODE_PRIVATE).getString("email", null);
        if (uid == null || name == null || email == null) {
            return new User("-1", "Guest", "guest@mail.com");
        }
        return new User(uid, name, email);
    }

    public static void logout(Context ctx) {
        ctx.getSharedPreferences("user", Context.MODE_PRIVATE).edit().clear().apply();
    }

    public static void saveUser(Context ctx, User user) {
        ctx.getSharedPreferences("user", Context.MODE_PRIVATE).edit()
                .putString("uid", user.getId())
                .putString("name", user.getName())
                .putString("email", user.getEmail())
                .apply();
    }
}
