package com.example.scamorsafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    private static final int TOTAL_QUESTIONS = 10;

    // index 0 = Easy, 1 = Medium, 2 = Hard
    private static final long[] TIME_MS = {15000, 10000, 5000};
    private static final int[] LIVES = {5, 3, 2};

    private List<Question> questions;
    private int index = 0;
    private int score = 0;
    private int difficulty = 1;
    private boolean daily = false;
    private long timePerQuestionMs;
    private int maxLives;
    private int lives;
    private int streak = 0;
    private int bestStreak = 0;
    private int correctCount = 0;
    private boolean answered = false;
    private final ArrayList<String> mistakes = new ArrayList<>();
    private CountDownTimer timer;

    private TextView tvScore, tvLives, tvTimer, tvProgress, tvStreak, tvType, tvContent, tvFeedback;
    private ProgressBar progressTimer;
    private Button btnScam, btnSafe, btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.parseColor("#0F172A"));
        setContentView(R.layout.activity_game);

        daily = getIntent().getBooleanExtra("daily", false);
        difficulty = daily ? 1 : getIntent().getIntExtra("difficulty", 1);
        if (difficulty < 0 || difficulty > 2) difficulty = 1;
        timePerQuestionMs = TIME_MS[difficulty];
        maxLives = LIVES[difficulty];
        lives = maxLives;

        tvScore = findViewById(R.id.tvScore);
        tvLives = findViewById(R.id.tvLives);
        tvTimer = findViewById(R.id.tvTimer);
        tvProgress = findViewById(R.id.tvProgress);
        tvStreak = findViewById(R.id.tvStreak);
        tvType = findViewById(R.id.tvType);
        tvContent = findViewById(R.id.tvContent);
        tvFeedback = findViewById(R.id.tvFeedback);
        progressTimer = findViewById(R.id.progressTimer);
        btnScam = findViewById(R.id.btnScam);
        btnSafe = findViewById(R.id.btnSafe);
        btnNext = findViewById(R.id.btnNext);

        progressTimer.setMax((int) (timePerQuestionMs / 100));

        questions = daily ? DailyChallenge.getQuestions() : QuestionBank.getRandom(TOTAL_QUESTIONS);

        btnScam.setOnClickListener(v -> onAnswer(true, false));
        btnSafe.setOnClickListener(v -> onAnswer(false, false));
        btnNext.setOnClickListener(v -> {
            if (lives == 0 || index + 1 >= questions.size()) {
                finishGame();
            } else {
                index++;
                showQuestion();
            }
        });

        showQuestion();
    }

    private void showQuestion() {
        answered = false;
        Question q = questions.get(index);

        tvProgress.setText((daily ? "📅 Daily  •  " : "")
                + "Question " + (index + 1) + " of " + questions.size());
        tvType.setText(typeLabel(q.type));
        tvContent.setText(q.content);
        tvContent.setTypeface(q.type.equals("URL") ? Typeface.MONOSPACE : Typeface.DEFAULT);
        tvFeedback.setVisibility(View.GONE);
        btnNext.setVisibility(View.GONE);
        setAnswerButtons(true);
        updateHud();

        // small fade-in for the question card
        View card = findViewById(R.id.cardQuestion);
        card.setAlpha(0f);
        card.setTranslationY(40f);
        card.animate().alpha(1f).translationY(0f).setDuration(350).start();

        if (timer != null) timer.cancel();
        progressTimer.setProgress(progressTimer.getMax());
        timer = new CountDownTimer(timePerQuestionMs, 100) {
            @Override
            public void onTick(long ms) {
                progressTimer.setProgress((int) (ms / 100));
                tvTimer.setText("⏱ " + ((ms + 999) / 1000) + "s");
            }

            @Override
            public void onFinish() {
                progressTimer.setProgress(0);
                onAnswer(false, true);
            }
        }.start();
    }

    private int getMultiplier() {
        if (streak >= 5) return 3;
        if (streak >= 3) return 2;
        return 1;
    }

    private void onAnswer(boolean userSaysScam, boolean timedOut) {
        if (answered) return;
        answered = true;
        if (timer != null) timer.cancel();

        Question q = questions.get(index);
        boolean correct = !timedOut && (userSaysScam == q.scam);
        String answerText = q.scam ? "This was a SCAM. " : "This was SAFE. ";

        Stats.recordAnswer(this, q.type, correct);

        if (correct) {
            correctCount++;
            streak++;
            if (streak > bestStreak) bestStreak = streak;

            int multiplier = getMultiplier();
            int points = 10 * multiplier;
            score += points;

            String bonus = multiplier > 1 ? "  (x" + multiplier + " combo!)" : "";
            tvFeedback.setText("✅ Correct!  +" + points + bonus + "\n" + answerText + q.reason);
            tvFeedback.setBackgroundResource(R.drawable.bg_feedback_good);
        } else {
            lives--;
            streak = 0;
            String prefix = timedOut ? "⏰ Time's up!\n" : "❌ Wrong!\n";
            tvFeedback.setText(prefix + answerText + q.reason);
            tvFeedback.setBackgroundResource(R.drawable.bg_feedback_bad);
            mistakes.add("[" + q.type + "] " + q.content + "\n=> " + answerText + q.reason);
        }

        tvTimer.setText("");
        tvFeedback.setVisibility(View.VISIBLE);
        tvFeedback.setAlpha(0f);
        tvFeedback.animate().alpha(1f).setDuration(300).start();

        setAnswerButtons(false);
        btnNext.setText((lives == 0 || index + 1 >= questions.size()) ? "SEE RESULT  ➜" : "NEXT  ➜");
        btnNext.setVisibility(View.VISIBLE);
        updateHud();

        // little pop animation on the streak badge when it grows
        if (correct && streak >= 2) {
            tvStreak.setScaleX(1.35f);
            tvStreak.setScaleY(1.35f);
            tvStreak.animate().scaleX(1f).scaleY(1f).setDuration(350).start();
        }
    }

    private void setAnswerButtons(boolean enabled) {
        btnScam.setEnabled(enabled);
        btnSafe.setEnabled(enabled);
        float alpha = enabled ? 1f : 0.35f;
        btnScam.setAlpha(alpha);
        btnSafe.setAlpha(alpha);
    }

    private String typeLabel(String type) {
        switch (type) {
            case "SMS":
                return "💬  SMS MESSAGE";
            case "EMAIL":
                return "📧  EMAIL";
            default:
                return "🔗  WEB LINK";
        }
    }

    private void updateHud() {
        tvScore.setText("⭐ " + score);

        StringBuilder hearts = new StringBuilder();
        for (int i = 0; i < maxLives; i++) hearts.append(i < lives ? "❤️" : "🖤");
        tvLives.setText(hearts.toString());

        if (streak >= 2) {
            int m = getMultiplier();
            tvStreak.setText("🔥 Streak " + streak + (m > 1 ? "  x" + m : ""));
            tvStreak.setVisibility(View.VISIBLE);
        } else {
            tvStreak.setVisibility(View.INVISIBLE);
        }
    }

    private void finishGame() {
        Stats.recordGame(this);

        Intent i = new Intent(this, ResultActivity.class);
        if (daily) {
            SharedPreferences prefs = getSharedPreferences(MainActivity.PREFS, MODE_PRIVATE);
            int dailyStreak = DailyChallenge.complete(prefs, score);
            i.putExtra("daily", true);
            i.putExtra("dailyStreak", dailyStreak);
        }
        i.putExtra("score", score);
        i.putExtra("played", index + 1);
        i.putExtra("correct", correctCount);
        i.putExtra("bestStreak", bestStreak);
        i.putExtra("difficulty", difficulty);
        i.putStringArrayListExtra("mistakes", mistakes);
        startActivity(i);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
    }
}