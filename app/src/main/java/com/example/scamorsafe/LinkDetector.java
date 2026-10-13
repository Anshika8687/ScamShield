package com.example.scamorsafe;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LinkDetector extends AppCompatActivity {

    private EditText etInput;
    private ScrollView scroll;
    private View resultContainer, cardVerdict;
    private TextView tvVerdictEmoji, tvVerdict, tvVerdictSub, tvRiskPct, tvBreakdown,
            tvFindings, tvAdvice, tvXp;
    private ProgressBar barRisk;
    private String lastChecked = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.parseColor("#0B0720"));
        setContentView(R.layout.activity_link_detector);

        etInput = findViewById(R.id.etInput);
        scroll = findViewById(R.id.scroll);
        resultContainer = findViewById(R.id.resultContainer);
        cardVerdict = findViewById(R.id.cardVerdict);
        tvVerdictEmoji = findViewById(R.id.tvVerdictEmoji);
        tvVerdict = findViewById(R.id.tvVerdict);
        tvVerdictSub = findViewById(R.id.tvVerdictSub);
        tvRiskPct = findViewById(R.id.tvRiskPct);
        tvBreakdown = findViewById(R.id.tvBreakdown);
        tvFindings = findViewById(R.id.tvFindings);
        tvAdvice = findViewById(R.id.tvAdvice);
        tvXp = findViewById(R.id.tvXp);
        barRisk = findViewById(R.id.barRisk);

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnPaste).setOnClickListener(v -> pasteFromClipboard());
        findViewById(R.id.btnClear).setOnClickListener(v -> {
            etInput.setText("");
            resultContainer.setVisibility(View.GONE);
        });
        findViewById(R.id.btnAnalyze).setOnClickListener(v -> analyze());

        setExample(R.id.ex1, "http://sbi-kyc-update.xyz/login");
        setExample(R.id.ex2, "https://www.sbi.co.in");
        setExample(R.id.ex3, "http://bit.ly/free-recharge-99");
        setExample(R.id.ex4, "https://sbi.co.in.kyc-update.info/login");
    }

    private void setExample(int viewId, String link) {
        findViewById(viewId).setOnClickListener(v -> {
            etInput.setText(link);
            etInput.setSelection(etInput.getText().length());
            analyze();
        });
    }

    private void pasteFromClipboard() {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null && cm.hasPrimaryClip()) {
            ClipData clip = cm.getPrimaryClip();
            if (clip != null && clip.getItemCount() > 0) {
                CharSequence text = clip.getItemAt(0).coerceToText(this);
                if (text != null && text.toString().trim().length() > 0) {
                    etInput.setText(text.toString().trim());
                    etInput.setSelection(etInput.getText().length());
                    return;
                }
            }
        }
        Toast.makeText(this, "Clipboard is empty", Toast.LENGTH_SHORT).show();
    }

    private void analyze() {
        String text = etInput.getText().toString().trim();
        if (text.isEmpty()) {
            Toast.makeText(this, "Paste or type a link first", Toast.LENGTH_SHORT).show();
            return;
        }

        LinkAnalyzer.Result r = LinkAnalyzer.analyze(text);
        if (!r.valid) {
            Toast.makeText(this, "That doesn't look like a link. Example: https://example.com",
                    Toast.LENGTH_LONG).show();
            return;
        }

        hideKeyboard();
        showResult(r);

        // +5 XP and stats only for a link that was not just checked
        boolean fresh = !text.equals(lastChecked);
        lastChecked = text;
        if (fresh) {
            XpManager.addXp(this, 5);
            SharedPreferences p = getSharedPreferences(MainActivity.PREFS, MODE_PRIVATE);
            p.edit()
                    .putInt("stat_links_checked", p.getInt("stat_links_checked", 0) + 1)
                    .putInt("stat_links_risky", p.getInt("stat_links_risky", 0) + (r.risk() >= 30 ? 1 : 0))
                    .apply();
        }
        tvXp.setVisibility(fresh ? View.VISIBLE : View.GONE);
    }

    private void showResult(LinkAnalyzer.Result r) {
        int level = r.level();
        int color;
        String emoji, title, sub, advice;

        if (level == 2) {
            color = 0xFFEF4444;
            emoji = "🚨";
            title = "HIGH RISK";
            sub = "This link is very likely a phishing attempt. Do not open it.";
            advice = "• Do NOT open the link or enter any details.\n"
                    + "• Delete the message and block the sender.\n"
                    + "• If you already entered details or paid, call your bank and the Cyber Crime Helpline 1930 right away.\n"
                    + "• Report it at cybercrime.gov.in.";
        } else if (level == 1) {
            color = 0xFFF59E0B;
            emoji = "⚠️";
            title = "SUSPICIOUS";
            sub = "Several warning signs found. Be very careful with this link.";
            advice = "• Do not enter passwords, OTP or card details.\n"
                    + "• Do not trust the link. Open the company's official app or type its website yourself.\n"
                    + "• Ask the sender through another channel if you are unsure.";
        } else {
            color = 0xFF22C55E;
            emoji = "✅";
            title = r.trusted ? "LOOKS SAFE" : "NO MAJOR RED FLAGS";
            sub = r.trusted ? "This matches a known trusted domain."
                    : "No common phishing signs found, but no tool is perfect.";
            advice = "• Even on a safe link, never share your OTP, PIN or CVV.\n"
                    + "• Check that the address is spelled exactly right.\n"
                    + "• When in doubt, type the website address yourself.";
        }

        // verdict card
        styleCard(cardVerdict, color);
        tvVerdictEmoji.setText(emoji);
        tvVerdict.setText(title);
        tvVerdict.setTextColor(color);
        tvVerdictSub.setText(sub);
        tvRiskPct.setText("Risk: " + r.risk() + "%");
        tvRiskPct.setTextColor(color);
        barRisk.setMax(100);
        barRisk.setProgress(r.risk());
        barRisk.setProgressTintList(ColorStateList.valueOf(color));
        barRisk.setProgressBackgroundTintList(ColorStateList.valueOf(0x33FFFFFF));

        // breakdown
        String connection;
        if (r.protocol.equals("https")) connection = "🔒 HTTPS (encrypted)";
        else if (r.protocol.equals("http")) connection = "🔓 HTTP (not secure)";
        else connection = "❔ Not specified";

        tvBreakdown.setText(
                "🌐 Address shown:  " + r.host
                        + "\n🏷 Real domain:  " + (r.isIp ? r.host + " (IP address)" : r.realDomain)
                        + "\n🔌 Connection:  " + connection
                        + "\n🛡 Domain status:  " + (r.trusted ? "Known trusted site" : "Not on the trusted list"));

        // findings
        SpannableStringBuilder sb = new SpannableStringBuilder();
        for (LinkAnalyzer.Finding f : r.goods) addFinding(sb, "✅ ", f);
        for (LinkAnalyzer.Finding f : r.flags) addFinding(sb, "🚩 ", f);
        if (r.flags.isEmpty()) {
            sb.append("No red flags were found in this link.");
        }
        tvFindings.setText(sb);

        tvAdvice.setText(advice);

        // show with a small animation
        resultContainer.setVisibility(View.VISIBLE);
        resultContainer.setAlpha(0f);
        resultContainer.animate().alpha(1f).setDuration(350).start();
        scroll.post(() -> scroll.smoothScrollTo(0, resultContainer.getTop()));
    }

    private void addFinding(SpannableStringBuilder sb, String icon, LinkAnalyzer.Finding f) {
        int start = sb.length();
        sb.append(icon).append(f.title);
        sb.setSpan(new StyleSpan(Typeface.BOLD), start, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.append("\n").append(f.detail).append("\n\n");
    }

    private void styleCard(View card, int color) {
        GradientDrawable g = new GradientDrawable();
        g.setColor((color & 0x00FFFFFF) | 0x33000000);
        g.setStroke((int) dp(1.5f), color);
        g.setCornerRadius(dp(20));
        card.setBackground(g);
    }

    private float dp(float v) {
        return v * getResources().getDisplayMetrics().density;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(etInput.getWindowToken(), 0);
    }
}