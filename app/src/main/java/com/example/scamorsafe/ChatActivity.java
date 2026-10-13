package com.example.scamorsafe;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private ChatStory story;
    private LinearLayout chat, panel;
    private ScrollView scroll;
    private TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.parseColor("#0F172A"));
        setContentView(R.layout.activity_chat);

        List<ChatStory> all = ChatStories.all();
        int index = getIntent().getIntExtra("story", 0);
        if (index < 0 || index >= all.size()) index = 0;
        story = all.get(index);

        chat = findViewById(R.id.chatContainer);
        panel = findViewById(R.id.choicePanel);
        scroll = findViewById(R.id.chatScroll);
        tvStatus = findViewById(R.id.tvStatus);

        ((TextView) findViewById(R.id.tvName)).setText(story.emoji + "  " + story.contact);
        findViewById(R.id.tvBack).setOnClickListener(v -> finish());

        playNode(story.start);
    }

    /** Shows the scammer's messages one by one, then your choices (or the ending). */
    private void playNode(String id) {
        final ChatStory.Node node = story.nodes.get(id);
        panel.removeAllViews();
        tvStatus.setText("typing…");

        long delay = 700;
        for (final String msg : node.messages) {
            handler.postDelayed(() -> addBubble(msg, false), delay);
            delay += 1200;
        }
        handler.postDelayed(() -> {
            tvStatus.setText("online");
            if (node.ending == 0) {
                showChoices(node);
            } else {
                showEnding(node);
            }
        }, delay);
    }

    private void showChoices(ChatStory.Node node) {
        panel.removeAllViews();
        for (final ChatStory.Choice choice : node.choices) {
            Button b = makeButton(choice.text, R.drawable.bg_btn_ghost);
            b.setOnClickListener(v -> {
                panel.removeAllViews();
                addBubble(choice.text, true);
                playNode(choice.next);
            });
            panel.addView(b);
        }
    }

    private void showEnding(ChatStory.Node node) {
        boolean safe = node.ending == 1;
        Stats.recordChat(this, safe);

        TextView card = new TextView(this);
        card.setText((safe ? "✅  YOU STAYED SAFE\n\n" : "💸  YOU GOT SCAMMED\n\n") + node.lesson);
        card.setTextColor(Color.WHITE);
        card.setTextSize(15);
        card.setLineSpacing(dp(3), 1f);
        card.setBackgroundResource(safe ? R.drawable.bg_feedback_good : R.drawable.bg_feedback_bad);
        card.setPadding(dp(16), dp(16), dp(16), dp(16));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, dp(14), 0, dp(6));
        card.setLayoutParams(lp);
        chat.addView(card);
        card.setAlpha(0f);
        card.animate().alpha(1f).setDuration(400).start();
        scrollToBottom();

        panel.removeAllViews();
        Button again = makeButton("🔄  TRY AGAIN WITH DIFFERENT CHOICES", R.drawable.bg_btn_primary);
        again.setOnClickListener(v -> {
            handler.removeCallbacksAndMessages(null);
            chat.removeAllViews();
            playNode(story.start);
        });
        Button back = makeButton("⬅  BACK TO HOME", R.drawable.bg_btn_ghost);
        back.setOnClickListener(v -> finish());
        panel.addView(again);
        panel.addView(back);
    }

    private void addBubble(String text, boolean mine) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(15);
        tv.setLineSpacing(dp(2), 1f);
        tv.setBackgroundResource(mine ? R.drawable.bg_bubble_out : R.drawable.bg_bubble_in);
        tv.setPadding(dp(14), dp(10), dp(14), dp(10));
        tv.setMaxWidth((int) (getResources().getDisplayMetrics().widthPixels * 0.75f));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = mine ? Gravity.END : Gravity.START;
        lp.setMargins(0, dp(4), 0, dp(4));
        tv.setLayoutParams(lp);

        chat.addView(tv);
        tv.setAlpha(0f);
        tv.setTranslationY(dp(14));
        tv.animate().alpha(1f).translationY(0f).setDuration(300).start();
        scrollToBottom();
    }

    private Button makeButton(String text, int backgroundRes) {
        Button b = new Button(this);
        b.setText(text);
        b.setAllCaps(false);
        b.setTextColor(Color.WHITE);
        b.setTextSize(14);
        b.setStateListAnimator(null);
        b.setBackgroundResource(backgroundRes);
        b.setPadding(dp(16), dp(12), dp(16), dp(12));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, dp(5), 0, dp(5));
        b.setLayoutParams(lp);
        return b;
    }

    private void scrollToBottom() {
        scroll.postDelayed(() -> scroll.fullScroll(View.FOCUS_DOWN), 80);
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}