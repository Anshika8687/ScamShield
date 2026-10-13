package com.example.scamorsafe;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.parseColor("#0B0720"));
        setContentView(R.layout.activity_splash);

        // "Scam" in white, "Shield" in purple
        TextView tvName = findViewById(R.id.tvAppName);
        SpannableString name = new SpannableString("ScamShield");
        name.setSpan(new ForegroundColorSpan(Color.parseColor("#A78BFA")),
                4, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvName.setText(name);

        View logo = findViewById(R.id.splashLogo);
        logo.setScaleX(0.5f);
        logo.setScaleY(0.5f);
        logo.setAlpha(0f);
        logo.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(800).start();

        ProgressBar bar = findViewById(R.id.splashBar);
        ObjectAnimator.ofInt(bar, "progress", 0, 100).setDuration(2000).start();

        handler.postDelayed(() -> {
            boolean seen = getSharedPreferences(MainActivity.PREFS, MODE_PRIVATE)
                    .getBoolean("onboarding_done", false);
            startActivity(new Intent(this, seen ? MainActivity.class : OnboardingActivity.class));
            finish();
        }, 2200);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}