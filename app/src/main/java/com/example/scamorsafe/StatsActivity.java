package com.example.scamorsafe;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class StatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.parseColor("#0F172A"));
        setContentView(R.layout.activity_stats);

        SharedPreferences p = getSharedPreferences(MainActivity.PREFS, MODE_PRIVATE);

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());

        String[] types = {"SMS", "EMAIL", "URL"};
        String[] names = {"💬 SMS", "📧 Email", "🔗 URL"};
        String[] tips = {
                "Watch for urgency, links and any request for an OTP or PIN in SMS.",
                "Check the sender's domain letter by letter. Scammers copy names but change one character.",
                "Look at the real domain, the part right before the first '/'. Fake names often hide in subdomains."
        };
        int[] labelIds = {R.id.tvSms, R.id.tvEmail, R.id.tvUrl};
        int[] barIds = {R.id.barSms, R.id.barEmail, R.id.barUrl};

        int totalAll = 0;
        int correctAll = 0;
        int weakIndex = -1;
        int weakPercent = 101;

        for (int i = 0; i < types.length; i++) {
            int total = p.getInt("stat_tot_" + types[i], 0);
            int correct = p.getInt("stat_cor_" + types[i], 0);
            int percent = total == 0 ? 0 : correct * 100 / total;

            totalAll += total;
            correctAll += correct;

            ((TextView) findViewById(labelIds[i])).setText(
                    names[i] + "   " + percent + "%   (" + correct + "/" + total + ")");
            ((ProgressBar) findViewById(barIds[i])).setProgress(percent);

            if (total >= 3 && percent < weakPercent) {
                weakPercent = percent;
                weakIndex = i;
            }
        }

        int games = p.getInt("stat_games", 0);
        int accuracy = totalAll == 0 ? 0 : correctAll * 100 / totalAll;
        int chatPlayed = p.getInt("stat_chat_played", 0);
        int chatSafe = p.getInt("stat_chat_safe", 0);
        int streak = DailyChallenge.getStreak(p);

        ((TextView) findViewById(R.id.tvSummary)).setText(
                "🎮 Games played: " + games
                        + "\n🎯 Overall accuracy: " + accuracy + "%"
                        + "\n💬 Chat scams survived: " + chatSafe + " of " + chatPlayed
                        + "\n🔥 Daily streak: " + streak + (streak == 1 ? " day" : " days"));

        TextView tvTip = findViewById(R.id.tvTip);
        if (totalAll == 0) {
            tvTip.setText("Play a game to see your stats and your weak spot here!");
        } else if (weakIndex >= 0 && weakPercent < 100) {
            tvTip.setText("💡 Your weak spot: " + names[weakIndex] + " (" + weakPercent
                    + "% correct)\n\n" + tips[weakIndex]);
        } else {
            tvTip.setText("🌟 Great accuracy in every category! Try Hard mode or the chat simulator.");
        }

        ((TextView) findViewById(R.id.tvBests)).setText(
                "🟢 Easy: " + p.getInt("best_0", 0)
                        + "      🟡 Medium: " + p.getInt("best_1", 0)
                        + "      🔴 Hard: " + p.getInt("best_2", 0));

        findViewById(R.id.btnReset).setOnClickListener(v -> {
            Stats.reset(this);
            Toast.makeText(this, "Stats reset", Toast.LENGTH_SHORT).show();
            recreate();
        });
    }
}