package com.example.scamorsafe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.util.Locale;
import java.util.Random;

public class HomeFragment extends Fragment {

    private static final String[] TIPS = {
            "Banks never ask for your OTP, PIN or CVV. Not on call, SMS or WhatsApp.",
            "You never need to enter a UPI PIN to RECEIVE money.",
            "Check the real domain: it is the part right before the first '/'.",
            "Urgency is a trick. Real banks do not threaten to block you in 30 minutes.",
            "'Digital arrest' does not exist. No police arrests anyone over a video call.",
            "If an offer sounds too good to be true, it is a scam. Call 1930 if you are targeted."
    };

    private ScoreRingView ring;
    private TextView tvStreakVal, tvXpVal, tvLevelVal, tvLevelTitle, tvScoreLabel, tvDailyInfo, btnAvatar;
    private Button btnDaily;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ring = view.findViewById(R.id.scoreRing);
        tvStreakVal = view.findViewById(R.id.tvStreakVal);
        tvXpVal = view.findViewById(R.id.tvXpVal);
        tvLevelVal = view.findViewById(R.id.tvLevelVal);
        tvLevelTitle = view.findViewById(R.id.tvLevelTitle);
        tvScoreLabel = view.findViewById(R.id.tvScoreLabel);
        tvDailyInfo = view.findViewById(R.id.tvDailyInfo);
        btnDaily = view.findViewById(R.id.btnDaily);
        btnAvatar = view.findViewById(R.id.btnAvatar);

        btnAvatar.setOnClickListener(v -> main().switchTab(4));
        view.findViewById(R.id.btnBell).setOnClickListener(v -> showTip());
        view.findViewById(R.id.btnViewProgress).setOnClickListener(v -> main().switchTab(3));

        btnDaily.setOnClickListener(v -> {
            if (DailyChallenge.isDoneToday(prefs())) {
                Toast.makeText(requireContext(),
                        "You already finished today's challenge. Come back tomorrow!",
                        Toast.LENGTH_LONG).show();
            } else {
                Intent i = new Intent(requireContext(), GameActivity.class);
                i.putExtra("daily", true);
                startActivity(i);
            }
        });

        view.findViewById(R.id.tileChat).setOnClickListener(v -> main().switchTab(1));
        view.findViewById(R.id.tileLink).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), LinkDetector.class)));
        view.findViewById(R.id.tileLearn).setOnClickListener(v -> main().switchTab(2));
        view.findViewById(R.id.tileStats).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), StatsActivity.class)));
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        Context c = requireContext();
        SharedPreferences p = prefs();

        int streak = DailyChallenge.getStreak(p);
        int xp = XpManager.getXp(c);
        int level = XpManager.getLevel(c);

        tvStreakVal.setText("🔥 " + streak);
        tvXpVal.setText("⭐ " + String.format(Locale.US, "%,d", xp));
        tvLevelVal.setText("👑 Level " + level);
        tvLevelTitle.setText(XpManager.getTitle(level));
        btnAvatar.setText(ProfileManager.getAvatar(c));

        int score = SecurityScore.calculate(c);
        ring.setScore(score);
        tvScoreLabel.setText(SecurityScore.label(c, score));

        String streakText = streak > 0 ? "🔥 " + streak + "-day streak" : "Start your streak today!";
        if (DailyChallenge.isDoneToday(p)) {
            tvDailyInfo.setText("✅ Completed today (score " + DailyChallenge.getLastScore(p) + ")\n"
                    + streakText);
            btnDaily.setText("DONE FOR TODAY");
            btnDaily.setAlpha(0.5f);
        } else {
            tvDailyInfo.setText("5 questions  •  +50 XP\n" + streakText);
            btnDaily.setText("START CHALLENGE  ➜");
            btnDaily.setAlpha(1f);
        }
    }

    private void showTip() {
        String tip = TIPS[new Random().nextInt(TIPS.length)];
        new AlertDialog.Builder(requireContext())
                .setTitle("💡 Safety tip")
                .setMessage(tip)
                .setPositiveButton("Got it", null)
                .show();
    }

    private SharedPreferences prefs() {
        return requireContext().getSharedPreferences(MainActivity.PREFS, Context.MODE_PRIVATE);
    }

    private MainActivity main() {
        return (MainActivity) requireActivity();
    }
}