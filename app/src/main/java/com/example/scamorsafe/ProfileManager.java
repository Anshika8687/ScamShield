package com.example.scamorsafe;

import android.content.Context;
import android.content.SharedPreferences;

/** Stores the player's name and avatar on the phone. There is no account or login. */
public class ProfileManager {

    public static final String[] AVATARS = {"😎", "🕵️", "🦸", "🦊", "🐼", "🤖"};

    private static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences(MainActivity.PREFS, Context.MODE_PRIVATE);
    }

    public static String getName(Context c) {
        return prefs(c).getString("profile_name", "New Player");
    }

    public static void setName(Context c, String name) {
        prefs(c).edit().putString("profile_name", name).apply();
    }

    public static String getAvatar(Context c) {
        return prefs(c).getString("profile_avatar", AVATARS[0]);
    }

    public static void setAvatar(Context c, String avatar) {
        prefs(c).edit().putString("profile_avatar", avatar).apply();
    }
}