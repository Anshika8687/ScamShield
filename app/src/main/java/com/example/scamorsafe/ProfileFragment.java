package com.example.scamorsafe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private TextView avatarView, tvName, tvLevelLine;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        avatarView = view.findViewById(R.id.avatarView);
        tvName = view.findViewById(R.id.tvName);
        tvLevelLine = view.findViewById(R.id.tvLevelLine);

        avatarView.setOnClickListener(v -> pickAvatar());
        view.findViewById(R.id.btnEditName).setOnClickListener(v -> editName());
        tvName.setOnClickListener(v -> editName());

        view.findViewById(R.id.rowStats).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), StatsActivity.class)));
        view.findViewById(R.id.rowAchievements).setOnClickListener(v ->
                ((MainActivity) requireActivity()).switchTab(3));
        view.findViewById(R.id.rowSettings).setOnClickListener(v -> showSettings());
        view.findViewById(R.id.rowHelp).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), AwarenessActivity.class)));
        view.findViewById(R.id.rowShare).setOnClickListener(v -> share());
        view.findViewById(R.id.btnReset).setOnClickListener(v -> confirmReset());
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        Context c = requireContext();
        int level = XpManager.getLevel(c);
        avatarView.setText(ProfileManager.getAvatar(c));
        tvName.setText(ProfileManager.getName(c));
        tvLevelLine.setText("Level " + level + "  •  " + XpManager.getTitle(level));
    }

    private void pickAvatar() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Choose your avatar")
                .setItems(ProfileManager.AVATARS, (d, which) -> {
                    ProfileManager.setAvatar(requireContext(), ProfileManager.AVATARS[which]);
                    refresh();
                })
                .show();
    }

    private void editName() {
        final EditText input = new EditText(requireContext());
        input.setSingleLine(true);
        input.setText(ProfileManager.getName(requireContext()));
        input.setSelection(input.getText().length());

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Your name")
                .setPositiveButton("Save", (d, w) -> {
                    String name = input.getText().toString().trim();
                    if (!name.isEmpty()) {
                        ProfileManager.setName(requireContext(), name);
                        refresh();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.setView(input, 48, 16, 48, 0);
        dialog.show();
    }

    private void showSettings() {
        String[] items = {"🎚  Default difficulty", "🗑  Reset all progress"};
        new AlertDialog.Builder(requireContext())
                .setTitle("Settings")
                .setItems(items, (d, which) -> {
                    if (which == 0) pickDifficulty();
                    else confirmReset();
                })
                .show();
    }

    private void pickDifficulty() {
        final SharedPreferences p = requireContext()
                .getSharedPreferences(MainActivity.PREFS, Context.MODE_PRIVATE);
        String[] options = {"🟢 Easy (15s, 5 lives)", "🟡 Medium (10s, 3 lives)", "🔴 Hard (5s, 2 lives)"};
        new AlertDialog.Builder(requireContext())
                .setTitle("Default difficulty")
                .setSingleChoiceItems(options, p.getInt("difficulty", 1), (d, which) -> {
                    p.edit().putInt("difficulty", which).apply();
                    d.dismiss();
                    Toast.makeText(requireContext(), "Difficulty saved", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void share() {
        int score = SecurityScore.calculate(requireContext());
        Intent send = new Intent(Intent.ACTION_SEND);
        send.setType("text/plain");
        send.putExtra(Intent.EXTRA_TEXT,
                "I'm learning to spot scams with ScamShield! My security score is " + score
                        + "/100. Can you beat it?");
        startActivity(Intent.createChooser(send, "Share via"));
    }

    private void confirmReset() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Reset progress?")
                .setMessage("This clears your XP, stats, streak, high scores and profile.")
                .setPositiveButton("Reset", (d, w) -> {
                    SharedPreferences p = requireContext()
                            .getSharedPreferences(MainActivity.PREFS, Context.MODE_PRIVATE);
                    p.edit().clear().putBoolean("onboarding_done", true).apply();
                    requireActivity().recreate();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}