package com.example.scamorsafe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PracticeFragment extends Fragment {

    private SharedPreferences prefs;
    private TextView chipEasy, chipMedium, chipHard, tvDiffInfo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_practice, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = requireContext().getSharedPreferences(MainActivity.PREFS, Context.MODE_PRIVATE);

        chipEasy = view.findViewById(R.id.chipEasy);
        chipMedium = view.findViewById(R.id.chipMedium);
        chipHard = view.findViewById(R.id.chipHard);
        tvDiffInfo = view.findViewById(R.id.tvDiffInfo);

        chipEasy.setOnClickListener(v -> setDifficulty(0));
        chipMedium.setOnClickListener(v -> setDifficulty(1));
        chipHard.setOnClickListener(v -> setDifficulty(2));

        // coloured gradient cards
        style(view.findViewById(R.id.cardBank), 0xFF0E7490, 0xFF1E3A8A);
        style(view.findViewById(R.id.cardArrest), 0xFF7F1D1D, 0xFF4C1D95);
        style(view.findViewById(R.id.cardJob), 0xFF065F46, 0xFF134E4A);
        style(view.findViewById(R.id.cardLink), 0xFF5B21B6, 0xFF3730A3);
        style(view.findViewById(R.id.cardMixed), 0xFF334155, 0xFF1E293B);

        // story order matches ChatStories: 0 bank, 1 digital arrest, 2 fake job
        view.findViewById(R.id.cardBank).setOnClickListener(v -> openStory(0));
        view.findViewById(R.id.cardArrest).setOnClickListener(v -> openStory(1));
        view.findViewById(R.id.cardJob).setOnClickListener(v -> openStory(2));
        view.findViewById(R.id.cardLink).setOnClickListener(v -> openQuiz("URL"));
        view.findViewById(R.id.cardMixed).setOnClickListener(v -> openQuiz(null));
    }

    @Override
    public void onResume() {
        super.onResume();
        updateChips(prefs.getInt("difficulty", 1));
    }

    private void setDifficulty(int d) {
        prefs.edit().putInt("difficulty", d).apply();
        updateChips(d);
    }

    private void updateChips(int d) {
        TextView[] chips = {chipEasy, chipMedium, chipHard};
        for (int i = 0; i < chips.length; i++) {
            chips[i].setBackgroundResource(i == d ? R.drawable.bg_chip_selected : R.drawable.bg_chip);
        }
        String[] info = {
                "⏱ 15 seconds per question   ❤️ 5 lives",
                "⏱ 10 seconds per question   ❤️ 3 lives",
                "⏱ 5 seconds per question   ❤️ 2 lives"
        };
        tvDiffInfo.setText(info[d]);
    }

    private void openStory(int index) {
        Intent i = new Intent(requireContext(), ChatActivity.class);
        i.putExtra("story", index);
        startActivity(i);
    }

    private void openQuiz(@Nullable String type) {
        Intent i = new Intent(requireContext(), GameActivity.class);
        i.putExtra("difficulty", prefs.getInt("difficulty", 1));
        if (type != null) i.putExtra("type", type);
        startActivity(i);
    }

    private void style(View card, int colorStart, int colorEnd) {
        GradientDrawable g = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR, new int[]{colorStart, colorEnd});
        float d = getResources().getDisplayMetrics().density;
        g.setCornerRadius(20 * d);
        g.setStroke((int) d, 0x33FFFFFF);
        card.setBackground(g);
    }
}