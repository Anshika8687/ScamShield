package com.example.scamorsafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.parseColor("#0F172A"));
        setContentView(R.layout.activity_result);

        int score = getIntent().getIntExtra("score", 0);
        int played = getIntent().getIntExtra("played", 1);
        int correct = getIntent().getIntExtra("correct", 0);
        int bestStreak = getIntent().getIntExtra("bestStreak", 0);
        int difficulty = getIntent().getIntExtra("difficulty", 1);
        boolean daily = getIntent().getBooleanExtra("daily", false);
        int dailyStreak = getIntent().getIntExtra("dailyStreak", 0);
        ArrayList<String> mistakes = getIntent().getStringArrayListExtra("mistakes");

        int percent = correct * 100 / Math.max(played, 1);

        String rank;
        String emoji;
        if (percent >= 80) {
            rank = "Scam Detective";
            emoji = "🏆";
        } else if (percent >= 50) {
            rank = "Alert Learner";
            emoji = "👍";
        } else {
            rank = "Beginner - keep learning!";
            emoji = "📚";
        }

        String[] modeNames = {"Easy", "Medium", "Hard"};
        String modeName = modeNames[Math.max(0, Math.min(2, difficulty))];

        TextView tvNewBest = findViewById(R.id.tvNewBest);
        String modeLine;

        if (daily) {
            // daily challenge: shows the streak and does not touch normal high scores
            tvNewBest.setVisibility(View.VISIBLE);
            tvNewBest.setText("📅 Daily done!  🔥 " + dailyStreak + "-day streak");
            modeLine = "📅 Daily Challenge";
        } else {
            SharedPreferences prefs = getSharedPreferences(MainActivity.PREFS, MODE_PRIVATE);
            String key = "best_" + difficulty;
            int oldBest = prefs.getInt(key, 0);
            if (score > oldBest) {
                prefs.edit().putInt(key, score).apply();
                if (score > 0) {
                    tvNewBest.setVisibility(View.VISIBLE);
                    tvNewBest.setText("🎉 NEW HIGH SCORE!");
                }
            }
            int best = Math.max(score, oldBest);
            modeLine = "🎚 Mode: " + modeName + "   🏆 Best: " + best;
        }

        ((TextView) findViewById(R.id.tvEmoji)).setText(emoji);
        ((TextView) findViewById(R.id.tvFinalScore)).setText(String.valueOf(score));
        ((TextView) findViewById(R.id.tvStats)).setText(
                correct + " correct out of " + played
                        + "\n🔥 Best streak: " + bestStreak
                        + "\n" + modeLine);
        ((TextView) findViewById(R.id.tvRank)).setText(rank);

        TextView tvMistakes = findViewById(R.id.tvMistakes);
        if (mistakes == null || mistakes.isEmpty()) {
            tvMistakes.setText("🎉 No mistakes. Excellent work!");
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mistakes.size(); i++) {
                sb.append(mistakes.get(i));
                if (i < mistakes.size() - 1) sb.append("\n\n──────────\n\n");
            }
            tvMistakes.setText(sb.toString());
        }

        View emojiView = findViewById(R.id.tvEmoji);
        emojiView.setScaleX(0f);
        emojiView.setScaleY(0f);
        emojiView.animate().scaleX(1f).scaleY(1f).setDuration(600).start();

        Button btnAgain = findViewById(R.id.btnAgain);
        if (daily) {
            btnAgain.setText("▶  PLAY A NORMAL GAME");
        }
        btnAgain.setOnClickListener(v -> {
            Intent i = new Intent(this, GameActivity.class);
            i.putExtra("difficulty", difficulty);
            startActivity(i);
            finish();
        });

        findViewById(R.id.btnLearn).setOnClickListener(v ->
                startActivity(new Intent(this, AwarenessActivity.class)));
        findViewById(R.id.btnHome).setOnClickListener(v -> {
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        });
    }
}