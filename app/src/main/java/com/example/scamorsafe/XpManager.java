package com.example.scamorsafe;

import android.content.Context;
import android.content.SharedPreferences;

public class XpManager {

    public static final int XP_PER_LEVEL = 200;

    private static final String[] TITLES = {
            "Rookie", "Learner", "Scam Spotter", "Detective", "Guardian", "Expert", "Legend"
    };

    private static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences(MainActivity.PREFS, Context.MODE_PRIVATE);
    }

    public static int getXp(Context c) {
        return prefs(c).getInt("xp_total", 0);
    }

    public static void addXp(Context c, int amount) {
        if (amount <= 0) return;
        prefs(c).edit().putInt("xp_total", getXp(c) + amount).apply();
    }

    public static int getLevel(Context c) {
        return getXp(c) / XP_PER_LEVEL + 1;
    }

    public static int getXpInLevel(Context c) {
        return getXp(c) % XP_PER_LEVEL;
    }

    public static String getTitle(int level) {
        return TITLES[Math.min(Math.max(level, 1), TITLES.length) - 1];
    }
}