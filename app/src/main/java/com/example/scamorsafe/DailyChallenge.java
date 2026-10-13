package com.example.scamorsafe;

import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class DailyChallenge {

    public static final int QUESTIONS = 5;

    private static String dateKey(int offsetDays) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, offsetDays);
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.getTime());
    }

    public static String todayKey() {
        return dateKey(0);
    }

    /** Same 5 questions all day: the shuffle is seeded with today's date. */
    public static List<Question> getQuestions() {
        List<Question> list = QuestionBank.getRandom(1000);   // gets the whole bank
        Collections.sort(list, (a, b) -> a.content.compareTo(b.content));
        Collections.shuffle(list, new Random(todayKey().hashCode()));
        return new ArrayList<>(list.subList(0, Math.min(QUESTIONS, list.size())));
    }

    public static boolean isDoneToday(SharedPreferences p) {
        return todayKey().equals(p.getString("daily_last", ""));
    }

    public static int getLastScore(SharedPreferences p) {
        return p.getInt("daily_score", 0);
    }

    /** Streak only counts if you played today or yesterday. */
    public static int getStreak(SharedPreferences p) {
        String last = p.getString("daily_last", "");
        if (last.equals(todayKey()) || last.equals(dateKey(-1))) {
            return p.getInt("daily_streak", 0);
        }
        return 0;
    }

    /** Call when the daily game finishes. Returns the new streak. */
    public static int complete(SharedPreferences p, int score) {
        String last = p.getString("daily_last", "");
        if (last.equals(todayKey())) {
            return p.getInt("daily_streak", 1);
        }
        int streak = last.equals(dateKey(-1)) ? p.getInt("daily_streak", 0) + 1 : 1;
        p.edit()
                .putString("daily_last", todayKey())
                .putInt("daily_streak", streak)
                .putInt("daily_score", score)
                .apply();
        return streak;
    }
}