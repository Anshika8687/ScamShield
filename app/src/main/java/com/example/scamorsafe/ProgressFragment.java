package com.example.scamorsafe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProgressFragment extends Fragment {

    private TextView tvLevelBig, tvLevelName, tvXpText;
    private ProgressBar barXp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_progress, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvLevelBig = view.findViewById(R.id.tvLevelBig);
        tvLevelName = view.findViewById(R.id.tvLevelName);
        tvXpText = view.findViewById(R.id.tvXpText);
        barXp = view.findViewById(R.id.barXp);

        view.findViewById(R.id.btnOpenStats).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), StatsActivity.class)));
    }

    @Override
    public void onResume() {
        super.onResume();
        Context c = requireContext();
        int level = XpManager.getLevel(c);
        int inLevel = XpManager.getXpInLevel(c);

        tvLevelBig.setText("Level " + level);
        tvLevelName.setText(XpManager.getTitle(level));
        barXp.setMax(XpManager.XP_PER_LEVEL);
        barXp.setProgress(inLevel);
        tvXpText.setText(inLevel + " / " + XpManager.XP_PER_LEVEL + " XP   •   Total "
                + XpManager.getXp(c) + " XP");
    }
}