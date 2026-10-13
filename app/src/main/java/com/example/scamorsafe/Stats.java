package com.example.scamorsafe;

import android.content.Context;
import android.content.SharedPreferences;

public class Stats {

    private static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences(MainActivity.PREFS, Context.MODE_PRIVATE);
    }

    /** type is SMS, EMAIL or URL */
    public static void recordAnswer(Context c, String type, boolean correct) {
        SharedPreferences p = prefs(c);
        SharedPreferences.Editor e = p.edit();
        e.putInt("stat_tot_" + type, p.getInt("stat_tot_" + type, 0) + 1);
        if (correct) {
            e.putInt("stat_cor_" + type, p.getInt("stat_cor_" + type, 0) + 1);
        }
        e.apply();
    }

    public static void recordGame(Context c) {
        SharedPreferences p = prefs(c);
        p.edit().putInt("stat_games", p.getInt("stat_games", 0) + 1).apply();
    }

    public static void recordChat(Context c, boolean safe) {
        SharedPreferences p = prefs(c);
        SharedPreferences.Editor e = p.edit();
        e.putInt("stat_chat_played", p.getInt("stat_chat_played", 0) + 1);
        if (safe) {
            e.putInt("stat_chat_safe", p.getInt("stat_chat_safe", 0) + 1);
        }
        e.apply();
    }

    /** Clears only the statistics, not high scores or the daily streak. */
    public static void reset(Context c) {
        SharedPreferences p = prefs(c);
        SharedPreferences.Editor e = p.edit();
        for (String key : p.getAll().keySet()) {
            if (key.startsWith("stat_")) e.remove(key);
        }
        e.apply();
    }
}