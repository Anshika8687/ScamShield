package com.example.scamorsafe;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {

    public static final String PREFS = "scamorsafe_prefs";

    private final int[] navIds = {
            R.id.navHome, R.id.navPractice, R.id.navLearn, R.id.navProgress, R.id.navProfile
    };
    private int current = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.parseColor("#0B0720"));
        setContentView(R.layout.activity_main);

        for (int i = 0; i < navIds.length; i++) {
            final int index = i;
            findViewById(navIds[i]).setOnClickListener(v -> {
                if (current != index) switchTab(index);
            });
        }

        int startTab = getIntent().getIntExtra("tab", 0);
        if (savedInstanceState != null) {
            startTab = savedInstanceState.getInt("tab", 0);
        }
        switchTab(startTab);

        // Back goes to Home first, then exits
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (current != 0) {
                    switchTab(0);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    /** Fragments call this to jump to another tab. 0 Home, 1 Practice, 2 Learn, 3 Progress, 4 Profile */
    public void switchTab(int index) {
        current = index;
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragmentContainer, createFragment(index))
                .commit();
        highlight(index);
    }

    private Fragment createFragment(int index) {
        switch (index) {
            case 1:
                return new PracticeFragment();
            case 2:
                return new LearnFragment();
            case 3:
                return new ProgressFragment();
            case 4:
                return new ProfileFragment();
            default:
                return new HomeFragment();
        }
    }

    private void highlight(int selected) {
        for (int i = 0; i < navIds.length; i++) {
            ViewGroup item = findViewById(navIds[i]);
            boolean on = i == selected;
            item.getChildAt(0).setAlpha(on ? 1f : 0.45f);
            TextView label = (TextView) item.getChildAt(1);
            label.setTextColor(on ? Color.WHITE : getResources().getColor(R.color.nav_idle));
            label.setTypeface(null, on ? Typeface.BOLD : Typeface.NORMAL);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tab", current);
    }
}