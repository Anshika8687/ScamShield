package com.example.scamorsafe;

import android.content.Context;
import android.content.SharedPreferences;

public class SecurityScore {

    private static final String[] TYPES = {"SMS", "EMAIL", "URL"};

    private static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences(MainActivity.PREFS, Context.MODE_PRIVATE);
    }

    private static int totalAnswers(SharedPreferences p) {
        int total = 0;
        for (String t : TYPES) total += p.getInt("stat_tot_" + t, 0);
        return total;
    }

    /** 0 to 100: 50% quiz accuracy + 25% chat survival + 25% daily streak (capped at 7 days). */
    public static int calculate(Context c) {
        SharedPreferences p = prefs(c);

        int total = totalAnswers(p);
        int correct = 0;
        for (String t : TYPES) correct += p.getInt("stat_cor_" + t, 0);
        int accuracy = total == 0 ? 0 : correct * 100 / total;

        int chatPlayed = p.getInt("stat_chat_played", 0);
        int chatSafe = p.getInt("stat_chat_safe", 0);
        int chat = chatPlayed == 0 ? 0 : chatSafe * 100 / chatPlayed;

        int streak = Math.min(DailyChallenge.getStreak(p), 7) * 100 / 7;

        return (accuracy * 50 + chat * 25 + streak * 25) / 100;
    }

    public static String label(Context c, int score) {
        SharedPreferences p = prefs(c);
        if (totalAnswers(p) == 0 && p.getInt("stat_chat_played", 0) == 0) {
            return "Play a game to build your score";
        }
        if (score < 40) return "Needs practice";
        if (score < 70) return "Getting stronger";
        if (score < 90) return "Well protected";
        return "Scam-proof!";
    }
}