package com.example.scamorsafe;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class OnboardingActivity extends AppCompatActivity {

    private final String[] emojis = {"🎣", "💬", "🏆"};
    private final String[] titles = {
            "Stay One Step Ahead of Scammers",
            "Practise Real Scams Safely",
            "Level Up Your Safety"
    };
    private final String[] subs = {
            "Real scenarios. Practical learning.\nA safer digital life.",
            "Chat with fake scammers in a safe space and learn exactly what to do.",
            "Earn XP, keep your daily streak and unlock achievements."
    };

    private int page = 0;
    private TextView tvEmoji, tvTitle, tvSub, tvSkip;
    private Button btnNext;
    private final TextView[] dots = new TextView[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.parseColor("#0B0720"));
        setContentView(R.layout.activity_onboarding);

        tvEmoji = findViewById(R.id.tvEmoji);
        tvTitle = findViewById(R.id.tvTitle);
        tvSub = findViewById(R.id.tvSub);
        tvSkip = findViewById(R.id.tvSkip);
        btnNext = findViewById(R.id.btnNext);
        dots[0] = findViewById(R.id.dot0);
        dots[1] = findViewById(R.id.dot1);
        dots[2] = findViewById(R.id.dot2);

        btnNext.setOnClickListener(v -> {
            if (page < titles.length - 1) {
                page++;
                show();
            } else {
                finishOnboarding();
            }
        });
        tvSkip.setOnClickListener(v -> finishOnboarding());

        show();
    }

    private void show() {
        View content = findViewById(R.id.pageContent);
        content.setAlpha(0f);
        tvEmoji.setText(emojis[page]);
        tvTitle.setText(titles[page]);
        tvSub.setText(subs[page]);
        content.animate().alpha(1f).setDuration(350).start();

        for (int i = 0; i < dots.length; i++) {
            dots[i].setTextColor(i == page ? 0xFFA78BFA : 0x44FFFFFF);
        }
        boolean last = page == titles.length - 1;
        btnNext.setText(last ? "Get Started  ➜" : "Next  ➜");
        tvSkip.setVisibility(last ? View.INVISIBLE : View.VISIBLE);
    }

    private void finishOnboarding() {
        getSharedPreferences(MainActivity.PREFS, MODE_PRIVATE)
                .edit().putBoolean("onboarding_done", true).apply();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}