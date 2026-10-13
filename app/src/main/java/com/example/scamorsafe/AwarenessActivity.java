package com.example.scamorsafe;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AwarenessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.parseColor("#0F172A"));
        setContentView(R.layout.activity_awareness);

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());

        findViewById(R.id.btnCall).setOnClickListener(v ->
                open(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:1930"))));

        findViewById(R.id.btnReport).setOnClickListener(v ->
                open(new Intent(Intent.ACTION_VIEW, Uri.parse("https://cybercrime.gov.in"))));
    }

    private void open(Intent intent) {
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Could not open on this device", Toast.LENGTH_SHORT).show();
        }
    }
}